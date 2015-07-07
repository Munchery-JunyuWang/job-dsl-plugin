package javaposse.jobdsl.dsl

import static javaposse.jobdsl.dsl.DslScriptHelper.getSourceDetails
import static javaposse.jobdsl.dsl.DslScriptHelper.stackTrace

/**
 * Abstract base class providing common functionality for all {@link JobManagement} implementations.
 */
abstract class AbstractJobManagement implements JobManagement {
    final PrintStream outputStream

    protected AbstractJobManagement(PrintStream out) {
        this.outputStream = out
    }

    @Override
    boolean createOrUpdateConfig(String path, String config, boolean ignoreExisting) {
         Item item = new Item(this) {
            @Override
            String getName() {
                path
            }

            @Override
            String getXml() {
                config
            }

            @Override
            Node getNode() {
                throw new UnsupportedOperationException()
            }
        }
        createOrUpdateConfig(item, ignoreExisting)
    }

    @Override
    void logDeprecationWarning() {
        List<StackTraceElement> currentStackTrack = stackTrace
        String details = getSourceDetails(currentStackTrack)
        logDeprecationWarning(currentStackTrack[0].methodName, details)
    }

    @Override
    void logDeprecationWarning(String subject) {
        logDeprecationWarning(subject, sourceDetails)
    }

    @Override
    void logDeprecationWarning(String subject, String scriptName, int lineNumber) {
        logDeprecationWarning(subject, getSourceDetails(scriptName, lineNumber))
    }

    protected void logDeprecationWarning(String subject, String details) {
        logWarningWithDetails("${subject} is deprecated", details)
    }

    protected static void validateUpdateArgs(String jobName, String config) {
        validateNameArg(jobName)
        validateConfigArg(config)
    }

    protected static void validateConfigArg(String config) {
        if (config == null || config.empty) {
            throw new ConfigurationMissingException()
        }
    }

    protected static void validateNameArg(String name) {
        if (name == null || name.empty) {
            throw new NameNotProvidedException()
        }
    }

    protected void logWarningWithDetails(String message, String details = getSourceDetails()) {
        logWarning('(%s) %s', details, message)
    }

    protected void logWarning(String message, Object... args) {
        outputStream.printf("Warning: $message\n", args)
    }
}
