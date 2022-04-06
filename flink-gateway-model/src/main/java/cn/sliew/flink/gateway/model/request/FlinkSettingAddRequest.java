package cn.sliew.flink.gateway.model.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class FlinkSettingAddRequest implements Request {

    @Serial
    private static final long serialVersionUID = -5529583791782492448L;

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
