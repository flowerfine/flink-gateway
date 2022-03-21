package cn.sliew.flink.engine.web.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flink")
@Tag(name = "/flink", description = "Flink 接口")
public class FlinkController {

    @DeleteMapping("cluster")
    @ApiOperation("关闭集群")
    public void shutdown() {

    }
}
