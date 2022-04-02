package cn.sliew.flink.gateway.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum JobType {

    JAR(0, "jar"),
    SQL(1, "SQL"),
    ;

    @JsonValue
    @EnumValue
    private int code;
    private String desc;

    JobType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
