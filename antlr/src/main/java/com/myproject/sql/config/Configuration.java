package com.myproject.sql.config;

import com.myproject.sql.enums.StorageTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
public class Configuration {

    @Builder.Default
    private StorageTypeEnum storageType = StorageTypeEnum.IN_MEMORY;

    @Builder.Default
    private String dataPath = Path.of(System.getProperty("java.io.tmpdir") + "/database_file").toString();


    public Configuration() {
    }

    public Configuration storageType(StorageTypeEnum storageType) {
        this.storageType = storageType;
        return this;
    }

    public Configuration dataPath(String path) {
        this.dataPath = path;
        return this;
    }
}
