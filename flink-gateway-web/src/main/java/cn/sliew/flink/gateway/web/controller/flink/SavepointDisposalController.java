package cn.sliew.flink.gateway.web.controller.flink;

import cn.sliew.flinkful.rest.base.RestClient;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationInfo;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointDisposalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * fixme not tested!
 */
@RestController
@RequestMapping("/flink/savepoint-disposal")
public class SavepointDisposalController {

    @Autowired
    private RestClient client;

    @PostMapping("/")
    public CompletableFuture<TriggerResponse> dispose(@RequestBody SavepointDisposalRequest request) throws IOException {
        return client.savepoint().savepointDisposal(request);
    }

    @GetMapping("{triggerId}")
    public CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> disposal(@PathVariable("triggerId") String triggerId) throws IOException {
        return client.savepoint().savepointDisposalResult(triggerId);
    }
}
