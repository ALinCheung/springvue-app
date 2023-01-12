package com.springvue.app.server.config;

import com.springvue.app.common.mail.ImapMailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "imap")
@Component
public class ImapProperties extends ImapMailProperties {
}
