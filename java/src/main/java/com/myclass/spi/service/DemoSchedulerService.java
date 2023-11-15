package com.myclass.spi.service;

import com.myclass.spi.api.SchedulerService;

public class DemoSchedulerService implements SchedulerService {

    @Override
    public void createWorkflow() {
        System.out.println("create workflow");
    }

    @Override
    public void updateWorkflow() {
        System.out.println("update workflow");
    }

    @Override
    public void deleteWorkflow() {
        System.out.println("delete workflow");
    }

    @Override
    public void submitWorkflow() {
        System.out.println("submit workflow");
    }
}
