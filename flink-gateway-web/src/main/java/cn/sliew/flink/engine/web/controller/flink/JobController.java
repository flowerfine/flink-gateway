package cn.sliew.flink.engine.web.controller.flink;

import cn.sliew.flink.gateway.engine.RestEndpoint;
import cn.sliew.flink.gateway.engine.impl.RestEndpointImpl;
import org.apache.flink.runtime.messages.webmonitor.JobIdsWithStatusOverview;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.JobAccumulatorsInfo;
import org.apache.flink.runtime.rest.messages.JobExceptionsInfoWithHistory;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointConfigInfo;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointStatistics;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointingStatistics;
import org.apache.flink.runtime.rest.messages.checkpoints.TaskCheckpointStatisticsWithSubtaskDetails;
import org.apache.flink.runtime.rest.messages.job.JobDetailsInfo;
import org.apache.flink.runtime.rest.messages.job.JobExecutionResultResponseBody;
import org.apache.flink.runtime.rest.messages.job.JobSubmitRequestBody;
import org.apache.flink.runtime.rest.messages.job.JobSubmitResponseBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointTriggerRequestBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.stop.StopWithSavepointRequestBody;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flink/jobs")
public class JobController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @GetMapping("overview")
    public MultipleJobsDetails jobsOverview() throws IOException {
        return endpoint.jobsOverview();
    }

    @GetMapping("/")
    public JobIdsWithStatusOverview jobs() throws IOException {
        return endpoint.jobs();
    }

    @GetMapping("{jobId}")
    public JobDetailsInfo job(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobDetail(jobId);
    }

    @GetMapping("{jobId}/config")
    public Map jobConfig(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobConfig(jobId);
    }

    @GetMapping("{jobId}/metrics")
    public List<Map> jobMetrics(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobMetrics(jobId, null);
    }

    @GetMapping("{jobId}/exceptions")
    public JobExceptionsInfoWithHistory jobExceptions(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobException(jobId, null);
    }

    @GetMapping("{jobId}/execution-result")
    public JobExecutionResultResponseBody jobExecutionResult(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobExecutionResult(jobId);
    }

    @GetMapping("{jobId}/accumulators")
    public JobAccumulatorsInfo jobAccumulators(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobAccumulators(jobId, null);
    }

    @GetMapping("{jobId}/plan")
    public JobPlanInfo jobPlan(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobPlan(jobId);
    }

    @GetMapping("{jobId}/checkpoints")
    public CheckpointingStatistics jobCheckpoints(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobCheckpoints(jobId);
    }

    @GetMapping("{jobId}/checkpoints/config")
    public CheckpointConfigInfo jobCheckpointsConfig(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobCheckpointConfig(jobId);
    }

    @GetMapping("{jobId}/checkpoints/details/{checkpointId}")
    public CheckpointStatistics jobCheckpointDetail(@PathVariable("jobId") String jobId, @PathVariable("checkpointId") Long checkpointId) throws IOException {
        return endpoint.jobCheckpointDetail(jobId, checkpointId);
    }

    @GetMapping("{jobId}/checkpoints/details/{checkpointId}/subtasks/{vertexId}")
    public TaskCheckpointStatisticsWithSubtaskDetails jobCheckpointSubtaskDetail(@PathVariable("jobId") String jobId,
                                                                                 @PathVariable("checkpointId") Long checkpointId,
                                                                                 @PathVariable("vertexId") String vertexId) throws IOException {
        return endpoint.jobCheckpointSubtaskDetail(jobId, checkpointId, vertexId);
    }

    @PostMapping("/")
    public JobSubmitResponseBody submit(@RequestBody JobSubmitRequestBody requestBody) throws IOException {
        return endpoint.jobSubmit(requestBody);
    }

    @PatchMapping("{jobId}")
    public boolean terminate(@PathVariable("jobId") String jobId) throws IOException {
        return endpoint.jobTerminate(jobId, null);
    }

    @PostMapping("{jobId}/stop")
    public TriggerResponse stop(@PathVariable("jobId") String jobId, @RequestBody StopWithSavepointRequestBody requestBody) throws IOException {
        return endpoint.jobStop(jobId, requestBody);
    }

    /**
     * Rescaling is temporarily disabled. See FLINK-12312
     */
    @PostMapping("{jobId}/rescaling")
    public TriggerResponse rescale(@PathVariable("jobId") String jobId,
                                   @RequestParam(value = "parallelism", defaultValue = "2") Integer parallelism) throws IOException {
        return endpoint.jobRescale(jobId, parallelism);
    }

    @GetMapping("{jobId}/rescaling/{triggerId}")
    public AsynchronousOperationResult rescaleResult(@PathVariable("jobId") String jobId,
                                                     @PathVariable("triggerId") String triggerId) throws IOException {
        return endpoint.jobRescaleResult(jobId, triggerId);
    }

    @PostMapping("{jobId}/savepoints")
    public TriggerResponse savepoint(@PathVariable("jobId") String jobId,
                                     @RequestBody SavepointTriggerRequestBody requestBody) throws IOException {
        return endpoint.jobSavepoint(jobId, requestBody);
    }

    @GetMapping("{jobId}/savepoints/{triggerId}")
    public AsynchronousOperationResult savepointResult(@PathVariable("jobId") String jobId,
                                     @PathVariable("triggerId") String triggerId) throws IOException {
        return endpoint.jobSavepointResult(jobId, triggerId);
    }








}
