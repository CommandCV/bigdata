package com.myproject.sql.service;

import com.myproject.sql.model.ParameterBinding;
import com.myproject.sql.model.Sorting;

import java.util.List;

public interface StorageService {

    void createDatabase(String database);

    void dropDatabase(String database);

    void createTable(String database, String table);

    void dropTable(String database, String table);

    void addColumn(String database, String table, int index, Object defaultValue);

    void dropColumn(String database, String table, int index);

    void insert(String database, String table, List<List<Object>> row);

    void delete(String database, String table, List<ParameterBinding> filter);

    List<List<Object>> query(String database, String table, List<Integer> select, List<ParameterBinding> filter,
                             Sorting sorting);

}
