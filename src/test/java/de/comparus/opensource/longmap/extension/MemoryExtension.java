package de.comparus.opensource.longmap.extension;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public class MemoryExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger LOGGER = Logger.getLogger(MemoryExtension.class.getName());

    private static final String MEMORY_START = "memory_start";
    private static final String MEMORY_IN_PROCESS = "memory_in_process";
    private ExtensionContext extContext;

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Method testMethod = context.getRequiredTestMethod();

        if (!testMethod.getName().equals("putMemoryUsage")
                && !testMethod.getName().equals("putJavaHashMapMemoryUsage")) {
            return;
        }

        extContext = context;
        getStore(extContext).put(MEMORY_START, Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Method testMethod = context.getRequiredTestMethod();

        if (!testMethod.getName().equals("putMemoryUsage")
                && !testMethod.getName().equals("putJavaHashMapMemoryUsage")) {
            return;
        }

        long memoryStart = getStore(context).remove(MEMORY_START, long.class);
        long memoryInProgress = getStore(context).remove(MEMORY_IN_PROCESS, long.class);

        LOGGER.info(() ->
                String.format("Method [%s] took %s bytes.", testMethod.getName(), memoryInProgress - memoryStart));
    }

    public void writeMemoryUsage() {
        getStore(extContext).put(MEMORY_IN_PROCESS, Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}