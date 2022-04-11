package cn.sliew.flink.gateway.dao.entity.gateway.flink;

import cn.sliew.flink.gateway.dao.entity.BaseEntityExt;
import lombok.Getter;
import lombok.Setter;

/**
 * flink artifact
 */
@Getter
@Setter
public class FlinkArtifact extends BaseEntityExt {

    /**
     * artifact 名称
     */
    private String name;

    /**
     * artifact 链接。支持 file、hdfs、s3 协议
     */
    private String url;

    /**
     * 描述
     */
    private String desc;
}
