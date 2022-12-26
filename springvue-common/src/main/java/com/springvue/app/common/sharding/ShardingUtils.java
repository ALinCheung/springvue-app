package com.springvue.app.common.sharding;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ShardingUtils {

    /**
     * 配置分布式键生成策略
     */
    public static void configKeyGenerator(ShardingRuleConfiguration configuration) {
        // 随机工作节点ID，确保分布式主键的唯一性
        String seed = System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(5);
        Long randomWorkerId = Math.abs(seed.hashCode()) % 1024L;
        // 配置策略
        Properties properties = new Properties();
        properties.setProperty("worker.id", randomWorkerId + "");
        KeyGeneratorConfiguration keyGeneratorConfiguration =
                new KeyGeneratorConfiguration("SNOWFLAKE", "id", properties);
        configuration.setDefaultKeyGeneratorConfig(keyGeneratorConfiguration);
    }

    /**
     * 配置表分片策略
     */
    public static void configTable(ShardingRuleConfiguration configuration,
                                   DataSource dataSource, String logicTable, String column, String actualDataNodes) {
        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration(logicTable, actualDataNodes);
        tableRuleConfiguration.setTableShardingStrategyConfig(
                new StandardShardingStrategyConfiguration(
                        column, new DynamicTablePreciseShardingAlgorithm(dataSource, logicTable)));

        configuration.getTableRuleConfigs().add(tableRuleConfiguration);
    }

    /**
     * 若表不存在创建表
     */
    public static String createTableIfNotExists(
            String table, String templateTable, DataSource dataSource) throws SQLException {
        StringBuilder sb = new StringBuilder("create table if not exists ")
                .append(table).append(" like ").append(templateTable);
        String createTableSql = sb.toString();
        try (final Connection connection = dataSource.getConnection();
             final Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);
        }
        return table;
    }
}
