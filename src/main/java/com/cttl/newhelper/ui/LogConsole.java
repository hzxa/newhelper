package com.cttl.newhelper.ui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class LogConsole extends OutputStream {
    private TextArea textArea;
    private static LogConsole logConsole;
    public static LogConsole instance(){
        return logConsole;
    }
    public static void setTextArea(TextArea textArea){
        logConsole = new LogConsole(textArea);
    }
    public LogConsole(TextArea textArea) {
        this.textArea = textArea;
    }

    public void appendText(String text){
        Platform.runLater(()->textArea.appendText(text));
    }

    @Override
    public void write(int b) throws IOException {
        appendText(String.valueOf((char)b));
    }
}
