package com.cttl.newhelper.ui;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class AbstractChildStageController {
    private Stage controllerStage;
    private Scene controllerScene;
    private boolean initialized;
    protected abstract void show();

    protected void init(){
        getControllerStage().setScene(getControllerScene());
        getControllerStage().initModality(Modality.APPLICATION_MODAL);
    }

    private void setupCloseStageShortcuts(){
        //cMD || CTL + W depending on platform
        getControllerScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.W, KeyCombination.META_DOWN), () -> {
                    getControllerStage().close();
                }
        );
    }

    protected void setControllerStage(Stage stg){
        controllerStage = stg;
    }

    protected Stage getControllerStage() {
        return controllerStage;
    }

    protected Scene getControllerScene() {
        return controllerScene;
    }

    protected void setControllerScene(Scene controllerScene) {
        this.controllerScene = controllerScene;
    }

    protected boolean isInitialized() {
        return initialized;
    }

    protected void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
