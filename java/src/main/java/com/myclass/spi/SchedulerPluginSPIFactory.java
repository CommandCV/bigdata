package com.myclass.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class SchedulerPluginSPIFactory<T extends SchedulerPluginSPI> {

    private final Map<String, T> pluginServiceMap = new HashMap<>();

    public SchedulerPluginSPIFactory(Class<T> pluginSpiClass) {
        ServiceLoader<T> pluginServices = ServiceLoader.load(pluginSpiClass);
        for (T pluginService : pluginServices) {
            if (pluginServiceMap.containsKey(pluginService.getIdentify())) {
                throw new IllegalStateException(
                        "Duplicate plugin name: " + pluginService.getIdentify());
            } else {
                pluginServiceMap.put(pluginService.getIdentify(), pluginService);
            }
        }
    }

    public Map<String, T> getPluginServiceMap() {
        return new HashMap<>(pluginServiceMap);
    }

}
