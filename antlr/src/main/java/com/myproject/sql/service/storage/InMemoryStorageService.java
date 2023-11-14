package com.myproject.sql.service.storage;

import com.myproject.sql.config.Configuration;
import com.myproject.sql.enums.ColumnDataTypeEnum;
import com.myproject.sql.enums.StorageTypeEnum;
import com.myproject.sql.model.ParameterBinding;
import com.myproject.sql.model.Sorting;
import com.myproject.sql.service.StorageService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorageService implements StorageService {

    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, List<List<Object>>>> localStorage
            = new ConcurrentHashMap<>();

    private final Configuration configuration;

    public InMemoryStorageService() {
        this.configuration = new Configuration().storageType(StorageTypeEnum.IN_MEMORY);
    }

    public InMemoryStorageService(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void createDatabase(String database) {
        localStorage.put(database, new ConcurrentHashMap<>());
    }

    @Override
    public void dropDatabase(String database) {
        localStorage.values().forEach(ConcurrentHashMap::clear);
        localStorage.remove(database);
    }

    @Override
    public void createTable(String database, String table) {
        localStorage.get(database).put(table, new ArrayList<>());
    }

    @Override
    public void dropTable(String database, String table) {
        localStorage.get(database).remove(table);
    }

    @Override
    public void addColumn(String database, String table, int index, Object defaultValue) {
        List<List<Object>> rows = localStorage.get(database).get(table);
        for (List<Object> row : rows) {
            row.add(index, defaultValue);
        }
    }

    @Override
    public void dropColumn(String database, String table, int index) {
        List<List<Object>> rows = localStorage.get(database).get(table);
        for (List<Object> row : rows) {
            row.remove(index);
        }
    }

    @Override
    public void insert(String database, String table, List<List<Object>> row) {
        localStorage.get(database).get(table).addAll(row);
    }

    @Override
    public void delete(String database, String table, List<ParameterBinding> filter) {
        List<List<Object>> rows = localStorage.get(database).get(table);
        rows.removeIf(row -> this.matchRow(filter, row));
    }

    @Override
    public List<List<Object>> query(String database, String table, List<Integer> select, List<ParameterBinding> filter,
                                    Sorting sorting) {
        List<List<Object>> rows = localStorage.get(database).get(table);
        List<List<Object>> result = new ArrayList<>();
        for (List<Object> row : rows) {
            if (this.matchRow(filter, row)) {
                List<Object> resultRow = new ArrayList<>();
                for (Integer index : select) {
                    resultRow.add(row.get(index));
                }
                result.add(resultRow);
            }
        }
        this.sorting(result, sorting);
        return result;
    }

    private boolean matchRow(List<ParameterBinding> filter, List<Object> row) {
        if (filter != null) {
            for (ParameterBinding parameterBinding : filter) {
                Object value = row.get(parameterBinding.getColumnIndex());
                ColumnDataTypeEnum type = parameterBinding.getType();
                Object parameterValue;
                if (type.isNumber()) {
                    parameterValue = Long.valueOf((String) parameterBinding.getValue());
                } else {
                    parameterValue = parameterBinding.getValue();
                }
                boolean match = switch (parameterBinding.getOperate()) {
                    case EQUALS -> {
                        if (type.isNumber()) {
                            yield ((Long) value).compareTo((Long) parameterValue) == 0;
                        } else {
                            yield ((String) value).compareTo(((String) parameterValue)) == 0;
                        }
                    }
                    case GREATER_THAN -> {
                        if (type.isNumber()) {
                            yield ((Long) value).compareTo((Long) parameterValue) > 0;
                        } else {
                            yield ((String) value).compareTo(((String) parameterValue)) > 0;
                        }
                    }
                    case LESS_THAN -> {
                        if (type.isNumber()) {
                            yield ((Long) value).compareTo((Long) parameterValue) < 0;
                        } else {
                            yield ((String) value).compareTo(((String) parameterValue)) < 0;
                        }
                    }
                };
                if (!match) {
                    return false;
                }
            }
        }
        return true;
    }

    private void sorting(List<List<Object>> result, Sorting sorting) {
        if (sorting != null) {
            result.sort(new Comparator<>() {

                private final int index = sorting.getIndex();

                @Override
                public int compare(List<Object> o1, List<Object> o2) {
                    if (sorting.getSortingType().isAsc()) {
                        if (sorting.getDataType().isNumber()) {
                            return ((Long) o1.get(index)).compareTo(((Long) o2.get(index)));
                        } else {
                            return ((String) o1.get(index)).compareTo(((String) o2.get(index)));
                        }
                    } else {
                        if (sorting.getDataType().isNumber()) {
                            return ((Long) o2.get(index)).compareTo(((Long) o1.get(index)));
                        } else {
                            return ((String) o2.get(index)).compareTo(((String) o1.get(index)));
                        }
                    }
                }
            });
        }
    }

}
