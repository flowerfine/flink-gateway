package cn.sliew.flink.gateway.engine.client;

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
import org.apache.flink.runtime.rest.messages.job.*;
import org.apache.flink.runtime.rest.messages.job.metrics.AggregatedMetricsResponseBody;
import org.apache.flink.runtime.rest.messages.job.metrics.MetricCollectionResponseBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointInfo;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointTriggerRequestBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.stop.StopWithSavepointRequestBody;
import org.apache.flink.runtime.webmonitor.threadinfo.JobVertexFlameGraph;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface JobClient {

    /**
     * Returns an overview over all jobs.
     */
    CompletableFuture<MultipleJobsDetails> jobsOverview() throws IOException;

    /**
     * Provides access to aggregated job metrics.
     *
     * @param get(optional)  Comma-separated list of string values to select specific metrics.
     * @param agg(optional)  Comma-separated list of aggregation modes which should be calculated. Available aggregations are: "min, max, sum, avg".
     * @param jobs(optional) Comma-separated list of 32-character hexadecimal strings to select specific jobs.
     */
    CompletableFuture<AggregatedMetricsResponseBody> jobsMetric(Optional<String> get, Optional<String> agg, Optional<String> jobs) throws IOException;

    /**
     * Returns an overview over all jobs and their current state.
     */
    CompletableFuture<JobIdsWithStatusOverview> jobs() throws IOException;

    /**
     * Returns details of a job.
     *
     * @param jobId 32-character hexadecimal string value that identifies a job.
     */
    CompletableFuture<JobDetailsInfo> jobDetail(String jobId) throws IOException;

    /**
     * Submits a job.
     * This call is primarily intended to be used by the Flink client.
     * This call expects a multipart/form-data request that consists of file uploads for the serialized JobGraph,
     * jars and distributed cache artifacts and an attribute named "request" for the JSON payload.
     *
     * @param requestBody
     */
    CompletableFuture<JobSubmitResponseBody> jobSubmit(JobSubmitRequestBody requestBody) throws IOException;

    /**
     * Terminates a job.
     *
     * @param jobId          32-character hexadecimal string value that identifies a job.
     * @param mode(optional) String value that specifies the termination mode. The only supported value is: "cancel".
     */
    CompletableFuture<EmptyResponseBody> jobTerminate(String jobId, String mode) throws IOException;

    /**
     * Returns the accumulators for all tasks of a job, aggregated across the respective subtasks.
     *
     * @param jobId                            32-character hexadecimal string value that identifies a job.
     * @param includeSerializedValue(optional) Boolean value that specifies whether serialized user task accumulators should be included in the response.
     */
    CompletableFuture<JobAccumulatorsInfo> jobAccumulators(String jobId, Optional<Boolean> includeSerializedValue) throws IOException;

    /**
     * Returns checkpointing statistics for a job.
     *
     * @param jobId 32-character hexadecimal string value that identifies a job.
     */
    CompletableFuture<CheckpointingStatistics> jobCheckpoints(String jobId) throws IOException;

    /**
     * Returns the checkpointing configuration.
     *
     * @param jobId 32-character hexadecimal string value that identifies a job.
     */
    CompletableFuture<CheckpointConfigInfo> jobCheckpointConfig(String jobId) throws IOException;

    /**
     * Returns details for a checkpoint.
     *
     * @param jobId        32-character hexadecimal string value that identifies a job.
     * @param checkpointId Long value that identifies a checkpoint.
     */
    CompletableFuture<CheckpointStatistics> jobCheckpointDetail(String jobId, Long checkpointId) throws IOException;

    /**
     * Returns checkpoint statistics for a task and its subtasks.
     *
     * @param jobId        32-character hexadecimal string value that identifies a job.
     * @param checkpointId Long value that identifies a checkpoint.
     * @param vertexId     32-character hexadecimal string value that identifies a job vertex.
     */
    CompletableFuture<TaskCheckpointStatisticsWithSubtaskDetails> jobCheckpointSubtaskDetail(String jobId, Long checkpointId, String vertexId) throws IOException;

    /**
     * Returns the configuration of a job.
     *
     * @param jobId 32-character hexadecimal string value that identifies a job.
     */
    CompletableFuture<JobConfigInfo> jobConfig(String jobId) throws IOException;

    /**
     * Returns the most recent exceptions that have been handled by Flink for this job.
     * The 'exceptionHistory.truncated' flag defines whether exceptions were filtered out through the GET parameter.
     * The backend collects only a specific amount of most recent exceptions per job.
     * This can be configured through web.exception-history-size in the Flink configuration.
     * The following first-level members are deprecated: 'root-exception', 'timestamp', 'all-exceptions', and 'truncated'.
     * Use the data provided through 'exceptionHistory', instead.
     *
     * @param jobId                   32-character hexadecimal string value that identifies a job.
     * @param maxExceptions(optional) Comma-separated list of integer values that specifies the upper limit of exceptions to return.
     */
    CompletableFuture<JobExceptionsInfoWithHistory> jobException(String jobId, Optional<String> maxExceptions) throws IOException;

    /**
     * Returns the result of a job execution.
     * Gives access to the execution time of the job and to all accumulators created by this job.
     *
     * @param jobId 32-character hexadecimal string value that identifies a job.
     */
    CompletableFuture<JobExecutionResultResponseBody> jobExecutionResult(String jobId) throws IOException;

    /**
     * Provides access to job metrics.
     *
     * @param jobId         32-character hexadecimal string value that identifies a job.
     * @param get(optional) Comma-separated list of string values to select specific metrics.
     */
    CompletableFuture<MetricCollectionResponseBody> jobMetrics(String jobId, Optional<String> get) throws IOException;

    /**
     * Returns the dataflow plan of a job.
     *
     * @param jobId 32-character hexadecimal string value that identifies a job.
     */
    CompletableFuture<JobPlanInfo> jobPlan(String jobId) throws IOException;

    /**
     * Triggers the rescaling of a job. This async operation would return a 'triggerid' for further query identifier.
     *
     * @param jobId                   32-character hexadecimal string value that identifies a job.
     * @param parallelism(mandatory): Positive integer value that specifies the desired parallelism.
     */
    CompletableFuture<TriggerResponse> jobRescale(String jobId, Integer parallelism) throws IOException;

    /**
     * Returns the status of a rescaling operation.
     *
     * @param jobId     32-character hexadecimal string value that identifies a job.
     * @param triggerId 32-character hexadecimal string that identifies an asynchronous operation trigger ID. The ID was returned then the operation was triggered.
     */
    CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> jobRescaleResult(String jobId, String triggerId) throws IOException;

    /**
     * Triggers a savepoint, and optionally cancels the job afterwards.
     * This async operation would return a 'triggerid' for further query identifier.
     *
     * @param jobId       32-character hexadecimal string value that identifies a job.
     * @param requestBody
     */
    CompletableFuture<TriggerResponse> jobSavepoint(String jobId, SavepointTriggerRequestBody requestBody) throws IOException;

    /**
     * Returns the status of a savepoint operation.
     *
     * @param jobId     32-character hexadecimal string value that identifies a job.
     * @param triggerId 32-character hexadecimal string that identifies an asynchronous operation trigger ID. The ID was returned then the operation was triggered.
     */
    CompletableFuture<AsynchronousOperationResult<SavepointInfo>> jobSavepointResult(String jobId, String triggerId) throws IOException;

    /**
     * Stops a job with a savepoint.
     * Optionally, it can also emit a MAX_WATERMARK before taking the savepoint to flush out any state waiting for timers to fire.
     * This async operation would return a 'triggerid' for further query identifier.
     *
     * @param jobId       32-character hexadecimal string value that identifies a job.
     * @param requestBody
     */
    CompletableFuture<TriggerResponse> jobStop(String jobId, StopWithSavepointRequestBody requestBody) throws IOException;

    /**
     * Returns details for a task, with a summary for each of its subtasks.
     *
     * @param jobId    32-character hexadecimal string value that identifies a job.
     * @param vertexId 32-character hexadecimal string value that identifies a job vertex.
     */
    CompletableFuture<JobVertexDetailsInfo> jobVertexDetail(String jobId, String vertexId) throws IOException;

    /**
     * Returns user-defined accumulators of a task, aggregated across all subtasks.
     *
     * @param jobId    32-character hexadecimal string value that identifies a job.
     * @param vertexId 32-character hexadecimal string value that identifies a job vertex.
     */
    CompletableFuture<JobVertexAccumulatorsInfo> jobVertexAccumulators(String jobId, String vertexId) throws IOException;

    /**
     * Returns back-pressure information for a job, and may initiate back-pressure sampling if necessary.
     *
     * @param jobId    32-character hexadecimal string value that identifies a job.
     * @param vertexId 32-character hexadecimal string value that identifies a job vertex.
     */
    CompletableFuture<JobVertexBackPressureInfo> jobVertexBackPressure(String jobId, String vertexId) throws IOException;

    /**
     * Returns flame graph information for a vertex, and may initiate flame graph sampling if necessary.
     *
     * @param jobId          32-character hexadecimal string value that identifies a job.
     * @param vertexId       32-character hexadecimal string value that identifies a job vertex.
     * @param type(optional) String value that specifies the Flame Graph type. Supported options are: "[FULL, ON_CPU, OFF_CPU]".
     */
    CompletableFuture<JobVertexFlameGraph> jobVertexFlameGraph(String jobId, String vertexId, String type) throws IOException;

    /**
     * Provides access to task metrics.
     *
     * @param jobId         32-character hexadecimal string value that identifies a job.
     * @param vertexId      32-character hexadecimal string value that identifies a job vertex.
     * @param get(optional) Comma-separated list of string values to select specific metrics.
     */
    CompletableFuture<MetricCollectionResponseBody> jobVertexMetrics(String jobId, String vertexId, String get) throws IOException;

    /**
     * Returns all user-defined accumulators for all subtasks of a task.
     *
     * @param jobId    32-character hexadecimal string value that identifies a job.
     * @param vertexId 32-character hexadecimal string value that identifies a job vertex.
     */
    CompletableFuture<SubtasksAllAccumulatorsInfo> jobVertexSubtaskAccumulators(String jobId, String vertexId) throws IOException;

    /**
     * Provides access to aggregated subtask metrics.
     *
     * @param jobId              32-character hexadecimal string value that identifies a job.
     * @param vertexId           32-character hexadecimal string value that identifies a job vertex.
     * @param get(optional)      Comma-separated list of string values to select specific metrics.
     * @param agg(optional)      Comma-separated list of aggregation modes which should be calculated. Available aggregations are: "min, max, sum, avg".
     * @param subtasks(optional) Comma-separated list of integer ranges (e.g. "1,3,5-9") to select specific subtasks.
     */
    CompletableFuture<MetricCollectionResponseBody> jobVertexSubtaskMetrics(String jobId, String vertexId, String get, String agg, String subtasks) throws IOException;

    /**
     * Returns details of the current or latest execution attempt of a subtask.
     *
     * @param jobId        32-character hexadecimal string value that identifies a job.
     * @param vertexId     32-character hexadecimal string value that identifies a job vertex.
     * @param subtaskindex Positive integer value that identifies a subtask.
     */
    CompletableFuture<SubtaskExecutionAttemptDetailsInfo> jobVertexSubtaskDetail(String jobId, String vertexId, Integer subtaskindex) throws IOException;

    /**
     * Returns details of an execution attempt of a subtask.
     * Multiple execution attempts happen in case of failure/recovery.
     *
     * @param jobId        32-character hexadecimal string value that identifies a job.
     * @param vertexId     32-character hexadecimal string value that identifies a job vertex.
     * @param subtaskindex Positive integer value that identifies a subtask.
     * @param attempt      Positive integer value that identifies an execution attempt.
     */
    CompletableFuture<SubtaskExecutionAttemptDetailsInfo> jobVertexSubtaskAttemptDetail(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException;

    /**
     * Returns the accumulators of an execution attempt of a subtask.
     * Multiple execution attempts happen in case of failure/recovery.
     *
     * @param jobId        32-character hexadecimal string value that identifies a job.
     * @param vertexId     32-character hexadecimal string value that identifies a job vertex.
     * @param subtaskindex Positive integer value that identifies a subtask.
     * @param attempt      Positive integer value that identifies an execution attempt.
     */
    CompletableFuture<SubtaskExecutionAttemptAccumulatorsInfo> jobVertexSubtaskAttemptAccumulators(String jobId, String vertexId, Integer subtaskindex, Integer attempt) throws IOException;

    /**
     * Provides access to subtask metrics.
     *
     * @param jobId         32-character hexadecimal string value that identifies a job.
     * @param vertexId      32-character hexadecimal string value that identifies a job vertex.
     * @param subtaskindex  Positive integer value that identifies a subtask.
     * @param get(optional) Comma-separated list of string values to select specific metrics.
     */
    CompletableFuture<MetricCollectionResponseBody> jobVertexSubtaskMetrics(String jobId, String vertexId, Integer subtaskindex, String get) throws IOException;

    /**
     * Returns time-related information for all subtasks of a task.
     *
     * @param jobId    32-character hexadecimal string value that identifies a job.
     * @param vertexId 32-character hexadecimal string value that identifies a job vertex.
     */
    CompletableFuture<SubtasksTimesInfo> jobVertexSubtaskTimes(String jobId, String vertexId) throws IOException;

    /**
     * Returns task information aggregated by task manager.
     *
     * @param jobId    32-character hexadecimal string value that identifies a job.
     * @param vertexId 32-character hexadecimal string value that identifies a job vertex.
     */
    CompletableFuture<JobVertexTaskManagersInfo> jobVertexTaskManagers(String jobId, String vertexId) throws IOException;

    /**
     * Returns the watermarks for all subtasks of a task.
     *
     * @param jobId    32-character hexadecimal string value that identifies a job.
     * @param vertexId 32-character hexadecimal string value that identifies a job vertex.
     */
    CompletableFuture<MetricCollectionResponseBody> jobVertexWatermarks(String jobId, String vertexId) throws IOException;
}
