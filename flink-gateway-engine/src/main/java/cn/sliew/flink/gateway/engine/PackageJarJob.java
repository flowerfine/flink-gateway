package cn.sliew.flink.gateway.engine;

import lombok.Getter;
import lombok.Setter;
import org.apache.flink.client.cli.CliFrontendParser;
import org.apache.flink.client.cli.ProgramOptions;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;

import java.net.URL;
import java.util.List;

/**
 * @see ProgramOptions
 */
@Getter
@Setter
public class PackageJarJob {

    /**
     * Flink program JAR file.
     */
    private String jarFilePath;

    /**
     * Class with the program entry point ("main()" method).
     * Only needed if the JAR file does not specify the class in its manifest.
     */
    private String entryPointClass;

    /**
     * Adds a URL to each user code classloader  on all nodes in the cluster.
     * The paths must specify a protocol (e.g. file://) and be accessible on all nodes (e.g. by means of a NFS share).
     * The protocol must be supported by the {@link java.net.URLClassLoader}.
     */
    private List<URL> classpaths;

    /**
     * Program arguments.
     */
    private String[] programArgs;

    /**
     * The parallelism with which to run the program.
     * Optional flag to override the default value specified in the configuration.
     */
    private int parallelism;

    /**
     * If present, runs the job in detached mode
     */
    private boolean detachedMode;

    /**
     * If the job is submitted in attached mode, perform a best-effort cluster shutdown when the CLI is terminated abruptly, e.g., in response to a user interrupt, such as typing Ctrl + C.
     */
    private boolean shutdownOnAttachedExit;

    /**
     * @see CliFrontendParser#SAVEPOINT_PATH_OPTION
     * @see CliFrontendParser#SAVEPOINT_ALLOW_NON_RESTORED_OPTION
     * @see CliFrontendParser#SAVEPOINT_RESTORE_MODE
     * @see SavepointConfigOptions#RESTORE_MODE
     */
    private SavepointRestoreSettings savepointSettings;
}
