package cn.sliew.flink.gateway.web.controller.flink;

import cn.sliew.flinkful.rest.base.RestClient;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationInfo;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.dataset.ClusterDataSetListResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/flink/datasets")
public class DataSetController {

    @Autowired
    private RestClient client;

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
