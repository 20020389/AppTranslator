package sample;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import sample.App.TTS;

import java.io.File;
import java.io.IOException;


public class Main extends Application {
    public static enum Status{
        LOGIN,
        HOME
    }
    private static Scene RENDER;
    public static Stage WINDOW;
    public static Status status = Status.LOGIN;
    public static EventHandler<WindowEvent> EVENT_CLOSE = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent layout = FXMLLoader.load(getClass().getResource("LoginApp/FormLogin.fxml"));
        Scene root = new Scene(layout);
        primaryStage.setTitle("Google Translate");
        primaryStage.setScene(root);
        primaryStage.setResizable(false);
        primaryStage.show();
        RENDER = root;
        WINDOW = primaryStage;
        Main.WINDOW.setOnCloseRequest(EVENT_CLOSE);
    }


    public static void changeWindow(String link, double w, double h) throws IOException {
        WINDOW.setOpacity(0);
        Parent layout = FXMLLoader.load(Main.class.getResource(link));
        RENDER.setRoot(layout);
        Stage primeStage = new Stage();
        primeStage.setScene(RENDER);
        primeStage.setWidth(w);
        primeStage.setHeight(h);
        primeStage.centerOnScreen();
        primeStage.setTitle("Snow Translator");
//        primeStage.setResizable(false);
        primeStage.setMinHeight(480);
        primeStage.setMinWidth(520);
        primeStage.setOnHidden(e -> Platform.exit());   //xóa các dialog khi tab chính đóng
        layout.setOpacity(0);
        primeStage.show();
        FadeTransition transition = new FadeTransition(Duration.millis(1000),layout);
        transition.setToValue(1);
        transition.play();
        WINDOW.close();
        WINDOW = primeStage;
        status = Status.HOME;
    }

    @Override
    public void stop() {
        File myObj = new File("src/main/Resource/assets/voice/voice.mp3");
        myObj.delete();
        System.out.println("Delete done!");
        if (TTS.thr != null) {
            if (TTS.Running) {
                TTS.thr.stop();
                System.out.println("Stoped Thread");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
