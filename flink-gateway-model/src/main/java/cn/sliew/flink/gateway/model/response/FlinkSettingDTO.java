package cn.sliew.flink.gateway.model.response;

import cn.sliew.flink.gateway.model.result.Result;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class FlinkSettingDTO implements Result {

    @Serial
    private static final long serialVersionUID = 6675017308545212501L;

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
