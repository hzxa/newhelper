package com.cttl.newhelper.domain;

public class DataBaseConfig {
    private String host;
    private String user;
    private String pwd;
    private String port;
    private String dbName;
    private String jufangTableName;
    private String yibiaoTableName;

    private static volatile DataBaseConfig dataBaseConfig;
    public static DataBaseConfig instance(){
        if(dataBaseConfig == null){
            synchronized (DataBaseConfig.class){
                if(dataBaseConfig == null){
                    dataBaseConfig = new DataBaseConfig();
                }
            }
        }
        return dataBaseConfig;
    }
    private DataBaseConfig() {
        host = "localhost";
        user = "root";
        pwd = "root";
        port = "3306";
        dbName = "dbtest";
        jufangTableName = "局方话单";
        yibiaoTableName = "仪表话单";
    }


    public void reset(){

    }
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getJufangTableName() {
        return jufangTableName;
    }

    public void setJufangTableName(String jufangTableName) {
        this.jufangTableName = jufangTableName;
    }

    public String getYibiaoTableName() {
        return yibiaoTableName;
    }

    public void setYibiaoTableName(String yibiaoTableName) {
        this.yibiaoTableName = yibiaoTableName;
    }
    public String getUrl(){
        //private static final String URL = "jdbc:mysql://localhost:3306";
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:mysql://").append(host).append(":").append(port);
        return sb.toString();
    }
}
