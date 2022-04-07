package cn.sliew.flink.gateway.model.status;

import lombok.Getter;

@Getter
public enum ResponseStatus {

    SUCCESS(0L, "success"),
    FAILURE(1L, "failure"),


    ;

    private long code;
    private String desc;

    ResponseStatus(long code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
