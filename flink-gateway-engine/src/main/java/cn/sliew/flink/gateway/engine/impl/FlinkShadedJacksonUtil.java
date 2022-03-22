package cn.sliew.flink.gateway.engine.impl;

import cn.sliew.milky.common.exception.Rethrower;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public enum FlinkShadedJacksonUtil {
    ;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T parseJsonString(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException var3) {
            log.error("json 反序列化失败 clazz: {}, json: {}", new Object[]{clazz.getName(), json, var3});
            Rethrower.throwAs(var3);
            return null;
        }
    }
}
