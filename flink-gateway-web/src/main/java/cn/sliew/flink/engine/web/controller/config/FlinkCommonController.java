package cn.sliew.flink.engine.web.controller.config;

import cn.sliew.milky.common.util.JacksonUtil;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.configuration.description.Formatter;
import org.apache.flink.configuration.description.HtmlFormatter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config/common")
public class FlinkCommonController {

    private final Formatter formatter = new HtmlFormatter();

    @GetMapping("configs")
    public ConfigOption configs() throws Exception {
        ConfigOption option = RestOptions.ADDRESS;
        Map<String, Object> map = new HashMap<>();
        map.put("key", option.key());
        map.put("class", option.getClass().getSimpleName());
        map.put("description", formatter.format(option.description()));
        if (option.hasDefaultValue()) {
            map.put("defaultValue", option.defaultValue());
        }
        if (option.hasFallbackKeys()) {
            map.put("fallbackKey", option.fallbackKeys());
        }
        System.out.println(map);
        return option;
    }


}
