package cn.sliew.flink.gateway.common.enums;

public enum FlinkVersion {
    ;

    private int code;
    private String desc;

    FlinkVersion(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
