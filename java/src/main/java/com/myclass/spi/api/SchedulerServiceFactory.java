package com.myclass.spi.api;

import com.myclass.spi.SchedulerPluginSPI;

public interface SchedulerServiceFactory extends SchedulerPluginSPI {

    SchedulerService create();

}
