package com.myclass;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;

public abstract class FlinkApplication {

    /**
     * flink配置
     */
    protected static Configuration conf = new Configuration();

    /**
     * 环境设置
     */
    protected static EnvironmentSettings settings;

    /**
     * 流式处理环境
     */
    protected static StreamExecutionEnvironment sEnv;

    static {
        settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        sEnv = StreamExecutionEnvironment.getExecutionEnvironment(conf);
    }

}
