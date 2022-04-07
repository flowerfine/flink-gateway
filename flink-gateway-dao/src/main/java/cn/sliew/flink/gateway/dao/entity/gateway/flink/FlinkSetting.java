package cn.sliew.flink.gateway.dao.entity.gateway.flink;

import cn.sliew.flink.gateway.dao.entity.BaseEntityExt;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

/**
 * flink 版本配置
 */
@Getter
@Setter
public class FlinkSetting extends BaseEntityExt {

    /**
     * flink 版本
     */
    @TableField("flink_version")
    private String flinkVersion;

    /**
     * scala 版本
     */
    @TableField("scala_version")
    private String scalaVersion;

    /**
     * java 版本
     */
    @TableField("java_version")
    private String javaVersion;

    /**
     * flink 配置目录。支持 file、hdfs、s3 协议
     */
    @TableField("flink_home")
    private String flinkHome;

    /**
     * 描述
     */
    @TableField("`desc`")
    private String desc;
}