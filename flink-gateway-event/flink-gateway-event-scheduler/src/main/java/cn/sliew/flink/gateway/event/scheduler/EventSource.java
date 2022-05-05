package cn.sliew.flink.gateway.event.scheduler;

public interface EventSource {

    <T> void register(ActionListener<T> listener);

    <T> void unregister(ActionListener<T> listener);
}
