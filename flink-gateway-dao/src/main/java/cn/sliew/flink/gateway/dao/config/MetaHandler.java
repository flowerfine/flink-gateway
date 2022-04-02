package cn.sliew.flink.gateway.dao.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MetaHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", () -> new Date(), Date.class);
        this.strictInsertFill(metaObject, "updateTime", () -> new Date(), Date.class);
        this.strictInsertFill(metaObject, "creator", () -> ThreadContext.get("traceId"), String.class);
        this.strictInsertFill(metaObject, "updater", () -> ThreadContext.get("traceId"), String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", () -> new Date(), Date.class);
        this.strictUpdateFill(metaObject, "updater", () -> ThreadContext.get("traceId"), String.class);
    }
}
