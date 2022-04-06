package cn.sliew.flink.gateway.model.result;

public class ExplainWrapper {

    /**
     * 请求参数。不对外提示，便于抓包排查问题，特别是未知系统异常，一般在debug模式下启用
     */
    private String params;

    /**
     * 异常信息。不对外提示，便于抓包排查问题，特别是未知系统异常，一般在debug模式下启用
     */
    private String exception;
}
