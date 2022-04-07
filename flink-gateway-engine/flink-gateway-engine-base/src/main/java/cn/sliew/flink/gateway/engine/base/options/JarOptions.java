package cn.sliew.flink.gateway.engine.base.options;

import org.apache.flink.client.cli.CliFrontendParser;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.CoreOptions;

import java.util.List;

/**
 * @see CliFrontendParser#getRunCommandOptions()
 */
public class JarOptions {

    public static final ConfigOption<String> ENTRY_POINT_CLASS =
            ConfigOptions.key("class")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("Class with the program entry point (\"main()\" method).");

    public static final ConfigOption<String> JAR_FILE_PATH =
            ConfigOptions.key("jarfile")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("Flink program JAR file.");

    public static final ConfigOption<List<String>> PROGRAM_ARGS =
            ConfigOptions.key("arguments")
                    .stringType()
                    .asList()
                    .noDefaultValue()
                    .withDescription("Program arguments.");

    public static final ConfigOption<List<String>> CLASSPATH =
            ConfigOptions.key("classpath")
                    .stringType()
                    .asList()
                    .noDefaultValue()
                    .withDescription("Adds a URL to each user code "
                            + "classloader  on all nodes in the cluster. The paths must specify a protocol (e.g. file://) and be "
                            + "accessible on all nodes (e.g. by means of a NFS share). You can use this option multiple "
                            + "times for specifying more than one URL. The protocol must be supported by the "
                            + "{@link java.net.URLClassLoader}.");

    public static final ConfigOption<Integer> PARALLELISM =
            ConfigOptions.key("parallelism")
                    .intType()
                    .defaultValue(CoreOptions.DEFAULT_PARALLELISM.defaultValue())
                    .withDescription("The parallelism with which to run the program.");

    public static final ConfigOption<Boolean> DETACHED_MODE =
            ConfigOptions.key("detached")
                    .booleanType()
                    .defaultValue(false)
                    .withDescription("If present true, runs the job in detached mode");

    public static final ConfigOption<String> RESTORE_PATH =
            ConfigOptions.key("fromSavepoint")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("Path to a savepoint to restore the job from (for example hdfs:///flink/savepoint-1537).");

    public static final ConfigOption<Boolean> ALLOW_NON_RESTORE_STATE =
            ConfigOptions.key("allowNonRestoredState")
                    .booleanType()
                    .defaultValue(false)
                    .withDescription("Allow to skip savepoint state that cannot be restored. "
                            + "You need to allow this if you removed an operator from your "
                            + "program that was part of the program when the savepoint was triggered.");

}
