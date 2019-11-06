package com.cttl.newhelper.ui;

import com.cttl.newhelper.domain.ColumnInfo;
import com.cttl.newhelper.service.DataBaseService;
import com.cttl.newhelper.utils.AlterUtil;
import com.cttl.newhelper.utils.LogUtil;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Map;

public class TableDialogController extends AbstractChildStageController{

    private ColumnInfo columnInfo;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button updateButton;

    @FXML
    private TableView tableView;

    private DataBaseService dataBaseService;

    @Override
    protected void show() {
        if(!isInitialized()) {
            init();
            setInitialized(true);
        }
        getControllerStage().setTitle("查看:" + this.columnInfo.getTableName());
        getControllerStage().showAndWait();
    }
    public void display(ColumnInfo columnInfo){
        populateTable(columnInfo);
        show();
    }
    public void populateTable(final ColumnInfo columnInfo){
        if(isInitialized()){
            deleteButton.setDisable(true);
            updateButton.setDisable(true);
        }
        tableView.getItems().clear();
        tableView.getColumns().clear();
        this.columnInfo = columnInfo;
        List<String> names = columnInfo.getColumnNames();

        for(int i=0;i<names.size(); i++){
            tableView.getColumns().add(createColumn(names.get(i),columnInfo));
        }
        tableView.getItems().addAll(columnInfo.getRows());
        tableView.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        columnInfo.setSelectedRow(newValue.intValue());
                        if((newValue.intValue()+1) == columnInfo.getRows().size()){
                            deleteButton.setDisable(true);
                        }else{
                            deleteButton.setDisable(false);
                        }

                    }
                }
        );
        tableView.getSelectionModel().setCellSelectionEnabled(true);
    }

    private TableColumn<Map<String,Object>, String> createColumn(final String title,
                                                                 final ColumnInfo columnInfo){
        TableColumn<Map<String,Object>,String> col = new TableColumn<>();
        col.setText(title);
        col.setCellValueFactory(new MapValueFactory(title));
        col.setOnEditCommit((TableColumn.CellEditEvent<Map<String,Object>,String> t)-> {
            Map<String,String> map = (Map)t.getTableView().getItems().get(
                    t.getTablePosition().getRow());
            if(!t.getNewValue().equals(t.getOldValue())){
                columnInfo.setChanged(true);
                updateButton.setDisable(false);
                map.put(title, t.getNewValue());
            }
        });

        Callback<TableColumn<Map<String,Object>, String>, TableCell<Map<String,Object>, String>>
                cellFactoryForMap = (TableColumn<Map<String,Object>, String> p) ->
                new TextFieldTableCell(new StringConverter() {
                    @Override
                    public String toString(Object t) {
                        if(t==null){
                            return "";
                        }
                        return String.valueOf(t);
                    }
                    @Override
                    public Object fromString(String string) {
                        if(string==null){
                            return "";
                        }
                        return string;
                    }
                });
        col.setCellFactory(cellFactoryForMap);
        return col;
    }

    @FXML
    public void addOnAction(ActionEvent event){
        LogUtil.info("TableDialogController::addOnAction");
        if(!columnInfo.isEndEmptyRowChanged()){
            return;
        }
        Map<String, Object> emptyRow = columnInfo.createEndEmptyRow();
        this.columnInfo.addRow(emptyRow);
        tableView.getItems().add(emptyRow);
        updateButton.setDisable(false);
    }
    @FXML
    public void deleteOnAction(ActionEvent event){
        LogUtil.info("TableDialogController::deleteOnAction:" + String.valueOf(columnInfo.getSelectedRow()));
        tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
        this.columnInfo.deleteRow(columnInfo.getSelectedRow());
        updateButton.setDisable(false);
    }

    @FXML
    public void closeOnAction(ActionEvent event){
//        LogUtil.info("TableDialogController::closeOnAction");
        boolean yes = true;
        if(!updateButton.isDisable()){
            yes = AlterUtil.confirmationAlert("","有更新未入库, 确认是否继续关闭，yes:关闭，不保存更新");
        }
        if(yes){
            getControllerStage().close();
        }
    }

    @FXML
    public void updateOnAction(ActionEvent event){
//        LogUtil.info("TableDialogController::updateOnAction");
        /**
         * 目前的解决方案，全部删除，再做插入操作
         * TBD: 直接连接查询对象操作
         */
        try{
            dataBaseService.updateAll(columnInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        updateButton.setDisable(true);
    }

    public void setDataBaseService(DataBaseService dataBaseService) {
        this.dataBaseService = dataBaseService;
    }
}
