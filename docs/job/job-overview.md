# job-overview

## 任务配置

https://jxeditor.github.io/2020/12/14/Flink%E7%B3%BB%E7%BB%9F%E9%85%8D%E7%BD%AE%E5%8F%82%E6%95%B0%E4%B8%80%E8%A7%88/



https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/deployment/config/#miscellaneous-options

### 基础配置

* jar 包。
* entryClass。
* programArgs。
* classPaths。
* 并行度。

### checkpoint

* CheckpointConfig
* CheckpointingOptions
* ExecutionCheckpointingOptions
  * 最小间隔。execution.checkpointing.interval
  * checkpoint 最小时间。execution.checkpointing.min-pause

### savepoint



### logs



### resource

cpu、memory

JobManager、TaskManager

### ha



## 任务运行信息

checkpoint

savepoint

logs

jobgraph

metrics



