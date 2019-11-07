package com.cttl.newhelper.ui;

import com.cttl.newhelper.domain.ColumnInfo;
import com.cttl.newhelper.service.DataBaseService;
import com.cttl.newhelper.utils.LogUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 后台执行任务，和JavaFX Application thread区分开，便于主UI响应用户点击等事件
 *
 */
public class DataBaseExecuteTask extends Task<String> {

    private File file;
    private DataBaseService dataBaseService;
    private TableDialogController tableDialogController;

    public DataBaseExecuteTask(File file, DataBaseService dataBaseService, TableDialogController tableDialogController) {
        this.file = file;
        this.dataBaseService = dataBaseService;
        this.tableDialogController = tableDialogController;
    }

    @Override
    protected String call() throws Exception {
        boolean display = true;
        updateMessage("开始运行");
        try{

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
            String l = null;

            List<String> lines = new ArrayList<>();
            while((l = br.readLine()) != null ){
                lines.add(l);
            }
            int lineCount = lines.size();
            int processCount = 0;
            for(String line : lines){
                line = StringUtils.trimToEmpty(line);
                if(line.isEmpty()){
                    processCount++;
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
                        TablePrompt tablePrompt = new TablePrompt(info, tableDialogController);
                        FutureTask<String> futureTask = new FutureTask<>(tablePrompt);
                        Platform.runLater(futureTask);
                        String result = futureTask.get();
                    default:
                        //sql
                        LogUtil.info(line);
                        DataBaseService.instance().excuteSql(line);
                        break;
                }
                processCount++;
                updateProgress(processCount,lineCount);
                updateMessage("正在运行...");

            }
            updateProgress(processCount,lineCount);
            if(processCount == lines.size()){
                updateMessage("运行完毕");
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtil.error(e.getMessage());
            updateMessage("发生错误...");
            throw e;
        }
        return "";
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

    class TablePrompt implements Callable<String>{
        private ColumnInfo info;
        private TableDialogController tableDialogController;

        public TablePrompt(ColumnInfo info, TableDialogController tableDialogController) {
            this.info = info;
            this.tableDialogController = tableDialogController;
        }

        @Override
        public String call() throws Exception {
            tableDialogController.display(info);
            return "";
        }
    }
}

