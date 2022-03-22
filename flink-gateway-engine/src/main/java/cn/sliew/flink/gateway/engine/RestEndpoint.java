package cn.sliew.flink.gateway.engine;

import org.apache.flink.runtime.messages.webmonitor.JobIdsWithStatusOverview;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.ClusterConfigurationInfoEntry;
import org.apache.flink.runtime.rest.messages.DashboardConfiguration;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.runtime.rest.messages.LogListInfo;
import org.apache.flink.runtime.rest.messages.dataset.ClusterDataSetListResponseBody;
import org.apache.flink.runtime.rest.messages.job.JobSubmitRequestBody;
import org.apache.flink.runtime.rest.messages.job.JobSubmitResponseBody;
import org.apache.flink.runtime.webmonitor.handlers.*;

import java.io.IOException;
import java.util.List;

public interface RestEndpoint {

    /**
     * Shuts down the cluster
     */
    boolean cluster() throws IOException;

    /**
     * Returns the configuration of the WebUI.
     */
    DashboardConfiguration config() throws IOException;

    /**
     * Returns all cluster data sets.
     */
    ClusterDataSetListResponseBody datasets() throws IOException;

    /**
     * Triggers the deletion of a cluster data set.
     * This async operation would return a 'triggerid' for further query identifier.
     */
    TriggerResponse deleteDataSet(String datasetId) throws IOException;

    /**
     * Returns the status for the delete operation of a cluster data set.
     */
    AsynchronousOperationResult deleteDataSetStatus(String triggerId) throws IOException;

    /**
     * Returns a list of all jars previously uploaded via '/jars/upload'.
     */
    JarListInfo jars() throws IOException;

    /**
     * Uploads a jar to the cluster.
     * The jar must be sent as multi-part data.
     * Make sure that the "Content-Type" header is set to "application/x-java-archive", as some http libraries do not add the header by default.
     * Using 'curl' you can upload a jar via 'curl -X POST -H "Expect:" -F "jarfile=@path/to/flink-job.jar" http://hostname:port/jars/upload'.
     */
    JarUploadResponseBody uploadJar(String filePath) throws IOException;

    /**
     * Deletes a jar previously uploaded via '/jars/upload'.
     */
    boolean deleteJar(String jarId) throws IOException;

    /**
     * Returns the dataflow plan of a job contained in a jar previously uploaded via '/jars/upload'.
     * Program arguments can be passed both via the JSON request (recommended) or query parameters.
     */
    JobPlanInfo jarPlan(String jarId, JarPlanRequestBody requestBody) throws IOException;

    /**
     * Submits a job by running a jar previously uploaded via '/jars/upload'.
     * Program arguments can be passed both via the JSON request (recommended) or query parameters.
     */
    JarRunResponseBody jarRun(String jarId, JarRunRequestBody requestBody) throws IOException;

    /**
     * Returns the cluster configuration.
     */
    List<ClusterConfigurationInfoEntry> jobmanagerConfig() throws IOException;

    /**
     * Returns the list of log files on the JobManager.
     */
    LogListInfo jobmanagerLogs() throws IOException;

    /**
     * Provides access to job manager metrics.
     */
    String jobmanagerMetrics(String metric) throws IOException;

    /**
     * Returns an overview over all jobs and their current state.
     */
    JobIdsWithStatusOverview jobs() throws IOException;

    /**
     * Submits a job.
     * This call is primarily intended to be used by the Flink client.
     * This call expects a multipart/form-data request that consists of file uploads for the serialized JobGraph,
     * jars and distributed cache artifacts and an attribute named "request" for the JSON payload.
     */
    JobSubmitResponseBody jobSubmit(JobSubmitRequestBody requestBody) throws IOException;

    String jobsMetric(String get, String agg, String jobs) throws IOException;

    /**
     * Returns an overview over all jobs.
     */
    MultipleJobsDetails jobsOverview() throws IOException;



}
