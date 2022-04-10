package cn.sliew.flink.gateway.event.scheduler;

public interface EventSource {

    <T> void register(ActionListener<T> listener);




}
