package cn.sliew.flink.gateway.model.result;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class ResultWrapper<T> implements Result {

    static final String SUCCESS = "success";
    static final String FAILURE = "failure";

    @Serial
    private static final long serialVersionUID = -6775375311523603147L;

    /**
     * 状态。true: 成功, false: 失败
     */
    private boolean status;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 响应码
     */
    private String code;

    /**
     * 业务数据
     */
    private T entry;
}
