package cn.sliew.flink.gateway.event.scheduler;

/**
 * 调度器不断执行轮询任务，产生事件，输出事件到固定的地方。
 */
public interface RoundRobinEventSource extends EventSource {

    Scheduler getScheduler();
}
