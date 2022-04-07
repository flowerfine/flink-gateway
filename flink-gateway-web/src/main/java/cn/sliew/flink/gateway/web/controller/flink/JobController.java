package cn.sliew.flink.gateway.web.controller.flink;

import cn.sliew.flink.gateway.engine.endpoint.impl.RestEndpointImpl2;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.messages.webmonitor.JobIdsWithStatusOverview;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationInfo;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.*;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointConfigInfo;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointStatistics;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointingStatistics;
import org.apache.flink.runtime.rest.messages.checkpoints.TaskCheckpointStatisticsWithSubtaskDetails;
import org.apache.flink.runtime.rest.messages.job.JobDetailsInfo;
import org.apache.flink.runtime.rest.messages.job.JobExecutionResultResponseBody;
import org.apache.flink.runtime.rest.messages.job.JobSubmitRequestBody;
import org.apache.flink.runtime.rest.messages.job.JobSubmitResponseBody;
import org.apache.flink.runtime.rest.messages.job.metrics.AggregatedMetricsResponseBody;
import org.apache.flink.runtime.rest.messages.job.metrics.MetricCollectionResponseBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointInfo;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointTriggerRequestBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.stop.StopWithSavepointRequestBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/jobs")
public class JobController {

//    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");
    private RestEndpoint endpoint = new RestEndpointImpl2(GlobalConfiguration.loadConfiguration());

    @GetMapping("overview")
    public CompletableFuture<MultipleJobsDetails> jobsOverview() throws IOException {
        return endpoint.jobsOverview();
    }

    @GetMapping("/")
    public CompletableFuture<JobIdsWithStatusOverview> jobs() throws IOException {
        return endpoint.jobs();
    }

    @GetMapping("/metrics")
    public CompletableFuture<AggregatedMetricsResponseBody> jobsMetrics(@RequestParam(value = "get", required = false) Optional<String> get,
                                                                        @RequestParam(value = "agg", required = false) Optional<String> agg,
                                                                        @RequestParam(value = "jobs", required = false) Optional<String> jobs) throws IOException {
        return endpoint.jobsMetric(get, agg, jobs);
    }

    @GetMapping("{jobId}")
    public CompletableFuture<JobDetailsInfo> job(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobDetail(jobId);
    }

    @GetMapping("{jobId}/config")
    public CompletableFuture<JobConfigInfo> jobConfig(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobConfig(jobId);
    }

    @GetMapping("{jobId}/metrics")
    public CompletableFuture<MetricCollectionResponseBody> jobMetrics(@PathVariable("jobId") String jobId,
                                                                      @RequestParam(value = "get", required = false) Optional<String> get) throws IOException {
        return endpoint.jobMetrics(jobId, get);
    }

    @GetMapping("{jobId}/exceptions")
    public CompletableFuture<JobExceptionsInfoWithHistory> jobExceptions(@PathVariable("jobId") String jobId,
                                                                         @RequestParam(value = "maxExceptions", required = false) Optional<String> maxExceptions) throws IOException {
        return endpoint.jobException(jobId, maxExceptions);
    }

    @GetMapping("{jobId}/execution-result")
    public CompletableFuture<JobExecutionResultResponseBody> jobExecutionResult(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobExecutionResult(jobId);
    }

    @GetMapping("{jobId}/accumulators")
    public CompletableFuture<JobAccumulatorsInfo> jobAccumulators(@PathVariable("jobId") String jobId,
                                                                  @RequestParam(value = "includeSerializedValue", required = false) Optional<Boolean> includeSerializedValue) throws IOException {
        return endpoint.jobAccumulators(jobId, includeSerializedValue);
    }

    @GetMapping("{jobId}/plan")
    public CompletableFuture<JobPlanInfo> jobPlan(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobPlan(jobId);
    }

    @GetMapping("{jobId}/checkpoints")
    public CompletableFuture<CheckpointingStatistics> jobCheckpoints(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobCheckpoints(jobId);
    }

    @GetMapping("{jobId}/checkpoints/config")
    public CompletableFuture<CheckpointConfigInfo> jobCheckpointsConfig(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobCheckpointConfig(jobId);
    }

    @GetMapping("{jobId}/checkpoints/details/{checkpointId}")
    public CompletableFuture<CheckpointStatistics> jobCheckpointDetail(@PathVariable("jobId") String jobId, @PathVariable("checkpointId") Long checkpointId) throws IOException {
        return endpoint.jobCheckpointDetail(jobId, checkpointId);
    }

    @GetMapping("{jobId}/checkpoints/details/{checkpointId}/subtasks/{vertexId}")
    public CompletableFuture<TaskCheckpointStatisticsWithSubtaskDetails> jobCheckpointSubtaskDetail(@PathVariable("jobId") String jobId,
                                                                                                    @PathVariable("checkpointId") Long checkpointId,
                                                                                                    @PathVariable("vertexId") String vertexId) throws IOException {
        return endpoint.jobCheckpointSubtaskDetail(jobId, checkpointId, vertexId);
    }

    @PostMapping("/")
    public CompletableFuture<JobSubmitResponseBody> submit(@RequestBody JobSubmitRequestBody requestBody) throws IOException {
        return endpoint.jobSubmit(requestBody);
    }

    @PatchMapping("{jobId}")
    public CompletableFuture<EmptyResponseBody> terminate(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobTerminate(jobId, null);
    }

    @PostMapping("{jobId}/stop")
    public CompletableFuture<TriggerResponse> stop(@PathVariable("jobId") String jobId, @RequestBody StopWithSavepointRequestBody requestBody) throws IOException {
        return endpoint.jobStop(jobId, requestBody);
    }

    /**
     * Rescaling is temporarily disabled. See FLINK-12312
     */
    @PostMapping("{jobId}/rescaling")
    public CompletableFuture<TriggerResponse> rescale(@PathVariable("jobId") String jobId,
                                                      @RequestParam(value = "parallelism", defaultValue = "2") Integer parallelism) throws IOException {
        return endpoint.jobRescale(jobId, parallelism);
    }

    @GetMapping("{jobId}/rescaling/{triggerId}")
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> rescaleResult(@PathVariable("jobId") String jobId,
                                                                                                   @PathVariable("triggerId") String triggerId) throws IOException {
        return endpoint.jobRescaleResult(jobId, triggerId);
    }

    @PostMapping("{jobId}/savepoints")
    public CompletableFuture<TriggerResponse> savepoint(@PathVariable("jobId") String jobId,
                                                        @RequestBody SavepointTriggerRequestBody requestBody) throws IOException {
        return endpoint.jobSavepoint(jobId, requestBody);
    }

    @GetMapping("{jobId}/savepoints/{triggerId}")
    public CompletableFuture<AsynchronousOperationResult<SavepointInfo>> savepointResult(@PathVariable("jobId") String jobId,
                                                                                         @PathVariable("triggerId") String triggerId) throws IOException {
        return endpoint.jobSavepointResult(jobId, triggerId);
    }


}
