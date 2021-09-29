package com.myclass.common.config;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;

public class SocketConfigOption {
    public static final ConfigOption<String> host = ConfigOptions.key("host").stringType().noDefaultValue();
    public static final ConfigOption<Integer> port = ConfigOptions.key("port").intType().defaultValue(7777);
    public static final ConfigOption<String> format = ConfigOptions.key("format").stringType().defaultValue("csv");
    public static final ConfigOption<String> delimiter = ConfigOptions.key("delimiter").stringType().defaultValue("\t");
}
