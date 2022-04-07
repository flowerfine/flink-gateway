package cn.sliew.flink.gateway.web.controller.flink;

import cn.sliew.flink.gateway.engine.base.client.FlinkClient;
import cn.sliew.flink.gateway.engine.http.client.FlinkHttpClient;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationInfo;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.dataset.ClusterDataSetListResponseBody;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/datasets")
public class DataSetController {

    private FlinkClient client = new FlinkHttpClient("http://localhost:8081");

    @GetMapping("/")
    public CompletableFuture<ClusterDataSetListResponseBody> datasets() throws IOException {
        return client.dataSet().datasets();
    }

    @DeleteMapping("/{dataSetId}")
    public CompletableFuture<TriggerResponse> delete(@PathVariable("dataSetId") String dataSetId) throws IOException {
        return client.dataSet().deleteDataSet(dataSetId);
    }

    @DeleteMapping("/delete/{triggerId}")
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> deleteStatus(@PathVariable("triggerId") String triggerId) throws IOException {
        return client.dataSet().deleteDataSetStatus(triggerId);
    }
}
