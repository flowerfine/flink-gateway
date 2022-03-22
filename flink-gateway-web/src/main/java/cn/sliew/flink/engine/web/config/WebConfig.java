package cn.sliew.flink.engine.web.config;

import cn.sliew.flink.engine.web.http.message.MappingFlinkShadedJackson2HttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingFlinkShadedJackson2HttpMessageConverter());
    }
}
