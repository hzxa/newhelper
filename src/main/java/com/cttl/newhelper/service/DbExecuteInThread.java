package com.cttl.newhelper.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class DbExecuteInThread implements Callable<String>{

    private DataBaseService dataBaseService;

    private String sql;

    public DbExecuteInThread(DataBaseService dataBaseService, String sql) {
        this.dataBaseService = dataBaseService;
        this.sql = sql;
    }

    @Override
    public String call() throws Exception {
        try{
            dataBaseService.excuteSql(sql);
        }catch (Exception e){
            throw  new ExecutionException(e.getCause());
        }
        return "";
    }
}
