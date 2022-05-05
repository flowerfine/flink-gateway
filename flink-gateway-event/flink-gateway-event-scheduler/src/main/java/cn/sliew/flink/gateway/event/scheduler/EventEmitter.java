package cn.sliew.flink.gateway.event.scheduler;

public interface EventEmitter<T> {

    void emit(T event);
}
