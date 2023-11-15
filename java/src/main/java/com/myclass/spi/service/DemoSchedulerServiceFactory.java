package com.myclass.spi.service;

import com.myclass.spi.api.SchedulerService;
import com.myclass.spi.api.SchedulerServiceFactory;

public class DemoSchedulerServiceFactory implements SchedulerServiceFactory {

    @Override
    public SchedulerService create() {
        return new DemoSchedulerService();
    }

    @Override
    public String getIdentify() {
        return "demo";
    }
}
