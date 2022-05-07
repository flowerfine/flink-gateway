package cn.sliew.flink.gateway.web.config;

import cn.sliew.flinkful.rest.base.RestClient;
import cn.sliew.flinkful.rest.client.FlinkRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        org.apache.flink.configuration.Configuration configuration = null;
        return new FlinkRestClient("localhost", 8081, configuration);
    }
}
