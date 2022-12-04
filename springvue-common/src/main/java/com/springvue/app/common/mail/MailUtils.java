package com.springvue.app.common.mail;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import com.springvue.app.common.utils.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.CollectionUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 邮件工具类
 */
@Slf4j
public class MailUtils {

    /**
     * 发送邮件
     */
    public static void send(JavaMailSender javaMailSender, StmpMail mail) throws Exception {
        // 检验入参
        ValidateUtils.entity(mail);
        // 组装实体
        MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
        mimeMessageHelper.setFrom(mail.getSender());
        int size = mail.getReceivers().size();
        mimeMessageHelper.setTo(mail.getReceivers().toArray(new String[size]));
        mimeMessageHelper.setSubject(mail.getTitle());
        mimeMessageHelper.setText(mail.getContent());
        // 添加附件
        if (!CollectionUtils.isEmpty(mail.getAttachments())) {
            for (String fileName : mail.getAttachments()) {
                if (FileUtil.exist(fileName)) {
                    FileSystemResource fileSystemResource = new FileSystemResource(FileUtil.file(fileName));
                    mimeMessageHelper.addAttachment(FileUtil.file(fileName).getName(), fileSystemResource);
                }
            }
        }
        javaMailSender.send(mimeMailMessage);
    }

    /**
     * 获取邮箱连接
     */
    public static Store getStore(ImapMailProperties properties) throws Exception {
        // 检验入参
        ValidateUtils.entity(properties);
        // 配置参数
        Properties props = new Properties();
        // 是否开启SSL
        if (properties.getIsSsl()) {
            props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", properties.getHost());
        props.setProperty("mail.imap.port", properties.getPort().toString());
        // 创建会话
        Session session = Session.getInstance(props);
        Store store = session.getStore("imap");
        // 连接邮件服务器
        store.connect(properties.getHost(),
                properties.getPort(),
                properties.getUsername(),
                properties.getPassword());
        return store;
    }

    /**
     * 移动垃圾邮件到收件箱
     */
    public static void mvTrash(ImapMailProperties properties) {
        // 检验入参
        Folder folder = null;
        Folder trashFolder = null;
        try (Store store = MailUtils.getStore(properties)) {
            // 获得收件箱
            folder = store.getFolder(properties.getInboxName());
            if (folder != null && StringUtils.isNotBlank(properties.getTrashFolderName())) {
                // 获取收件箱文件夹
                trashFolder = Arrays.stream(store.getDefaultFolder().list())
                        .filter(o -> o.getFullName().equals(properties.getTrashFolderName()))
                        .findFirst().orElse(null);
                if (trashFolder != null) {
                    // 以读写模式打开收件箱
                    trashFolder.open(Folder.READ_WRITE);
                    // 获取邮件数量
                    log.info("邮箱[{}]中共有垃圾邮件[{}]封!", properties.getUsername(), trashFolder.getMessageCount());
                    Message[] messages = trashFolder.getMessages();
                    // 过滤邮件
                    if (messages != null && messages.length >= 1) {
                        for (Message o : messages) {
                            trashFolder.copyMessages(new Message[]{o}, folder);
                            o.setFlag(Flags.Flag.DELETED, true);
                        }
                    }
                } else {
                    log.info("邮箱[{}]找不到垃圾箱文件夹[{}]", properties.getUsername(), properties.getTrashFolderName());
                }
            }
        } catch (Exception e) {
            log.error("移动垃圾邮件到收件箱异常, 原因: {}", e.getMessage(), e);
        } finally {
            try {
                if (trashFolder != null && trashFolder.isOpen()) {
                    trashFolder.close(true);
                }
                if (folder != null && folder.isOpen()) {
                    folder.close(true);
                }
            } catch (Exception e) {
                log.error("关闭收件箱连接异常, 原因: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 处理收件箱邮件
     */
    public static void handleReceived(ImapMailProperties properties, Predicate<MimeMessage> predicate, Consumer<ImapMail> consumer) {
        List<Message> inboxMessage = new ArrayList<>();
        Folder folder = null;
        Folder historyFolder = null;
        try (Store store = MailUtils.getStore(properties)) {
            // 获得收件箱
            folder = store.getFolder(properties.getInboxName());
            if (folder != null && StringUtils.isNotBlank(properties.getHistoryFolderName())) {
                historyFolder = Arrays.stream(store.getDefaultFolder().list())
                        .filter(o -> o.getFullName().equals(properties.getHistoryFolderName()))
                        .findFirst().orElse(null);
                // 以读写模式打开收件箱
                folder.open(Folder.READ_WRITE);
                // 获取邮件数量
                log.info("收件箱[{}]中共[{}]封邮件!", properties.getUsername(), folder.getMessageCount());
                Integer start = 1;
                Integer handleSize = 0;
                // 获取未处理的邮件入库
                handleSize = MailUtils.getMessage(properties, inboxMessage, folder, start, handleSize, predicate, consumer);
                // 收件箱超过30封后移动到历史邮箱
                if (historyFolder != null && properties.getInBoxCount() != null && !inboxMessage.isEmpty()) {
                    // 按时间排序
                    List<Message> sortMsgList = inboxMessage.stream().sorted((o1, o2) -> {
                        MimeMessage msg1 = (MimeMessage) o1;
                        MimeMessage msg2 = (MimeMessage) o2;
                        return MailUtils.getDate(msg2).compareTo(MailUtils.getDate(msg1));
                    }).collect(Collectors.toList());
                    // 复制移动邮件
                    for (int i = 0; i < sortMsgList.size(); i++) {
                        if (i > properties.getInBoxCount()) {
                            folder.copyMessages(new Message[]{sortMsgList.get(i)}, historyFolder);
                            sortMsgList.get(i).setFlag(Flags.Flag.DELETED, true);
                        }
                    }
                }
                log.info("收件箱处理结束, 共[{}]封邮件, 符合条件且处理[{}]封!", folder.getMessageCount(), handleSize);
            }
        } catch (Exception e) {
            log.error("处理收件箱邮件异常, 原因: {}", e.getMessage(), e);
        } finally {
            try {
                if (historyFolder != null && historyFolder.isOpen()) {
                    historyFolder.close(true);
                }
                if (folder != null && folder.isOpen()) {
                    folder.close(true);
                }
            } catch (Exception e) {
                log.error("关闭收件箱连接异常, 原因: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 获取未处理的邮件入库
     */
    private static Integer getMessage(ImapMailProperties properties,
                                      List<Message> inboxMessage, Folder folder,
                                      Integer start,
                                      Integer handleSize,
                                      Predicate<MimeMessage> predicate,
                                      Consumer<ImapMail> consumer) throws Exception {
        int end = start + properties.getBatchCount() - 1;
        Message[] messages = folder.getMessages(start, Math.min(end, folder.getMessageCount()));
        // 过滤邮件
        if (messages != null && messages.length >= 1) {
            // 收集所有邮件信息
            inboxMessage.addAll(Arrays.asList(messages));
            // 过滤发送时间大于数据库邮件
            List<MimeMessage> matchMsgList = new ArrayList<>();
            for (Message o : messages) {
                MimeMessage msg = (MimeMessage) o;
                // 自定义判断条件
                if (msg.getMessageID() != null && predicate != null && predicate.test(msg)) {
                    matchMsgList.add(msg);
                }
            }
            // 处理符合条件的邮件
            if (!matchMsgList.isEmpty()) {
                for (MimeMessage message : matchMsgList) {
                    try {
                        // 初始化邮件信息
                        ImapMail mail = MailUtils.initEmail(properties, message);
                        log.info("当前处理邮件标题[{}], 邮件时间[{}]", mail.getTitle(), DatePattern.NORM_DATETIME_FORMAT.format(mail.getCreateTime()));
                        // 自定义处理逻辑
                        if (consumer != null) {
                            consumer.accept(mail);
                        }
                    } catch (Exception e) {
                        log.error("处理邮箱[{}]邮件[{}]异常, 继续执行下一封, 原因: {}", getSender(message), MimeUtility.decodeText(message.getSubject()), e.getMessage(), e);
                    }
                }
                handleSize += matchMsgList.size();
                log.info("循环处理邮件[{}]封, 符合条件邮件[{}]封!", messages.length, matchMsgList.size());
            }
            // 如果获取数少于收件箱数继续循环
            if (end < folder.getMessageCount()) {
                handleSize = MailUtils.getMessage(properties, inboxMessage, folder, start + properties.getBatchCount(), handleSize, predicate, consumer);
            }
        }
        return handleSize;
    }

    /**
     * 获取邮件发送人
     */
    public static String getSender(MimeMessage msg) throws Exception {
        return ((InternetAddress) msg.getFrom()[0]).getAddress();
    }

    /**
     * 获取邮件日期
     */
    public static Date getDate(MimeMessage mimeMessage) {
        Date date = new Date();
        if (mimeMessage != null) {
            try {
                date = mimeMessage.getSentDate();
                if (date == null) {
                    date = mimeMessage.getReceivedDate();
                    if (date == null) {
                        String receivedDate = "";
                        // 获取Received的信息
                        String[] lines = new String[]{"Received"};
                        Enumeration<String> temp = mimeMessage.getMatchingHeaderLines(lines);
                        String received = temp.nextElement();
                        if (received.split(";").length > 1) {
                            // 根据Received里提取的时间信息格式决定（ps:必须保持一致）
                            SimpleDateFormat sdf = new SimpleDateFormat(DatePattern.HTTP_DATETIME_PATTERN, Locale.US);
                            receivedDate = received.split(";")[1].replaceAll("[\r\n\t]", "");
                            // 将提取的时间转成date
                            date = sdf.parse(receivedDate);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("获取邮件时间异常, 原因: {}", e.getMessage(), e);
            }
        }
        return date;
    }

    /**
     * 初始化邮件信息
     */
    private static ImapMail initEmail(ImapMailProperties properties, MimeMessage msg) throws Exception {
        // 装载邮件信息
        String sender = getSender(msg);
        ImapMail mail = new ImapMail();
        BeanUtils.copyProperties(properties, mail);
        mail.setMessageId(msg.getMessageID());
        mail.setSender(sender);
        mail.setReceiver(mail.getUsername());
        mail.setTitle(MimeUtility.decodeText(msg.getSubject()));
        mail.setContent(getMailTextContent(msg));
        mail.setCreateTime(MailUtils.getDate(msg));
        if (StringUtils.isNotBlank(properties.getAttachmentDir()) && isContainAttachment(msg)) {
            List<String> attachments = new ArrayList<>();
            String dir = properties.getAttachmentDir() + "/" + DatePattern.NORM_DATE_FORMAT.format(MailUtils.getDate(msg)) + "/" + sender.replace(".", "_") + "/";
            // 保存附件
            saveAttachment(mail, msg, dir, attachments);
            mail.setAttachments(attachments);
        }
        return mail;
    }

    /**
     * 判断邮件中是否包含附件
     *
     * @param part 邮件内容
     * @return 邮件中存在附件返回true，不存在返回false
     */
    public static boolean isContainAttachment(Part part) throws Exception {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("application")) {
                        flag = true;
                    }
                    if (contentType.contains("name")) {
                        flag = true;
                    }
                }
                if (flag) {
                    break;
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 获得邮件文本内容
     */
    private static String getMailTextContent(MimeMessage msg) throws Exception {
        StringBuffer content = new StringBuffer(30);
        getMailTextContent(msg, content);
        return content.toString();
    }

    /**
     * 获得邮件文本内容
     *
     * @param part    邮件体
     * @param content 存储邮件文本内容的字符串
     */
    private static void getMailTextContent(Part part, StringBuffer content) throws Exception {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            if (part.getContent() != null && part.getContent() instanceof String) {
                content.append(part.getContent().toString());
            }
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, content);
            }
        }
    }

    /**
     * 保存附件
     *
     * @param part    邮件中多个组合体中的其中一个组合体
     * @param destDir 附件保存目录
     */
    private static void saveAttachment(ImapMail mail, Part part, String destDir, List<String> fileNames) throws Exception {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    String fileName = saveFile(mail, bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
                    fileNames.add(destDir + fileName);
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(mail, bodyPart, destDir, fileNames);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("name") || contentType.contains("application")) {
                        String fileName = saveFile(mail, bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
                        fileNames.add(destDir + fileName);
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment(mail, (Part) part.getContent(), destDir, fileNames);
        }
    }

    /**
     * 读取输入流中的数据保存至指定目录
     *
     * @param mail     邮件信息
     * @param is       输入流
     * @param destDir  文件存储目录
     * @param fileName 文件名
     */
    private static String saveFile(ImapMail mail, InputStream is, String destDir, String fileName) {
        FileUtil.mkdir(destDir);
        String absoluteFileName = destDir + fileName;
        // 如果已有相同的文件
        if (FileUtil.exist(absoluteFileName)) {
            String suffix = FileUtil.getSuffix(fileName);
            if (StringUtils.isNotBlank(suffix)) {
                fileName = fileName.replace(suffix, System.currentTimeMillis() + "." + suffix);
            } else {
                fileName = fileName + System.currentTimeMillis();
            }
        }
        log.info("保存邮箱[{}]邮件[{}]附件: {}", mail.getSender(), mail.getTitle(), destDir + fileName);
        FileUtil.writeFromStream(is, destDir + fileName);
        return fileName;
    }

    /**
     * 文本解码
     */
    private static String decodeText(String encodeText) throws Exception {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }
}

