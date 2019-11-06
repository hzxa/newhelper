package com.cttl.newhelper.utils;

import com.cttl.newhelper.ui.LogConsole;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

    public static void info(String s){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date(System.currentTimeMillis()));
        StringBuilder sb = new StringBuilder();
        sb.append(now).append(" INFO ").append(s).append("\n");
        LogConsole.instance().appendText(sb.toString());
//        System.out.printf("%s %s %s\n", now, "INFO",s);
    }
    public static void error(String s){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date(System.currentTimeMillis()));
        StringBuilder sb = new StringBuilder();
        sb.append(now).append(" ERROR ").append(s).append("\n");
        LogConsole.instance().appendText(sb.toString());
//        System.out.printf("%s %s %s\n", now, "ERROR",s);
    }
}
