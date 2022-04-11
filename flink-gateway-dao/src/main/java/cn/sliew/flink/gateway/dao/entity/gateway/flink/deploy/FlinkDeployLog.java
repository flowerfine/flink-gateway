package cn.sliew.flink.gateway.dao.entity.gateway.flink.deploy;

import cn.sliew.flink.gateway.dao.entity.BaseEntityExt;
import lombok.Getter;
import lombok.Setter;

/**
 * flink 部署日志
 */
@Getter
@Setter
public class FlinkDeployLog extends BaseEntityExt {

    /**
     * 部署配置 id
     */
    private Long deployConfigId;

    /**
     * flink 配置 id
     */
    private Long settingId;

    /**
     * flink cluster id
     */
    private String clusterId;

    /**
     * flink web-ui 地址
     */
    private String webInterfaceUrl;

    /**
     * 集群状态。运行或者关闭
     */
    private Integer status;
}
