package cn.sliew.flink.gateway.model.result;

import cn.sliew.flink.gateway.model.status.ResponseStatus;

public enum ResultHelper {
    ;

    public static <T> ResultWrapper<T> success(T entry) {
        return successWithStatus(entry, ResponseStatus.SUCCESS);
    }

    public static <T> ResultWrapper<T> successWithStatus(T entry, ResponseStatus status) {
        ResultWrapper<T> resultWrapper = new ResultWrapper<>();
        resultWrapper.setStatus(true);
        resultWrapper.setCode(String.valueOf(status.getCode()));
        resultWrapper.setMessage(status.getDesc());
        resultWrapper.setEntry(entry);
        return resultWrapper;
    }

}
