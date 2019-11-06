package com.cttl.newhelper.domain;

import com.cttl.newhelper.constants.SqlTypeConstant;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColumnInfo {
    private String tableName;
    private Integer selectedRow;
    private boolean changed;
    private Integer rowNum;
    private ObservableList<Map<String, Object>> rows;
    private ObservableList<Map<String, Object>> rowsBak;
    private List<String> columnNames;
    private List<Integer> columnTypes;

    public ColumnInfo() {
        columnNames = new ArrayList<>();
        columnTypes = new ArrayList<>();
        rows =  FXCollections.observableArrayList();
        rowsBak = FXCollections.observableArrayList();
    }

    public void addColumnName(final String name){
        columnNames.add(name);
    }
    public void addColumnType(int type){
        columnTypes.add(type);
    }



    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<Integer> getColumnTypes() {
        return columnTypes;
    }

    public ObservableList<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(ObservableList<Map<String, Object>> rows) {
        this.rows = rows;
    }

    public void addRow(Map<String, Object> row){
        this.rows.add(row);
    }

    public Map<String, Object> createEndEmptyRow(){
        Map<String, Object> m = new HashMap<>();
        for(int i=0;i<getColumnNames().size(); i++){
            m.put(columnNames.get(i), null);
        }
        return m;
    }

    public boolean isEndEmptyRowChanged(){
        Map<String, Object> row = this.rows.get(this.rows.size()-1);
        for(Object o : row.values()){
            if (o != null){
                return true;
            }
        }
        return false;
    }
    public void addEndRow(){
        Map<String, Object> m = new HashMap<>();
        for(int i=0;i<getColumnNames().size(); i++){
            m.put(columnNames.get(i), null);
        }
    }

    public void deleteRow(Integer rowNum){
        rows.remove(rowNum);
    }
    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }


    public String updateRow(Integer rowNum){
        Map<String,Object> row = rows.get(rowNum);
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String deleteAll(){
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(this.tableName).append(";");
        return sb.toString();
    }
    public List<String> getAllInsert(){
        List<String> list = new ArrayList<>();
//        INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....);
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" ").append("(");
        for(int i=0; i<columnNames.size();i++){
            sb.append(columnNames.get(i));
            if(i != (columnNames.size() - 1)){
                sb.append(",");
            }
        }
        sb.append(") VALUES (");
        String prefix = sb.toString();
        for(int i=0; i< rows.size(); i++){
            StringBuilder sb2 = new StringBuilder(prefix);
            for(int j=0; j<columnNames.size();j++){
                if(SqlTypeConstant.STRING.equals(getSqlFieldType(columnTypes.get(j)))){

                }
                sb2.append(rows.get(i).get(columnNames.get(j)));
                if( j != (columnNames.size()-1)){
                    sb2.append(",");
                }
            }
            sb2.append(");");
            list.add(sb2.toString());
        }
        return list;
    }

    private Integer getSqlFieldType(Integer sqlType){
        Integer result = SqlTypeConstant.STRING;
        switch (sqlType){
            case Types.BIT:
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.DECIMAL:
                result = SqlTypeConstant.NUMBER;
                break;
            default:
                result = SqlTypeConstant.STRING;
                break;
        }
        return result;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Integer getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(Integer selectedRow) {
        this.selectedRow = selectedRow;
    }
}
