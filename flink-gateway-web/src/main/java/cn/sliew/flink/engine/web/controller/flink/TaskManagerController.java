package cn.sliew.flink.engine.web.controller.flink;

import cn.sliew.flink.gateway.engine.endpoint.RestEndpoint;
import cn.sliew.flink.gateway.engine.endpoint.impl.RestEndpointImpl;
import org.apache.flink.runtime.rest.messages.LogListInfo;
import org.apache.flink.runtime.rest.messages.job.metrics.MetricCollectionResponseBody;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagerDetailsInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagersInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.ThreadDumpInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/task-manager")
public class TaskManagerController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @GetMapping("/")
    public CompletableFuture<TaskManagersInfo> taskManagers() throws IOException {
        return endpoint.taskManagers();
    }

    /**
     * todo 拆开 get, agg, taskmanagers
     */
    @GetMapping("metrics")
    public CompletableFuture<MetricCollectionResponseBody> taskMangersMetrics() throws IOException {
        return endpoint.taskManagersMetrics(null, null, null);
    }

    @GetMapping("{taskManagerId}")
    public CompletableFuture<TaskManagerDetailsInfo> taskManagerDetail(@PathVariable("taskManagerId") String taskManagerId) throws IOException {
        return endpoint.taskManagerDetail(taskManagerId);
    }

    /**
     * todo 拆开 get
     */
    @GetMapping("{taskManagerId}/metrics")
    public CompletableFuture<MetricCollectionResponseBody> taskManagerMetrics(@PathVariable("taskManagerId") String taskManagerId) throws IOException {
        return endpoint.taskManagerMetrics(taskManagerId, null);
    }

    @GetMapping("{taskManagerId}/logs")
    public CompletableFuture<LogListInfo> taskManagerLogs(@PathVariable("taskManagerId") String taskManagerId) throws IOException {
        return endpoint.taskManagerLogs(taskManagerId);
    }

    @GetMapping("{taskManagerId}/thread-dump")
    public CompletableFuture<ThreadDumpInfo> taskManagerThreadDump(@PathVariable("taskManagerId") String taskManagerId) throws IOException {
        return endpoint.taskManagerThreadDump(taskManagerId);
    }
}
