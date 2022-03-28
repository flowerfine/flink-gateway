FROM flink
MAINTAINER wangqi

ARG USER_JAR
ARG USER_JAR_PATH
RUN mkdir -p $FLINK_HOME/usrlib
RUN echo $FLINK_HOME
#COPY $USER_JAR_PATH/$USER_JAR $FLINK_HOME/usrlib/$USER_JAR
COPY TopSpeedWindowing.jar $FLINK_HOME/usrlib/TopSpeedWindowing.jar