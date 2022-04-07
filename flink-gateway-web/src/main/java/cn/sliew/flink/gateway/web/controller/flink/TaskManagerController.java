package cn.sliew.flink.gateway.web.controller.flink;

import org.apache.flink.runtime.rest.messages.LogListInfo;
import org.apache.flink.runtime.rest.messages.job.metrics.AggregatedMetricsResponseBody;
import org.apache.flink.runtime.rest.messages.job.metrics.MetricCollectionResponseBody;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagerDetailsInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagersInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.ThreadDumpInfo;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/task-manager")
public class TaskManagerController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @GetMapping("/")
    public CompletableFuture<TaskManagersInfo> taskManagers() throws IOException {
        return endpoint.taskManagers();
    }

    @GetMapping("metrics")
    public CompletableFuture<AggregatedMetricsResponseBody> taskMangersMetrics(@RequestParam(value = "get", required = false) Optional<String> get,
                                                                               @RequestParam(value = "agg", required = false) Optional<String> agg,
                                                                               @RequestParam(value = "taskmanagers", required = false) Optional<String> taskmanagers) throws IOException {
        return endpoint.taskManagersMetrics(get, agg, taskmanagers);
    }

    @GetMapping("{taskManagerId}")
    public CompletableFuture<TaskManagerDetailsInfo> taskManagerDetail(@PathVariable("taskManagerId") String taskManagerId) throws IOException {
        return endpoint.taskManagerDetail(taskManagerId);
    }

    @GetMapping("{taskManagerId}/metrics")
    public CompletableFuture<MetricCollectionResponseBody> taskManagerMetrics(@PathVariable("taskManagerId") String taskManagerId,
                                                                              @RequestParam(value = "get", required = false) Optional<String> get) throws IOException {
        return endpoint.taskManagerMetrics(taskManagerId, get);
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
