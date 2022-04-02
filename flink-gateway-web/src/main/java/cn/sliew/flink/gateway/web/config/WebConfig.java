package cn.sliew.flink.gateway.web.config;

import cn.sliew.flink.gateway.web.http.message.MappingFlinkShadedJackson2HttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new MappingFlinkShadedJackson2HttpMessageConverter());
    }
}
