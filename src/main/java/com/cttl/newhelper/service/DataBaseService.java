package com.cttl.newhelper.service;

import com.cttl.newhelper.constants.SqlOpConstant;
import com.cttl.newhelper.domain.ColumnInfo;
import com.cttl.newhelper.domain.DataBaseConfig;
import com.cttl.newhelper.utils.LogUtil;
import com.mysql.jdbc.ResultSetImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseService {
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306";
    private static final String USER = "root";
    private static final String PWD = "root";

    private DataBaseConfig property;

    private static DataBaseService dataBaseService;

    public static DataBaseService instance(){
        if (dataBaseService == null){
            synchronized (DataBaseService.class){
                if (dataBaseService == null){
                    dataBaseService = new DataBaseService(DataBaseConfig.instance());
                }
            }
        }
        return dataBaseService;

    }

    private DataBaseService(DataBaseConfig property) {
        this.property = property;
    }

    //    String myConnectionString =
//            "jdbc:mysql://localhost:3307/mydb?" +
//                    "useUnicode=true&characterEncoding=UTF-8" +
//                    "&rewriteBatchedStatements=true";
//String sql = "insert into employee (name, city, phone) values (?, ?, ?)";
//    Connection connection = new getConnection();
//    PreparedStatement ps = connection.prepareStatement(sql);
//
//for (Employee employee: employees) {
//
//        ps.setString(1, employee.getName());
//        ps.setString(2, employee.getCity());
//        ps.setString(3, employee.getPhone());
//        ps.addBatch();
//    }
//ps.executeBatch();
//ps.close();
//connection.close();

    public void createDb(final String dbName){
        Connection conn = null;
        try{
            conn = getDataBaseConnection("");
            StringBuilder sb= new StringBuilder();
            sb.append("CREATE DATABASE ");
            sb.append(dbName);
            PreparedStatement pstmt = conn.prepareStatement(sb.toString());
            pstmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String checkStatus(){
        Connection connection = null;
        try {
            connection = getDataBaseConnection(property.getDbName());

            if (connection == null) {
                return "无法连接：" + property.getDbName();
            }
        }catch (Exception e) {
            return "连接 " + property.getDbName() + " 异常，原因：" + e.getMessage();
        }finally {
            closeConnection(connection);
        }

        try{
            if (!tableExist(property.getJufangTableName())){
                return property.getJufangTableName() + " 表不存在";
            }
        }catch (Exception e){
            return "查询 " + property.getJufangTableName() + " 异常，原因：" + e.getMessage();
        }

        try{
            if (!tableExist(property.getJufangTableName())){
                return property.getYibiaoTableName() + " 表不存在";
            }
        }catch (Exception e){
            return "查询 " + property.getYibiaoTableName() + " 异常，原因：" + e.getMessage();
        }
        return "";
//            connection.close();
//            connection = getDataBaseConnection(property.getJufangTableName());
//            if(connection == null){
//                return "数据库连接错误";
//            }
//            if (!tableExist(property.getJufangTableName())){
//                return property.getJufangTableName() + " 表不存在";
//            }
//
//            if (!tableExist(property.getYibiaoTableName())){
//                return property.getYibiaoTableName() + " 表不存在";
//            }
//            return "";
//        }catch (Exception e){
//            return "连接：" + property.getDbName() + " 异常，" + e.getMessage();
//        }

    }
    private void closeConnection(final Connection connection) {
        try{
            if(connection != null ){
                connection.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private boolean tableExist(final String tableName) throws Exception{
        Connection conn = null;
        try{
            conn = getDataBaseConnection("information_schema");
            if(conn == null){
                return false;
            }
            int count = 0;

            String sql = "SELECT count(1) as count FROM `TABLES` where TABLE_SCHEMA=? and TABLE_NAME=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, property.getDbName());
            ps.setString(2, tableName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                count = rs.getInt("count");
            }
            return count==1;

        }finally {
            quietCloseConnection(conn);
        }
    }

    public void createTable(final String tableName){

    }
    public ColumnInfo excuteQuery(final String sql) throws Exception {
        Connection conn = null;
        try{
//            System.out.println("execute:"+ sql);
            conn = getDataBaseConnection(property.getDbName());
//            String sql = "SELECT count(1) as count FROM `TABLES` where TABLE_SCHEMA=? and TABLE_NAME=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if(rs == null){
                return null;
            }
            ResultSetImpl impl = (ResultSetImpl)rs;
            ResultSetMetaData metaData = impl.getMetaData();
            ColumnInfo columnInfo = new ColumnInfo();
            for(int i=1;i <=metaData.getColumnCount(); i++){
                columnInfo.addColumnName(metaData.getColumnName(i));
                columnInfo.addColumnType(metaData.getColumnType(i));
            }
            while(rs.next()){
                Map<String, Object> row = new HashMap<>();
//                ObservableList<StringProperty> list = FXCollections.observableArrayList();
                for(int i=1; i<=metaData.getColumnCount(); i++){
                    row.put(metaData.getColumnName(i), rs.getObject(i));
//                    list.add(new SimpleStringProperty(String.valueOf(rs.getObject(i))));
                }
                columnInfo.addRow(row);
            }
            columnInfo.addRow(columnInfo.createEndEmptyRow());
            if(statement != null){
                statement.close();
            }
            return columnInfo;
        }catch (Exception e){
//            e.printStackTrace();
            StringBuilder sb = new StringBuilder("运行sql语句发生错误：");
            sb.append(sql).append( "\n");
            String msg = e.getMessage();
            if(msg.isEmpty() && e.getCause()!=null){
                msg = e.getCause().getMessage();
            }
            sb.append("错误具体原因：").append(msg);
            e.printStackTrace();
            throw new Exception(sb.toString());
//            throw e;
        }finally {
            quietCloseConnection(conn);
        }
    }
    public void excuteSql(final String sql) throws Exception{
        Connection conn = null;
        try{
//            System.out.println("execute:"+ sql);
            conn = getDataBaseConnection(property.getDbName());
//            String sql = "SELECT count(1) as count FROM `TABLES` where TABLE_SCHEMA=? and TABLE_NAME=?;";
            Statement statement = conn.createStatement();
            statement.execute(sql);
            if(statement != null){
                statement.close();
            }
        }catch (Exception e){
            StringBuilder sb = new StringBuilder("运行sql语句发生错误：");
            sb.append(sql).append( "\n");
            String msg = e.getMessage();
            if(msg.isEmpty() && e.getCause()!=null){
                msg = e.getCause().getMessage();
            }
            sb.append("错误具体原因：").append(msg);
//            e.printStackTrace();
            throw new Exception(sb.toString());
        }finally {
            quietCloseConnection(conn);
        }
    }
    public Connection getDataBaseConnection(final String dbName) throws SQLException {
        try{
            Class.forName(DataBaseService.DRIVER);
            StringBuilder sb = new StringBuilder(property.getUrl());
            if (!dbName.isEmpty()){
                sb.append("/").append(dbName);
            }
            sb.append("?characterEncoding=utf-8&useSSL=true");
            return DriverManager.getConnection(sb.toString(),
                    property.getUser(),
                    property.getPwd());

        }catch (SQLException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e.getCause());
        }
    }

    public void updateAll(final ColumnInfo info) throws Exception{
        Connection conn = null;
        try{
            conn = getDataBaseConnection(property.getDbName());
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM ").append(info.getTableName()).append(";");
            Statement statement = conn.createStatement();
            statement.execute(sb.toString());
            if(statement != null){
                statement.close();
            }
            sb = new StringBuilder();
            sb.append("INSERT INTO ").append(info.getTableName()).append(" ").append("(");
            for(int i=0; i<info.getColumnNames().size();i++){
                sb.append(info.getColumnNames().get(i));
                if(i != (info.getColumnNames().size() - 1)){
                    sb.append(",");
                }
            }
            sb.append(") VALUES (");
            for(int i=0; i<info.getColumnNames().size();i++){
                sb.append("?");
                if(i != (info.getColumnNames().size() - 1)){
                    sb.append(",");
                }
            }
            sb.append(");");
            PreparedStatement ps = conn.prepareStatement(sb.toString());
            int rowNum = info.getRows().size();
            rowNum = rowNum > 0 ? (rowNum-1):0;
            for(int i=0; i<rowNum; i++){
                for(int j=0; j<info.getColumnNames().size(); j++){
                    ps.setObject(j+1, info.getRows().get(i).get(info.getColumnNames().get(j)), info.getColumnTypes().get(j));
                }
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            quietCloseConnection(conn);
        }
    }

    private void quietCloseConnection(Connection connection){
        try{
            if(connection != null){
                connection.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
