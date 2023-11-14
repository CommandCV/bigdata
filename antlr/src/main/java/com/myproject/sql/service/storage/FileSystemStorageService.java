package com.myproject.sql.service.storage;

import com.myproject.sql.config.Configuration;
import com.myproject.sql.enums.StorageTypeEnum;
import com.myproject.sql.model.ParameterBinding;
import com.myproject.sql.model.Sorting;
import com.myproject.sql.service.StorageService;

import java.util.List;

public class FileSystemStorageService implements StorageService {

    private final Configuration configuration;

    private final String dataPath;

    public FileSystemStorageService() {
        this.configuration = new Configuration().storageType(StorageTypeEnum.FILE_SYSTEM);
        this.dataPath = configuration.getDataPath();
    }

    public FileSystemStorageService(Configuration configuration) {
        this.configuration = configuration;
        this.dataPath = configuration.getDataPath();
    }

    @Override
    public void createDatabase(String database) {

    }

    @Override
    public void dropDatabase(String database) {

    }

    @Override
    public void createTable(String database, String table) {

    }

    @Override
    public void dropTable(String database, String table) {

    }

    @Override
    public void addColumn(String database, String table, int index, Object defaultValue) {

    }

    @Override
    public void dropColumn(String database, String table, int index) {

    }

    @Override
    public void insert(String database, String table, List<List<Object>>  row) {

    }

    @Override
    public void delete(String database, String table, List<ParameterBinding> filter) {

    }

    @Override
    public List<List<Object>> query(String database, String table, List<Integer> select, List<ParameterBinding> filter, Sorting sorting) {
        return null;
    }
}
