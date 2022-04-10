package cn.sliew.flink.gateway.event.scheduler;

import java.util.concurrent.ScheduledExecutorService;

/**
 * jdk 本身的 调度线程池
 * netty 的 eventloop。eventloop 是 jdk 线程池的实现。
 * 时间轮
 * 或者是 quartz
 *
 * 调度器不断执行轮询任务，产生事件，输出事件到固定的地方。
 *
 *
 */
public interface Scheduler extends ScheduledExecutorService {



}
