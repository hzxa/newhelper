package com.cttl.newhelper.ui;

import com.cttl.newhelper.domain.ColumnInfo;
import com.cttl.newhelper.service.DataBaseService;
import com.cttl.newhelper.service.DbExecuteInThread;
import com.cttl.newhelper.utils.AlterUtil;
import com.cttl.newhelper.utils.LogUtil;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MainController {

    private static Stage mainStage;

    private StartTaskController startTaskController;

    private TableDialogController tableDialogController;

    @FXML
    private MenuItem startMenuItem;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private TextArea centerTextField;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progressBarInd;

    @FXML
    private Label progressStatus;

    private ThreadPoolExecutor executorService;

    public static void setMainStage(Stage stage){
        mainStage = stage;
    }

    @FXML
    private void initialize(){
        try{
            FXMLLoader fldr = new FXMLLoader(getClass().getResource("startTask.fxml"));
            Parent parent = fldr.load();
            startTaskController = fldr.getController();
            startTaskController.setControllerScene(new Scene(parent));
            startTaskController.setControllerStage(new Stage(StageStyle.UTILITY));
            startTaskController.setMainController(this);
            fldr = new FXMLLoader(getClass().getResource("tableDialog.fxml"));
            parent= fldr.load();
            tableDialogController = fldr.getController();
            tableDialogController.setControllerScene(new Scene(parent));
            tableDialogController.setControllerStage(new Stage(StageStyle.UTILITY));

            LogConsole.setTextArea(centerTextField);

            executorService = new ThreadPoolExecutor(
                    1,
                    1,
                    5,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1)
            );
        }catch (Exception e){
            StringBuilder sb = new StringBuilder("初始化失败,具体原因：\n");
            String msg = e.getMessage();
            if(msg.isEmpty()&&e.getCause()!=null){
                msg = e.getCause().getMessage();
            }
            sb.append(msg);
            LogUtil.error(sb.toString());
        }
    }

    @FXML
    private void startAction(ActionEvent event){
//        LogUtil.info("startAction");
        if(executorService.getActiveCount()>0){
            AlterUtil.errorAlert("", "已有任务正在运行",null);
            return;
        }
        LogUtil.info("开始测试集");
        centerTextField.clear();
        progressBar.progressProperty().unbind();
        progressBarInd.progressProperty().unbind();
        progressStatus.textProperty().unbind();

        progressStatus.setText("未开始");
        progressBar.setProgress(0.0);
        progressBarInd.setProgress(0.0);
        startTaskController.show();

        if(startTaskController.isReadyToload()){
            progressStatus.setText("正在运行");
//            loadTestCase(startTaskController.getFile());
            DataBaseExecuteTask task = new DataBaseExecuteTask(startTaskController.getFile(),
                    DataBaseService.instance(),
                    tableDialogController);
            progressBar.progressProperty().bind(task.progressProperty());
            progressBarInd.progressProperty().bind(task.progressProperty());
            progressStatus.textProperty().bind(task.messageProperty());
            executorService.submit(task);
        }
    }

    @FXML
    private void exitAction(ActionEvent event){
//        LogUtil.info("exitAction");
        if(executorService.getActiveCount()>0){
            if(!AlterUtil.confirmationAlert("", "还有任务未完成，确认强行退出?")) {
                return;
            }
        }
        mainStage.close();
    }

    public void loadTestCase(File file){
        boolean display = true;
        try{

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
            String l = null;
            tableDialogController.setDataBaseService(DataBaseService.instance());

            List<String> lines = new ArrayList<>();
            while((l = br.readLine()) != null ){
                lines.add(l);
            }
            int lineCount = lines.size();
            int processCount = 0;
            for(String line : lines){
                line = StringUtils.trimToEmpty(line);
                if(line.isEmpty()){
                    continue;
                }
                char firstChar = line.charAt(0);
                switch (firstChar){
                    case '[':

                        if(display){
                            LogUtil.info(line.substring(1));
                        }
                        break;
                    case '+':
                        display = true;
                        break;
                    case '-':
                        display = false;
                        break;
                    case ']':
                        break;
                    case '>':
                        line = line.substring(1);
                        LogUtil.info(line);
                        String tableName = getTableName(line);
                        ColumnInfo info = DataBaseService.instance().excuteQuery(line);
                        info.setTableName(tableName);
                        tableDialogController.display(info);

                    default:
//                        sql
                        LogUtil.info(line);
                        DataBaseService.instance().excuteSql(line);
//                        DbExecuteInThread inThread = new DbExecuteInThread(DataBaseService.instance(), line);
//                        Future<String> task = executorService.submit(inThread);
//                        int count = 0;
//                        while(!task.isDone()){
//                            Thread.sleep(1000);
//                            count++;
//                            if(count % 60 ==0){
//                                LogUtil.info("ongoing...");
//                                System.out.println("ongoing" + String.valueOf(System.currentTimeMillis()));
//                                progressStatus.setText("正在运行...");
//                            }
//                        }
                        break;
                }
                processCount++;
                double progess = ((double)processCount)/lineCount;
                progressBar.setProgress(progess);
                progressBarInd.setProgress(progess);
                if(processCount == lines.size()){
                    progressStatus.setText("运行完毕");
                }
            }
        }catch (Exception e){
//            e.printStackTrace();
            progressStatus.setText("发生错误");
            LogUtil.error(e.getMessage());
        }
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
}
