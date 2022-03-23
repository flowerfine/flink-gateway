package cn.sliew.flink.engine.web.controller.flink;

import cn.sliew.flink.gateway.engine.RestEndpoint;
import cn.sliew.flink.gateway.engine.impl.RestEndpointImpl;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagerDetailsInfo;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagersInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flink/task-manager")
public class TaskManagerController {

    private RestEndpoint endpoint = new RestEndpointImpl("http://localhost:8081");

    @GetMapping("taskmanagers")
    public TaskManagersInfo taskmanagers() throws IOException {
        return endpoint.taskManagers();
    }

    /**
     * todo 拆开 get, agg, taskmanagers
     */
    @GetMapping("taskmanagers/metrics")
    public List<Map> taskMangersMetrics() throws IOException {
        return endpoint.taskManagersMetrics(null, null, null);
    }

    @GetMapping("taskmanagers/{taskManagerId}")
    public TaskManagerDetailsInfo taskmanagerDetail(@PathVariable("taskManagerId") String taskManagerId) throws IOException {
        return endpoint.taskManagerDetail(taskManagerId);
    }




}
