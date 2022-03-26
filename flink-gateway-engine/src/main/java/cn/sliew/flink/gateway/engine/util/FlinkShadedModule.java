package cn.sliew.flink.gateway.engine.util;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptionSerializer;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.module.SimpleModule;

public class FlinkShadedModule extends SimpleModule {

    public FlinkShadedModule() {
        super();
        this.addSerializer(ConfigOption.class, new ConfigOptionSerializer());
    }
}
