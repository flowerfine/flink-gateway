package cn.sliew.flink.gateway.engine.job.graph;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.util.CollectionUtil;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Util {
    ;

    private static final String FLINK_HOME = System.getenv("FLINK_HOME");
    private static final String HADOOP_HOME = System.getenv("HADOOP_HOME");
//    private static final String HADOOP_HOME = "/Users/wangqi/Documents/software/hadoop/hadoop-3.2.1";
//    private static final String FLINK_HOME = "/Users/wangqi/Documents/software/flink/flink-1.13.6";

    private static final String FLINK_CONF_DIR = FLINK_HOME + "/conf";
    private static final String FLINK_PLUGINS_DIR = FLINK_HOME + "/plugins";
    private static final String FLINK_LIB_DIR = FLINK_HOME + "/lib";
    private static final String FLINK_EXAMPLES_DIR = FLINK_HOME + "/examples";
    public static final String FLINK_DIST_JAR = FLINK_HOME + "/lib/flink-dist_2.11-1.13.6.jar";

    private static final String HADOOP_CONF_DIR = HADOOP_HOME + "/etc/hadoop";

    public static final String JAR_FILE_PATH = FLINK_EXAMPLES_DIR + "/streaming/TopSpeedWindowing.jar";
    public static final String LOCAL_JAR_FILE_PATH = "local:///opt/flink/usrlib/TopSpeedWindowing.jar";
    public static final String ENTRY_POINT_CLASS_NAME = "org.apache.flink.streaming.examples.windowing.TopSpeedWindowing";

    public static Configuration loadConfiguration() {
        return GlobalConfiguration.loadConfiguration(FLINK_CONF_DIR, new Configuration());
    }

    public static JobGraph createJobGraph(Configuration config) throws ProgramInvocationException {
        PackagedProgram program = PackagedProgram.newBuilder()
                .setJarFile(new File(JAR_FILE_PATH))
//                .setArguments("--port", "9000", "--host", InetAddress.getLocalHost().getHostAddress())
                .setEntryPointClassName(ENTRY_POINT_CLASS_NAME)
                .build();
        return PackagedProgramUtils.createJobGraph(program, config, 1, false);
    }

    public static void addJarFiles(YarnClusterDescriptor clusterDescriptor, Configuration config) throws MalformedURLException {
        boolean isRemoteJarPath =
                !CollectionUtil.isNullOrEmpty(config.get(YarnConfigOptions.PROVIDED_LIB_DIRS));
        List<File> shipFiles = new ArrayList<>();
        File[] plugins = new File(FLINK_PLUGINS_DIR).listFiles();
        if (plugins != null) {
            for (File plugin : plugins) {
                if (plugin.isDirectory() == false) {
                    continue;
                }
                if (!isRemoteJarPath) {
                    shipFiles.addAll(Arrays.asList(plugin.listFiles()));
                }
            }
        }
        File[] jars = new File(FLINK_LIB_DIR).listFiles();
        if (jars != null) {
            for (File jar : jars) {
                if (jar.toURI().toURL().toString().contains("flink-dist")) {
                    clusterDescriptor.setLocalJarPath(new Path(jar.toURI().toURL().toString()));
                } else if (!isRemoteJarPath) {
                    shipFiles.add(jar);
                }
            }
        }
//        shipFiles.forEach(file -> System.out.println(file.getAbsolutePath()));
        clusterDescriptor.addShipFiles(shipFiles);
    }

    public static ClusterSpecification createClusterSpecification() {
        return new ClusterSpecification.ClusterSpecificationBuilder()
                .setMasterMemoryMB(2048)
                .setTaskManagerMemoryMB(2048)
                .setSlotsPerTaskManager(2)
                .createClusterSpecification();
    }
}
