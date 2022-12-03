package com.springvue.app.server.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.springvue.app.common.constants.enums.MsgType;
import com.springvue.app.common.mail.ImapMailProperties;
import com.springvue.app.common.mail.MailUtils;
import com.springvue.app.common.utils.ExecutorUtils;
import com.springvue.app.dao.model.vo.SysEmailVo;
import com.springvue.app.dao.service.impl.SysEmailServiceImpl;
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
    private SysEmailServiceImpl sysEmailService;

    @ApiOperation(value = "获取邮件")
    @PostMapping("/receiveEmail")
    @Scheduled(fixedDelay = 5 * 60 * 1000L)
    public void receiveEmail() {
        // 多线程执行
        ExecutorUtils.lock((Void) -> {
            // 接收邮件配置
            ImapMailProperties properties = new ImapMailProperties();
            MailUtils.handleReceived(properties
                    , mimeMessage -> MailUtils.getDate(mimeMessage).getTime() > System.currentTimeMillis() - (18 * 24 * 60 * 60 * 1000L)
                    , imapMail -> {
                        SysEmailVo sysEmailVo = new SysEmailVo();
                        BeanUtils.copyProperties(imapMail, sysEmailVo);
                        sysEmailVo.setMsgType(MsgType.EMAIL_RECEIVE.getCode());
                        sysEmailVo.setReceivers(CollectionUtil.newArrayList(imapMail.getReceiver()));
                        sysEmailService.save(sysEmailVo);
                    });
        });
    }
}
