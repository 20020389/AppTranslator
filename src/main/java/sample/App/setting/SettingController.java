package sample.App.setting;

import com.voicerss.tts.Voices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sample.App.Controller;
import sample.App.home.TranslateController;
import sample.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class SettingController implements Initializable {
    @FXML
    private AnchorPane settingBase;
    @FXML
    private ListView<MyItem> comboBg;              //danh sách bg
    @FXML
    private ListView<MyItem> comboVoice;
    @FXML
    private Button saveButton;                     //lưu cài đặt
    @FXML
    private Button buttonBg;
    @FXML
    private Button buttonVoice;          //nhấn để mở danh sách bg
    @FXML
    private Label restartLog;                      //thông báo restart
    @FXML
    private Label setOpacity;                      //hiện status of opacity
    @FXML
    private Slider sliderOpacity;                  //slider để cài đặt opacity
    @FXML
    private Pane sliderTrack;
    @FXML
    private CheckBox muteVoice;
    @FXML
    private CheckBox autoCorrect;

    private JsonReader reader;                      //biến đọc tệp setting
    private String bgChoosed;                              //lưu tên bg đã chọn
    private String bgChoosing;                             //lưu tên bg đang chọn
    private double opacityChoosed;                         //lưu trạng thái opacity đã cài đặt
    private String voiceChoosing;                          //giọng đang chọn
    private FileChooser addBackGroundFile;
    private String cacheBG;
    private boolean useCache;

    static private CheckBox MUTECPY;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addBackGroundFile = new FileChooser();
        addBackGroundFile.setTitle("Select a picture");
        addBackGroundFile.setInitialDirectory(new File(Paths.get("").toUri()));
        reader = new JsonReader();
        String bg[] = reader.get_Listbg();
        MyItem item;
        bgChoosed = reader.get_Link().replace("assets/bg/", "");
        bgChoosing = bgChoosed;
        buttonBg.setText(bgChoosed.replace(".jpg", "").replace(".png", ""));
        for (String i : bg) {
            item = new MyItem(i, 30);
            comboBg.getItems().add(item);
        }
        opacityChoosed = reader.get_Opacity();
        setOpacity.setText("Opacity: " + (opacityChoosed));
        sliderOpacity.setValue(opacityChoosed * 100);
        sliderTrack.styleProperty().set("-fx-background-color:"
                + " linear-gradient(to right, #7db2c2 "
                + opacityChoosed * 10000 / 100 + "%,rgba(255,255,255,0.2)"
                + opacityChoosed * 10000 / 100 + "%);");
        //sự kiện khi thay đổi bg
        comboBg.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String text = comboBg.getSelectionModel().getSelectedItem().getText();
                bgChoosing = text;
                double value = (int) sliderOpacity.getValue();
                value /= 100;
                Controller.setCpyBackground(text, value);
                text = text.replace(".jpg", "").replace(".png", "");
                buttonBg.setText(text);
                comboBg.setVisible(false);
                if (!bgChoosed.equals(bgChoosing)) {
                    saveButton.setDisable(false);
                }
                useCache = false;
            }
        });

        useCache = false;

        //sự kiện khi thay đổi opacity
        sliderOpacity.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                double value = (int) sliderOpacity.getValue();
                value /= 100;
                setOpacity.setText("Opacity: " + (value));
                sliderTrack.styleProperty().set("-fx-background-color: "
                        + "linear-gradient(to right, #7db2c2 "
                        + value * 10000 / 100 + "%,rgba(255,255,255,0.2)"
                        + value * 10000 / 100 + "%);");
                if (!useCache) {
                    Controller.setCpyBackground(bgChoosing, value);
                } else {
                    Controller.setCacheBackground(cacheBG, value);
                }
                if (value != reader.get_Opacity()) {
                    saveButton.setDisable(false);
                } else {
                    saveButton.setDisable(true);
                }
            }
        });

        addItemVoice(comboVoice);
        comboVoice.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String text = comboVoice.getSelectionModel().getSelectedItem().getText();
                if (!buttonVoice.getText().equals(text)) {
                    TranslateController.VOICERSS.setVoice(text);
                    buttonVoice.setText(text);
                }
                if (voiceChoosing.equals(buttonVoice.getText())) {
                    saveButton.setDisable(true);
                } else {
                    saveButton.setDisable(false);
                }
                comboVoice.setVisible(false);
            }
        });

        buttonVoice.setText(TranslateController.VOICESAVE.getVoice());
        muteVoice.setSelected(TranslateController.VOICESAVE.isUsevoice());
        autoCorrect.setSelected(TranslateController.VOICESAVE.isAutoCorrect());
        voiceChoosing = buttonVoice.getText();
        MUTECPY = muteVoice;
        Controller.TASK.add(() -> {
        });
    }

    //sự khiện khi nhấn buttonbg
    public void listClick(ActionEvent actionEvent) {
        comboBg.setVisible(true);
        comboVoice.setVisible(false);
    }

    //sự kiện khi unfocus buttonbg
    public void paneClick(MouseEvent mouseEvent) {
        comboBg.setVisible(false);
        comboVoice.setVisible(false);
    }

    public void voicebtClick(ActionEvent actionEvent) {
        comboVoice.setVisible(true);
        comboBg.setVisible(false);
    }

    //sự kiện nhấn nút save
    public void saveAllSetting(ActionEvent actionEvent) {
        double opacity = (int) sliderOpacity.getValue();
        opacity /= 100;
        if (useCache) {
            bgChoosing = "newbg.jpg";
        }
        System.out.println(opacity);
        reader.set_Link("assets/bg/" + bgChoosing, opacity);
        saveButton.setDisable(true);
        bgChoosed = bgChoosing;
        voiceChoosing = buttonVoice.getText();
        TranslateController.VOICESAVE.set_Voice(buttonVoice.getText(),
                muteVoice.isSelected(),
                autoCorrect.isSelected());
        restartLog.setVisible(true);
    }


    void addItemVoice(ListView<MyItem> listView) {
        MyItem a = new MyItem(Voices.English_UnitedStates.John);
        listView.getItems().add(a);
        a = new MyItem(Voices.English_UnitedStates.Amy);
        listView.getItems().add(a);
        a = new MyItem(Voices.English_UnitedStates.Linda);
        listView.getItems().add(a);
        a = new MyItem(Voices.English_UnitedStates.Mary);
        listView.getItems().add(a);
        a = new MyItem(Voices.English_UnitedStates.Mike);
        listView.getItems().add(a);
        a = new MyItem(Voices.English_GreatBritain.Alice);
        listView.getItems().add(a);
        a = new MyItem(Voices.English_GreatBritain.Nancy);
        listView.getItems().add(a);
        a = new MyItem(Voices.English_GreatBritain.Lily);
        listView.getItems().add(a);
    }

    public void muteVoiceClick(ActionEvent actionEvent) {
        TranslateController.TTSBTCPY.setDisable(muteVoice.isSelected());
        saveButton.setDisable(false);
    }

    public void autoCorrectClick(ActionEvent actionEvent) {
        TranslateController.setAUTOCORRECT(autoCorrect.isSelected());
        saveButton.setDisable(false);
    }

    public static boolean getMuteVoice() {
        return MUTECPY.isSelected();
    }

    public void addBgClick(ActionEvent actionEvent) {
        String path = "src/main/Resource/assets/bg/";
        System.out.println(path);
        File bg = addBackGroundFile.showOpenDialog(Main.WINDOW);
        if (bg != null) {
            try {
                File bgto = new File(Paths.get(path + "newbg.jpg").toUri());
                bgto.delete();
                Files.copy(bg.toPath(), bgto.toPath());
                cacheBG = bg.toURI().toString();
                double value = (int) sliderOpacity.getValue();
                value /= 100;
                Controller.setCacheBackground(bg.toURI().toString(), value);
                buttonBg.setText("bg_cache");
                useCache = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


// class tạo ra một cái button trong ô gợi ý
class MyItem extends VBox {
    private Label label;
    private double height;

    public MyItem(String text) {
        height = 50;
        label = new Label(text);
        label.getStyleClass().add("combo_bg_label");   //đặt tên classStyle cho element
        this.getChildren().add(label);
        label.setPrefHeight(height);
        this.setPrefHeight(height);
        this.getStyleClass().add("combo_bg_item");
    }

    public MyItem(String text, int h) {
        height = h;
        label = new Label(text);
        label.getStyleClass().add("combo_bg_label");   //đặt tên classStyle cho element
        this.getChildren().add(label);
        label.setPrefHeight(height);
        this.setPrefHeight(height);
        this.getStyleClass().add("combo_bg_item");
    }

    String getText() {
        return label.getText();
    }
}