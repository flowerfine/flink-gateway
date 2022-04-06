package cn.sliew.flink.gateway.model.result;

public enum ResultHelper {
    ;

    public static <T> ResultWrapper<T> success(T entry) {
        ResultWrapper<T> resultWrapper = new ResultWrapper<>();
        resultWrapper.setStatus(true);
//        resultWrapper.setCode();
        resultWrapper.setMessage(ResultWrapper.SUCCESS);
        resultWrapper.setEntry(entry);
        return resultWrapper;
    }
}
