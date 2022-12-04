package com.springvue.app.server.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.springvue.app.common.constants.enums.MsgType;
import com.springvue.app.common.mail.MailUtils;
import com.springvue.app.common.utils.ExecutorUtils;
import com.springvue.app.dao.model.po.SysEmailPo;
import com.springvue.app.dao.model.vo.SysEmailVo;
import com.springvue.app.dao.service.impl.SysEmailServiceImpl;
import com.springvue.app.server.config.ImapProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "任务模块")
@RestController("/task")
public class TaskController {

    @Autowired
    private ImapProperties imapProperties;

    @Autowired
    private SysEmailServiceImpl sysEmailService;

    @ApiOperation(value = "获取邮件")
    @PostMapping("/receiveEmail")
    @Scheduled(fixedDelay = 5 * 60 * 1000L)
    public void receiveEmail() {
        // 多线程执行
        ExecutorUtils.lock((Void) -> {
            // 接收邮件
            MailUtils.handleReceived(imapProperties
                    , mimeMessage -> MailUtils.getDate(mimeMessage).getTime() > System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L)
                    , imapMail -> {
                        if (sysEmailService.list(new LambdaQueryWrapper<SysEmailPo>().eq(SysEmailPo::getMessageId, imapMail.getMessageId()))
                                .stream().findFirst().orElse(null) == null) {
                            SysEmailVo sysEmailVo = new SysEmailVo();
                            BeanUtils.copyProperties(imapMail, sysEmailVo);
                            sysEmailVo.setMsgType(MsgType.EMAIL_RECEIVE.getCode());
                            sysEmailVo.setReceivers(CollectionUtil.newArrayList(imapMail.getReceiver()));
                            sysEmailService.save(sysEmailVo);
                        } else if (CollectionUtil.isNotEmpty(imapMail.getAttachments())) {
                            for (String attachment : imapMail.getAttachments()) {
                                FileUtil.del(attachment);
                            }
                        }
                    });
        });
    }
}
