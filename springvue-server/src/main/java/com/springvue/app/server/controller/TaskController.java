package com.springvue.app.server.controller;

import com.springvue.app.common.mail.ImapMailProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "任务模块")
@RestController("/task")
public class TaskController {

    @ApiOperation(value = "获取邮件")
    @PostMapping("/receiveEmail")
    @Scheduled(fixedDelay = 5 * 60 * 1000L)
    public void receiveEmail() {
        ImapMailProperties properties = new ImapMailProperties();
    }
}
