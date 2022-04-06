package cn.sliew.flink.gateway.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DeploymentMode {

    APPLICATION(0, "Application"),
    PER_JOB(1, "Per-Job"),
    SESSION(2, "Session");

    @JsonValue
    @EnumValue
    private int code;
    private String name;

    DeploymentMode(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
