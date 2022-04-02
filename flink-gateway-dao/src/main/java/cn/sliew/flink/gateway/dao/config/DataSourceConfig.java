package cn.sliew.flink.gateway.dao.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    static final String GATEWAY_DATA_SOURCE_NAME = "cn.sliew.flink.gateway.dao.config.GatewayDataSource";

    @Bean(GATEWAY_DATA_SOURCE_NAME)
    @ConfigurationProperties(prefix = "spring.datasource.hikari.gateway")
    public HikariDataSource flinkGatewayDataSource() {
        return new HikariDataSource();
    }
}
