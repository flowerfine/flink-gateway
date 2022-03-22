package cn.sliew.flink.gateway.engine.impl;

import cn.sliew.milky.common.exception.Rethrower;
import cn.sliew.milky.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static <T> List<T> parseJsonArray(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        } else {
            try {
                CollectionType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
                return (List)OBJECT_MAPPER.readValue(json, listType);
            } catch (Exception var3) {
                log.error("json 反序列化为 list 失败 clazz: {}, json: {}", new Object[]{clazz.getName(), json, var3});
                return Collections.emptyList();
            }
        }
    }
}
