package cn.sliew.flink.gateway.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class FlinkSettingGetResponse implements Response {

    @Serial
    private static final long serialVersionUID = -2229834978406030711L;

    /**
     * flink 版本
     */
    private String flinkVersion;

    /**
     * scala 版本
     */
    private String scalaVersion;

    /**
     * java 版本
     */
    private String javaVersion;

    /**
     * flink 配置目录。支持 file、hdfs、s3 协议
     */
    private String flinkHome;

    /**
     * 描述
     */
    private String desc;
}
