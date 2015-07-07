package javaposse.jobdsl.dsl

import static java.lang.Thread.currentThread
import static org.codehaus.groovy.runtime.StackTraceUtils.isApplicationClass

/**
 * Helper class for dealing with stack traces originating from DSL scripts and finding the DSL script source line that
 * led to a stack trace.
 *
 * @since 1.36
 */
class DslScriptHelper {
    private DslScriptHelper() {
    }

    static List<StackTraceElement> getStackTrace() {
        List<StackTraceElement> result = currentThread().stackTrace.findAll { isApplicationClass(it.className) }
        result[4..-1]
    }

    static String getSourceDetails() {
        getSourceDetails(stackTrace)
    }

    static String getSourceDetails(StackTraceElement[] stackTrace) {
        getSourceDetails(stackTrace as List<StackTraceElement>)
    }

    static String getSourceDetails(List<StackTraceElement> stackTrace) {
        StackTraceElement source = stackTrace.find {
            isApplicationClass(it.className) && !it.className.startsWith('javaposse.jobdsl.')
        }
        getSourceDetails(source?.fileName, source == null ? -1 : source.lineNumber)
    }

    static String getSourceDetails(String scriptName, int lineNumber) {
        String details = 'unknown source'
        if (scriptName != null) {
            details = scriptName.matches(/script\d+\.groovy/) ? 'DSL script' : scriptName
            if (lineNumber > 0) {
                details += ", line ${lineNumber}"
            }
        }
        details
    }
}
