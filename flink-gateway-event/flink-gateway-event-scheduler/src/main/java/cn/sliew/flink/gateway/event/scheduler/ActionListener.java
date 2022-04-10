package cn.sliew.flink.gateway.event.scheduler;

public interface ActionListener<T> {

    void onComplete(T result);

    void onThrowable(Throwable cause);
}
