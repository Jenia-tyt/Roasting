package com.friendandco.roasting.multiThread;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class ThreadPoolFix {

    private ExecutorService service;
    private int countThread;

    public void init(int countThread) {
        service = Executors.newFixedThreadPool(countThread);
        this.countThread = countThread;
    }
}
