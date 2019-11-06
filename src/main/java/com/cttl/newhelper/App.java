package com.cttl.newhelper;

import com.cttl.newhelper.ui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

//        java.net.URL url = getClass().getResource("com/cttl/newhelper/ui/main.fxml");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/main.fxml"));
        BorderPane borderPane = loader.load();
        Scene scene = new Scene(borderPane,800,720);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("小助手");
        primaryStage.getIcons().add(new Image(getClass().getResource("ui/data-find-icon.png").toString()));
        primaryStage.show();
        MainController.setMainStage(primaryStage);
    }

    public static void main(String[] args ){
        launch(args);
    }
}
