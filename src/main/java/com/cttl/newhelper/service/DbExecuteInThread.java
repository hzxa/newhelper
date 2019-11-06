package com.cttl.newhelper.service;

import java.util.concurrent.Callable;

public class DbExecuteInThread implements Callable<String>{

    private DataBaseService dataBaseService;

    private String sql;

    public DbExecuteInThread(DataBaseService dataBaseService, String sql) {
        this.dataBaseService = dataBaseService;
        this.sql = sql;
    }

    @Override
    public String call() throws Exception {
        dataBaseService.excuteSql(sql);
        return "";
    }
}
