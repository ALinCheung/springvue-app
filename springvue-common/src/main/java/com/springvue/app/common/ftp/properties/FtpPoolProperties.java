package com.springvue.app.common.ftp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ftp.pool")
@Data
public class FtpPoolProperties {
    private int maxTotal = -1;
    private int maxTotalPerKey = 5;
    private int maxIdlePerKey = 5;
    private int minIdlePerKey = 0;
    private long maxWaitMillis = -1L;
    private boolean lifo = true;
    private boolean fairness = false;
    private long minEvictableIdleTimeMillis = 1800000L;
    private long softMinEvictableIdleTimeMillis = -1L;
    private int numTestsPerEvictionRun = 3;
    private long evictorShutdownTimeoutMillis = 10000L;
    private long timeBetweenEvictionRunsMillis = -1L;
    private String evictionPolicyClassName = "org.apache.commons.pool2.impl.DefaultEvictionPolicy";
    private boolean blockWhenExhausted = true;
    private boolean jmxEnabled = false;
    private String jmxNameBase = null;
    private String jmxNamePrefix = "ftp-keyed-pool";
    private boolean testOnBorrow = true;
    private boolean testOnReturn = false;
    private boolean testOnCreate = false;
    private boolean testWhileIdle = false;
}
