package com.myproject.sql.service;

import java.util.List;

public interface FrontendService {

    void execute(String statement);

    List<List<Object>> executeQuery(String statement);

}
