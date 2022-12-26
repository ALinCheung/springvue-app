package com.springvue.app.common.sharding;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

/**
 * 动态分表策略
 */
@Slf4j
public class DynamicTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String>, RangeShardingAlgorithm<String> {

    /**
     * 逻辑表名
     */
    private String logicTableName;

    /**
     * 数据源
     */
    private DataSource dataSource;

    public DynamicTablePreciseShardingAlgorithm(DataSource dataSource, String logicTableName) {
        this.dataSource = dataSource;
        this.logicTableName = logicTableName;
    }

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        // 默认分区表
        String actualTableName = logicTableName;
        try {
            if (StringUtils.isNotBlank(shardingValue.getValue())) {
                String table = String.format("%s_%s", logicTableName, shardingValue.getValue());
                actualTableName = ShardingUtils.createTableIfNotExists(table, logicTableName, dataSource);
            }
        } catch (SQLException e) {
            log.error("创建表时发错错误：" + e.getMessage(), e);
        }
        return actualTableName;
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<String> shardingValue) {
        return Collections.singleton(logicTableName);
    }
}
