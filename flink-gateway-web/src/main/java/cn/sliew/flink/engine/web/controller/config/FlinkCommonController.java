package cn.sliew.flink.engine.web.controller.config;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.RestOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/config/common")
public class FlinkCommonController {

    @GetMapping("configs")
    public List<ConfigOption> configs() throws Exception {
        ConfigOption<String> urlOption = ConfigOptions.key("url")
                .stringType()
                .defaultValue("jdbc:mysql://localhost:3306/test")
                .withDescription("jdbc url")
                .withFallbackKeys("jdbc.url");
//        return RestOptions.ADDRESS;
        return Arrays.asList(urlOption, RestOptions.ADDRESS, RestOptions.PORT);
    }
}
