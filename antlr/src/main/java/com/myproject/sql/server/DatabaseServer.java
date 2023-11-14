package com.myproject.sql.server;

import com.myproject.sql.config.Configuration;
import com.myproject.sql.service.CatalogService;
import com.myproject.sql.service.FrontendService;
import com.myproject.sql.service.StorageService;
import com.myproject.sql.service.catalog.InternalCatalogService;
import com.myproject.sql.service.frontend.InternalFrontendService;
import com.myproject.sql.service.storage.InMemoryStorageService;

import java.util.List;

public class DatabaseServer {

    private final Configuration configuration;

    private final CatalogService catalogService;

    private final StorageService storageService;

    private final FrontendService frontendService;

    public DatabaseServer() {
        this.configuration = new Configuration();
        this.catalogService = new InternalCatalogService(configuration);
        this.storageService = new InMemoryStorageService(configuration);
        this.frontendService = new InternalFrontendService(configuration, catalogService, storageService);
    }

    public DatabaseServer(Configuration configuration) {
        this.configuration = configuration;
        this.catalogService = new InternalCatalogService(configuration);
        this.storageService = new InMemoryStorageService(configuration);
        this.frontendService = new InternalFrontendService(configuration, catalogService, storageService);
    }

    public void execute(String statement) {
        frontendService.execute(statement);
    }

    public List<List<Object>> executeQuery(String statement) {
        return frontendService.executeQuery(statement);
    }

}
