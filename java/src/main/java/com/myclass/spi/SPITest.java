package com.myclass.spi;

import com.myclass.spi.api.SchedulerService;
import com.myclass.spi.api.SchedulerServiceFactory;

public class SPITest {

    public static void main(String[] args) {
        SchedulerPluginSPIFactory<SchedulerServiceFactory> schedulerPluginSPIFactory = new SchedulerPluginSPIFactory<>(
                SchedulerServiceFactory.class);
        SchedulerServiceFactory factory = schedulerPluginSPIFactory.getPluginServiceMap()
                .get("demo");
        SchedulerService schedulerService = factory.create();
        schedulerService.createWorkflow();
        schedulerService.deleteWorkflow();
    }

}
