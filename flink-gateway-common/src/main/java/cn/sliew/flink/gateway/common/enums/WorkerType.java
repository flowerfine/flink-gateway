package cn.sliew.flink.gateway.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum WorkerType {

    CONTAINER(0, "容器"),
    MACHINE(1, "物理机器");

    @JsonValue
    @EnumValue
    private int code;
    private String desc;

    WorkerType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
