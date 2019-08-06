package com.js.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ThreadLoggingSupport {

    private static final Map<Long, Boolean> THREAD_LOG_ALL = new HashMap<>();
    private static final Map<Long, Boolean> THREAD_LOG_DEBUG = new HashMap<>();

    public static void logEverything(boolean enabled) {
    	THREAD_LOG_ALL.put(Thread.currentThread().getId(), enabled);
    }
    
    public static void logDebug(boolean enabled) {
    	THREAD_LOG_DEBUG.put(Thread.currentThread().getId(), enabled);
    }

    public static boolean shouldLogEverything() {
        return Optional.ofNullable(THREAD_LOG_ALL.get(Thread.currentThread().getId()))
                .orElse(false);
    }
    
    public static boolean shouldLogDebug() {
        return Optional.ofNullable(THREAD_LOG_DEBUG.get(Thread.currentThread().getId()))
                .orElse(false);
    }

    public static void cleanup() {
    	THREAD_LOG_ALL.remove(Thread.currentThread().getId());
    	THREAD_LOG_DEBUG.remove(Thread.currentThread().getId());
    }
}
