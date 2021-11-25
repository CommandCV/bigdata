package com.myclass;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;

public class FlinkApplication {

    /**
     * 环境设置
     */
    protected static EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();

    /**
     * 流式处理环境
     */
    protected static StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();


}
