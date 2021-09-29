package com.myclass.connector.socket.source;

import com.myclass.common.config.SocketConfigOption;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;

import java.util.HashSet;
import java.util.Set;

public class SocketDynamicSourceFactory implements DynamicTableSourceFactory {

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);
        helper.validate();
        return new SocketDynamicSource(helper.getOptions(), context.getCatalogTable().getSchema().getTableColumns());
    }

    @Override
    public String factoryIdentifier() {
        return "my-socket";
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        final Set<ConfigOption<?>> required = new HashSet<>(8);
        required.add(SocketConfigOption.host);
        return required;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        final Set<ConfigOption<?>> optional = new HashSet<>(8);
        optional.add(SocketConfigOption.port);
        optional.add(SocketConfigOption.format);
        optional.add(SocketConfigOption.delimiter);
        return optional;
    }
}
