package cn.sliew.flink.gateway.dao.entity.gateway.flink.deploy;

import cn.sliew.flink.gateway.common.enums.DeploymentMode;
import cn.sliew.flink.gateway.common.enums.ResourceProvider;
import cn.sliew.flink.gateway.dao.entity.BaseEntityExt;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

/**
 * flink 部署配置
 */
@Getter
@Setter
public class FlinkDeployConfig extends BaseEntityExt {

    /**
     * flink Resource。0: Standalone, 1: Native Kubernetes, 2: YARN
     */
    @TableField("resource_provider")
    private ResourceProvider resourceProvider;

    /**
     * flink 部署模式。0: Application, 1: Per-Job, 2: Session
     */
    @TableField("deploy_mode")
    private DeploymentMode deployMode;

    /**
     * yarn 配置地址或 kubernetes context 地址。支持 file、hdfs、s3 协议
     */
    @TableField("deploy_context")
    private String deployContext;

    /**
     * 描述
     */
    @TableField("desc")
    private String desc;
}