package com.cttl.newhelper.ui;

import com.cttl.newhelper.constants.SqlOpConstant;
import com.cttl.newhelper.domain.DataBaseConfig;
import com.cttl.newhelper.service.DataBaseService;
import com.cttl.newhelper.utils.AlterUtil;
import com.cttl.newhelper.utils.LogUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class StartTaskController extends AbstractChildStageController {

    @FXML
    private TextField dbHost;

    @FXML
    private TextField dbUser;

    @FXML
    private TextField dbPwd;

    @FXML
    private TextField dbPort;

    @FXML
    private TextField dbName;

    @FXML
    private TextField jufangTableName;

    @FXML
    private TextField yibiaoTableName;

    @FXML
    private TextField testCasePath;

    private boolean dbCheckOk;

    private File file;

    private String locationHistory;

    private MainController mainController;

    private boolean readyToload;


    @Override
    protected void init() {
        super.init();

        getControllerStage().setAlwaysOnTop(true);
        getControllerStage().setResizable(false);
        setInitialized(true);
        dbHost.setText(DataBaseConfig.instance().getHost());
        dbHost.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                DataBaseConfig.instance().setHost(newValue);
            }
        });
        dbPort.setText(DataBaseConfig.instance().getPort());
        dbPort.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                DataBaseConfig.instance().setPort(newValue);
            }
        });
        dbUser.setText(DataBaseConfig.instance().getUser());
        dbUser.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                DataBaseConfig.instance().setUser(newValue);
            }
        });
        dbPwd.setText(DataBaseConfig.instance().getPwd());
        dbPwd.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                DataBaseConfig.instance().setPwd(newValue);
            }
        });
        dbName.setText(DataBaseConfig.instance().getDbName());
        dbName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                DataBaseConfig.instance().setDbName(newValue);
            }
        });
        jufangTableName.setText(DataBaseConfig.instance().getJufangTableName());
        jufangTableName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                DataBaseConfig.instance().setJufangTableName(newValue);
            }
        });
        yibiaoTableName.setText(DataBaseConfig.instance().getYibiaoTableName());
        yibiaoTableName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                DataBaseConfig.instance().setYibiaoTableName(newValue);
            }
        });
//        dbHost.onActionProperty()
    }

    public void setMainController(MainController mainController){
        this.mainController = mainController;
    }
    @Override
    protected void show() {
        if(!isInitialized()) {
            init();
        }
        readyToload = false;
        getControllerStage().setTitle("配置测试任务参数");
        getControllerStage().showAndWait();
    }

    @FXML
    private void onInputChange(InputMethodEvent event){
        LogUtil.info(dbHost.getText());
    }
    @FXML
    private void startTestDataBaseAction(ActionEvent event){
//        LogUtil.info("startTestDataBaseAction");
        String result = DataBaseService.instance().checkStatus();
        if( result.isEmpty()){
            dbCheckOk = true;
            AlterUtil.infoAlert("","数据库测试通过",getControllerStage());
        }else{
            dbCheckOk = false;
            AlterUtil.errorAlert("",result,getControllerStage());
        }
    }

    @FXML
    private void  startLoadTestcase(ActionEvent event){
//        LogUtil.info("startLoadTestcase");
        final FileChooser fileChooser = new FileChooser();
        if(this.file != null){
            fileChooser.setInitialDirectory(file.getParentFile());
        }
        File file = fileChooser.showOpenDialog(getControllerStage());
        if(file != null){
            testCasePath.setText(file.toPath().toString());
            LogUtil.info("加载测试集:" + file.toPath().toString());
            this.file = file;

        }else{
            this.file = null;
            testCasePath.setText("");
        }

    }
    @FXML
    private void startTestAction(ActionEvent event){
//        LogUtil.info("startTestAction");
        if(!dbCheckOk){
            AlterUtil.errorAlert("","请先通过数据库连接测试", getControllerStage());
            return;
        }
        if(file == null){
            AlterUtil.errorAlert("","请选择测试集", getControllerStage());
            return;
        }
        this.readyToload = true;
        getControllerStage().close();
    }

    private String getTableName(String line){
        //format: SELECT APN表.* FROM APN表;
        String[] tokens = line.split("\\s+");
        String tableName = null;
        for(int i=0; i<tokens.length; i++){
            if("from".equals(tokens[i].toLowerCase())){
                if(i+1 < tokens.length){
                    return StringUtils.removeEnd(tokens[i+1], ";");
                }
            }
        }
        return "";
    }

    /**
     * 确定sql语句的操作类型: 查询，插入，更新，删除，建表
     * @param line
     * @return
     */
    private Integer sqlOperation(String line){
        String[] tokens =  line.split("\\s+");
        if ("select".equals(tokens[0].toLowerCase())){
            return SqlOpConstant.OPERATION_QUERY;
        }else if("insert".equals(tokens[0].toLowerCase())){
            return SqlOpConstant.OPERATION_INSERT;
        }else if("update".equals(tokens[0].toLowerCase())){
            return SqlOpConstant.OPERATION_UPDATE;
        }else if("delete".equals(tokens[0].toLowerCase())){
            return SqlOpConstant.OPERATION_DELETE;
        }else if("create".equals(tokens[0].toLowerCase())){
            return SqlOpConstant.OPERATION_CREATE;
        }else if("drop".equals(tokens[0].toLowerCase())){
            return SqlOpConstant.OPERATION_DROP;
        }else if("alter".equals(tokens[0].toLowerCase())){
            return SqlOpConstant.OPERATION_ALTER;
        }
        return null;
    }
    @FXML
    private void cancelTaskAction(ActionEvent event){
//        LogUtil.info("cancelTaskAction");
        getControllerStage().close();
    }

    @FXML
    private void onMouseDrapExitedAction(){

    }

    @FXML
    private void onMouseExitedAction(){

    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isReadyToload() {
        return readyToload;
    }

    public void setReadyToload(boolean readyToload) {
        this.readyToload = readyToload;
    }
}
