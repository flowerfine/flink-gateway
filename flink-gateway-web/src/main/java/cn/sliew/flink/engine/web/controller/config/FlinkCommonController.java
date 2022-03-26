package cn.sliew.flink.engine.web.controller.config;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.RestOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config/common")
public class FlinkCommonController {

    @GetMapping("configs")
    public ConfigOption configs() throws Exception {
        return RestOptions.ADDRESS;
    }
}
