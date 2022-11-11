package com.springvue.app.common.ftp.component;

import com.springvue.app.common.ftp.config.FtpConfig;
import com.springvue.app.common.ftp.properties.FtpPoolProperties;
import com.springvue.app.common.ftp.properties.FtpProperties;
import com.springvue.app.common.ftp.properties.FtpServerProperties;
import com.springvue.app.common.ftp.session.FtpSession;
import com.springvue.app.common.ftp.util.FtpUtils;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties({FtpProperties.class})
public class FtpAutoConfiguration {

    private final FtpProperties ftpProperties;

    @Autowired
    public FtpAutoConfiguration(FtpProperties ftpProperties) {
        this.ftpProperties = ftpProperties;
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "ftp.servers[0]",
            name = {"host"}
    )
    public KeyedObjectPool<String, FtpSession> ftpPool() {
        // 获取配置信息
        List<FtpServerProperties> serverProperties = this.ftpProperties.getServers();
        FtpPoolProperties poolProperties = this.ftpProperties.getPool();
        if (poolProperties == null) {
            poolProperties = new FtpPoolProperties();
        }
        // 创建连接池配置
        GenericKeyedObjectPoolConfig keyedObjectPoolConfig = new GenericKeyedObjectPoolConfig();
        BeanUtils.copyProperties(poolProperties, keyedObjectPoolConfig);
        // 创建连接池
        Map<String, FtpConfig> configMap = serverProperties.stream().filter((s) -> s.getKey() != null).collect(Collectors.toMap(FtpServerProperties::getKey, FtpUtils::toConfig));
        return new GenericKeyedObjectPool<String, FtpSession>(new FtpFactory(configMap), keyedObjectPoolConfig);
    }

    @Bean
    @ConditionalOnBean(
            name = {"ftpPool"}
    )
    public FtpTemplate ftpTemplate(@Autowired KeyedObjectPool<String, FtpSession> ftpPool) {
        return new FtpTemplate(ftpPool);
    }
}

