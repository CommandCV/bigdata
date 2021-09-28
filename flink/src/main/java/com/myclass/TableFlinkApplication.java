package com.myclass;

import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class TableFlinkApplication extends FlinkApplication {

    /**
     * 流式表执行环境
     */
    protected static StreamTableEnvironment tEnv = StreamTableEnvironment.create(sEnv, settings);;
}
