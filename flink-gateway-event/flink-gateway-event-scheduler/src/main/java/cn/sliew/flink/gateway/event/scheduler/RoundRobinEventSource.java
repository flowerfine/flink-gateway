package cn.sliew.flink.gateway.event.scheduler;

public interface RoundRobinEventSource extends EventSource {

    Scheduler getScheduler();

}
