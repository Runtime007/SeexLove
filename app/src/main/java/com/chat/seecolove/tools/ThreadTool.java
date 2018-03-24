package com.chat.seecolove.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 */
public class ThreadTool {
    private static ExecutorService instance = null;

    public static synchronized ExecutorService getInstance() {
        if (instance == null) {
            instance = Executors.newFixedThreadPool(2);
            LogTool.setLog("ExecutorService getInstance","");
        }
        return instance;
    }

}
