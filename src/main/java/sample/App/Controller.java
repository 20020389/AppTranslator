package sample.App;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import sample.App.addword.AddwordController;
import sample.App.home.TranslateController;
import sample.App.remove.RemoveController;
import sample.App.setting.JsonReader;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private StackPane workPane;
    @FXML
    private Pane basePanel;
    @FXML
    private Button homeTrans;
    @FXML
    private Button settingTrans;
    @FXML
    private Button addTrans;
    @FXML
    private Button removeTrans;
    @FXML
    private Button refreshTrans;
    @FXML
    private Pane backGround;
    @FXML
    private AnchorPane baseRefresh;

    private RotateTransition rfTransition;
    private JsonReader reader;
    private double checkBG;
    private int choosing = 0;
    private Pane formHome;
    private Pane formSetting;
    private Pane formAdd;
    private Pane formRemove;
    LinkedList<Button> menuButton;
    boolean[] bt_hover;
    public static Pane CpyBackground;
    public static LinkedList<Runnable> TASK;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            TASK = new LinkedList<>();
            bt_hover = new boolean[4];
            menuButton = new LinkedList<>();
            reader = new JsonReader();
            formHome = FXMLLoader.load(getClass().getResource("home/FormHome.fxml"));
            formSetting = FXMLLoader.load(getClass().getResource("setting/FormSetting.fxml"));
            formAdd = FXMLLoader.load(getClass().getResource("addword/FormAddword.fxml"));
            formRemove = FXMLLoader.load(getClass().getResource("remove/RemoveForm.fxml"));
            basePanel.getChildren().clear();
            basePanel.getChildren().add(formHome);
            addBtMenu(menuButton, bt_hover);
            menuButton.get(0).styleProperty().set("-fx-background-color: #3d4559");
        } catch (IOException e) {
            e.printStackTrace();
        }
        backGround.styleProperty().set("-fx-background-image: url('" + reader.get_Link() + "');-fx-opacity:" + reader.get_Opacity() + ";");
        CpyBackground = backGround;
        rfTransition = new RotateTransition(Duration.millis(1000), refreshTrans);
        rfTransition.setFromAngle(0);
        rfTransition.setToAngle(360);
        workPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            TranslateController.eventResize(workPane);
            AddwordController.moveButton(workPane);
            RemoveController.moveButton(workPane);
        });

        workPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            TranslateController.eventResize(workPane);
            AddwordController.moveButton(workPane);
            RemoveController.moveButton(workPane);
            baseRefresh.setPrefHeight(workPane.getHeight() - 200);
        });
    }

    private void addBtMenu(LinkedList<Button> e, boolean[] h) {
        e.add(homeTrans);
        e.add(settingTrans);
        e.add(addTrans);
        e.add(removeTrans);
        h[0] = false;
        h[1] = false;
        h[2] = false;
        h[3] = false;
    }

    public void homeClick(ActionEvent event) {
        if (choosing != 0) {
            menuButton.get(choosing).styleProperty().set("-fx-background-color: transparent");
            choosing = 0;
            menuButton.get(choosing).styleProperty().set("-fx-background-color: #3d4559");
            basePanel.getChildren().clear();
            basePanel.getChildren().add(formHome);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), workPane);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();
        }
    }

    public void settingClick(ActionEvent event) {
        if (choosing != 1) {
            menuButton.get(choosing).styleProperty().set("-fx-background-color: transparent");
            choosing = 1;
            menuButton.get(choosing).styleProperty().set("-fx-background-color: #3d4559");
            basePanel.getChildren().clear();
            basePanel.getChildren().add(formSetting);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), workPane);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();
        }
    }

    public void addClick(ActionEvent event) {
        if (choosing != 2) {
            menuButton.get(choosing).styleProperty().set("-fx-background-color: transparent");
            choosing = 2;
            menuButton.get(choosing).styleProperty().set("-fx-background-color: #3d4559");
            basePanel.getChildren().clear();
            basePanel.getChildren().add(formAdd);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), workPane);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();
        }
    }

    public void removeClick(ActionEvent event) {
        if (choosing != 3) {
            menuButton.get(choosing).styleProperty().set("-fx-background-color: transparent");
            choosing = 3;
            menuButton.get(choosing).styleProperty().set("-fx-background-color: #3d4559");
            basePanel.getChildren().clear();
            basePanel.getChildren().add(formRemove);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), workPane);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();
        }
    }

    public void refreshClick(ActionEvent actionEvent) {
        TASK.get(choosing).run();
        rfTransition.stop();
        rfTransition.play();
    }

    public static void setCpyBackground(String local, double opa) {
        CpyBackground.styleProperty().set("-fx-background-image: url(\"/assets/bg/" + local + "\");-fx-opacity: " + opa + ";");
    }

    public static void setCacheBackground(String loca, double opa) {
        if (loca != null) {
            CpyBackground.styleProperty().set("-fx-background-image: url(\"" + loca + "\");-fx-opacity: " + opa + ";");
        }
    }

    public static void setOpacity(double opa) {
        CpyBackground.styleProperty().set("-fx-opacity: " + opa + ";");
    }

}
