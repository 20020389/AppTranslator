package sample.App.home;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import sample.App.Controller;
import sample.App.Database;
import sample.App.TTS;
import sample.App.setting.SettingController;

import java.net.URL;
import java.util.*;

public class TranslateController implements Initializable {
    @FXML
    private ListView<MyItem> listItems;    //ô gợi ý
    @FXML
    private TextField textbox1;            //ô nhập từ
    @FXML
    private Label textbox1label;
    @FXML
    private Button clearBt;               //xóa từ trong textbox
    @FXML
    private Label transLabel;
    @FXML
    private Label speakLabel;
    @FXML
    private Label typeLabel;        //biến label của phần dịch
    @FXML
    private VBox translateBase;           //Ô chứa thông tin dịch
    @FXML
    private Pane textboxChoosing;         //con trỏ gợi ý
    @FXML
    private Button textSpeech;
    @FXML
    private Button copyClipBt;

    public static VoiceSave VOICESAVE;
    public static TTS VOICERSS;                      //text to speech
    public static Button TTSBTCPY;
    public static VBox TRANSLATEBASECPY;
    public static boolean AUTOCORRECT;
    public static Database DATABASE;     //csdl
    private String[] items;
    private String[] trans;
    private String[] speak;
    private String[] type;
    private int textChoose = 0;            //vị trí con trỏ gợi ý
    private LinkedList<Integer> locaChar;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DATABASE = new Database();
        items = DATABASE.getArrData(0);//}
        trans = DATABASE.getArrData(3);//}
        type = DATABASE.getArrData(1); //}   => Lấy dữ liệu từ database
        speak = DATABASE.getArrData(2);//}
        /*URL link = this.getClass().getResource("main.html");
        web.getEngine().load(link.toString());*/
        VOICESAVE = new VoiceSave();
        Pane vBox = new Pane();
        addTextChange(textbox1, listItems, clearBt);
        VOICERSS = new TTS("src/main/Resource/assets/voice");
        VOICERSS.setVoice(VOICESAVE.getVoice());
        if (VOICESAVE.isUsevoice()) {
            textSpeech.setDisable(true);
        }
        clearBt.setFocusTraversable(false);
        listItems.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                translatetoVN(listItems.getSelectionModel().getSelectedItem().getText(), listItems, textboxChoosing);
                textbox1.requestFocus();
                textbox1.positionCaret(textbox1.getText().length());
                try {
                    if (!SettingController.getMuteVoice()) {
                        VOICERSS.start(textbox1.getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        addTextBoxKey(textbox1, textboxChoosing);
        addFocusEvent(textbox1, textbox1label);
        TTSBTCPY = textSpeech;
        VOICERSS.setCpy(textSpeech);
        TRANSLATEBASECPY = translateBase;
        textSpeech.setDisable(true);
        locaChar = DATABASE.getLocalChar();
        AUTOCORRECT = VOICESAVE.isAutoCorrect();
        textboxChoosing.setVisible(false);
        textboxChoosing.layoutYProperty().addListener((observableValue, number, t1) -> {
            textboxChoosing.setVisible(textboxChoosing.getLayoutY() != 0);
        });
        Controller.TASK.add(this::refreshDatabase);
    }

    private boolean check(String s1, String s2) {
        return s2.contains(s1);
    }

    int getIndex(String[] a, String need) {
        for (int i = 0; i < a.length; i++) {
            if (need.equals(a[i])) {
                return i;
            }
        }
        return -1;
    }

    private int getCharStart(int pos) {
        if (pos < 0 || pos > 97)
            return -1;
        return locaChar.get(pos);
    }

    private int getCharStop(int pos) {
        if (getCharStart(pos) == -1) {
            return -1;
        }
        for (int i = pos + 1; i < locaChar.size(); i++) {
            if (locaChar.get(i) != -1) {
                pos = locaChar.get(i);
                break;
            }
        }
        return pos;
    }

    //
    ////hàm xử lý event khi gõ chữ
    //
    void addTextChange(TextField textField, ListView listView, Button clear_button) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                MyItem vItem;
                listView.getItems().clear();
                int limit = 0;
                if (!textField.getText().isEmpty()) {
                    String text = textField.getText().replaceAll(" ", "").toLowerCase();
                    if (!text.isEmpty()) {
                        int pos = text.charAt(0) - 97;
                        /*System.out.println(getCharStart(pos) + " " + getCharStop(pos));*/
                        for (int i = getCharStart(pos); i < getCharStop(pos); i++) {
                            if (check(text, items[i])) {
                                vItem = new MyItem(items[i]);
                                vItem.setcustomPadding(20);
                                listView.getItems().add(vItem);
                                limit++;
                            }
                            if (limit == 5) {
                                break;
                            }
                        }
                    }
                }

                double list_width = 57.7 * limit;
                if (list_width > 284) {
                    list_width = 284;
                }

                listView.setPrefHeight(list_width);
                if (textField.getText().length() != 0) {
                    listView.setVisible(true);
                } else {
                    listView.setVisible(false);
                }

                textChoose = 0;
                textboxChoosing.setLayoutY(0);
                textSpeech.setDisable(true);
                if (limit == 0) {
                    listView.setVisible(false);
                }

                if (textField.getText().length() == 0) {
                    clear_button.setVisible(false);
                } else {
                    clear_button.setVisible(true);
                }
            }
        });
    }

    void addFocusEvent(TextInputControl textField, Label label) {
        textField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if(newPropertyValue) {
                animation(label,true);
                label.styleProperty().set("-fx-background-color: #323846; -fx-text-fill: #88bebc;");
                label.setDisable(false);
            }
            else {
                if (textField.getText().length() == 0) {
                    animation(label,false);
                    label.styleProperty().set("-fx-background-color: transparent;");
                    label.setDisable(true);
                }
                else {
                    label.styleProperty().set("-fx-background-color: #323846; -fx-text-fill: rgba(255,255,255,0.7);");
                }
            }
        });
    }

    String findSameWord(String text) {
        if (!text.isEmpty() && AUTOCORRECT) {
            int pos = text.toLowerCase().charAt(0) - 97;
            for (int i = 0; i < text.length(); i++) {
                String num = text.substring(0, text.length() - i);
                /*System.out.println(getCharStart(pos) + " => " + getCharStop(pos));*/
                for (int j = getCharStart(pos); j < getCharStop(pos); j++) {
                    if (items[j].contains(num)) {
                        text = items[j];
                        /*System.out.println(num + " -> " + text);*/
                        return text;
                    }
                }
            }
        }
        return null;
    }

    //
    ////hàm xử lý nhấn lên xuống để chọn gợi ý
    //
    void addTextBoxKey(TextField textField, Pane TB_Choosing) {
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    if (textChoose == 0) {
                        String text = textbox1.getText();
                        if (getIndex(items, text) == -1) {
                            text = findSameWord(text);
                        }
                        translatetoVN(text, listItems, textboxChoosing);
                        textbox1.positionCaret(textbox1.getText().length());
                    } else {
                        String text = listItems.getItems().get(textChoose - 1).getText();
                        translatetoVN(text, listItems, textboxChoosing);
                        textField.positionCaret(textbox1.getText().length());
                    }
                    if (!textField.getText().isEmpty()) {
                        try {
                            if (!SettingController.getMuteVoice()) {
                                VOICERSS.start(textbox1.getText());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (keyEvent.getCode() == KeyCode.DOWN) {
                    if (checkChoosing(listItems, textboxChoosing)) {
                        double loca = TB_Choosing.getLayoutY();
                        TB_Choosing.setLayoutY(loca + 56);
                        textChoose++;
                    }
                }
                if (keyEvent.getCode() == KeyCode.UP) {
                    if (TB_Choosing.getLayoutY() > 56) {
                        double loca = TB_Choosing.getLayoutY();
                        TB_Choosing.setLayoutY(loca - 56);
                        textChoose--;
                    }
                    textbox1.positionCaret(textbox1.getText().length());
                }
            }
        });
    }

    //
    //kiểm tra xem con trỏ có đang trỏ đến vị trí gợi ý hay k
    //
    private boolean checkChoosing(ListView list, Pane choose) {
        double list_h = list.getPrefHeight();
        double choose_y = choose.getLayoutY();
        if (choose_y + 56 < list_h) {
            return true;
        }
        return false;
    }

    //hàm xử lý sự kiện khi dịch (ẩn ô gợi ý, đưa con trỏ gợi ý về 0,...)
    void translatetoVN(String text, ListView listView, Pane choosing) {
        if (text != null) {
            int index = getIndex(items, text);
            String word_type = type[index];
            if (word_type == null || word_type.equals("null") || word_type.isEmpty()) {
                typeLabel.setText("Type: chưa có");
            } else {
                if (word_type.equals("n")) {
                    word_type = "danh từ";
                } else {
                    word_type = word_type.replace("pron", "đại từ")
                            .replace("n,", "danh từ,")
                            .replace(" n", " danh từ")
                            .replace("adj", "tính từ")
                            .replace("adv", "trạng từ")
                            .replace("v", "động từ")
                            .replace("conj", "liên từ")
                            .replace("prep", "giới từ")
                            .replace("det", "từ hạn định")
                            .replace("exc", "từ cảm thán")
                            .replace("number", "số");
                }
                typeLabel.setText("Type: " + word_type);
            }
            textbox1.setText(text);
            if (trans[index].equals("null") || trans[index].isEmpty()) {
                transLabel.setText("Bạn chưa thêm nghĩa cho từ này");
            } else {
                transLabel.setText("Dịch: -" + trans[index]
                        .replace("; ",";")
                        .replace(";","\n      -"));
            }

            if (speak[index].equals("null") || speak[index].isEmpty()) {
                speakLabel.setText("Cách đọc: chưa có");
            } else {
                speakLabel.setText("Cách đọc: " + speak[index]);
            }
            copyClipBt.setVisible(true);
        } else {
            transLabel.setText("Xin lỗi tôi không hiểu từ này");
            speakLabel.setText("");
            typeLabel.setText("");
            copyClipBt.setVisible(false);
        }
        choosing.setLayoutY(0);
        textChoose = 0;
        listView.setVisible(false);
    }


    //hàm xử lý khi nhấn nút x trong textbox
    public void clearBtClick(ActionEvent actionEvent) {
        textbox1.setText("");
        textbox1.requestFocus();                                //hàm này giúp focus lại textbox
        textbox1.positionCaret(textbox1.getText().length());    //hàm này giúp đưa con trỏ về cuối textbox
        textboxChoosing.setLayoutY(0);
        textChoose = 0;
        textSpeech.setDisable(true);
    }

    //hàm xử lý khi nói
    public void speakClick(ActionEvent event) {
        VOICERSS.play();
    }

    public static void eventResize(StackPane pane) {
        if (pane.getWidth() < 900) {
            moveBaseTrans(TRANSLATEBASECPY, true);
            TRANSLATEBASECPY.setPrefWidth(pane.getWidth() - 50);
            if (pane.getHeight() < 600) {
                TRANSLATEBASECPY.setPrefHeight(pane.getHeight() - pane.getHeight() / 3.5);
            } else {
                TRANSLATEBASECPY.setPrefHeight(409);
            }
        } else {
            moveBaseTrans(TRANSLATEBASECPY, false);
            TRANSLATEBASECPY.setPrefWidth(402);
        }
    }

    private static void moveBaseTrans(VBox pane, boolean down) {
        if (down) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), pane);
            transition.setToY(79);
            transition.setToX(-430);
            transition.play();
        } else {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), pane);
            transition.setToY(0);
            transition.setToX(0);
            transition.play();
        }
    }

    public static void setAUTOCORRECT(boolean save) {
        AUTOCORRECT = save;
    }

    void animation(Label label, boolean up) {
        if (up) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(150),label);
            transition.setToY(-27);
            transition.setToX(-10);
            transition.play();
            ScaleTransition scale = new ScaleTransition(Duration.millis(150),label);
            scale.setToY(0.8);
            scale.setToX(0.8);
            scale.play();
        } else {
            TranslateTransition transition = new TranslateTransition(Duration.millis(150),label);
            transition.setToY(0);
            transition.setToX(0);
            transition.play();
            ScaleTransition scale = new ScaleTransition(Duration.millis(150),label);
            scale.setToY(1);
            scale.setToX(1);
            scale.play();
        }
    }

    public void copyClick(ActionEvent actionEvent) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(transLabel.getText().replace("Dịch: -","")
                .replace("\n      -", "; "));
        clipboard.setContent(content);
    }

    private void refreshDatabase() {
        items = DATABASE.getArrData(0);//}
        trans = DATABASE.getArrData(3);//}
        type = DATABASE.getArrData(1); //}   => Lấy dữ liệu từ database
        speak = DATABASE.getArrData(2);//}
        locaChar = DATABASE.getLocalChar();
    }
}

// class tạo ra một cái button trong ô gợi ý
class MyItem extends VBox {
    private Label label;
    private double height;

    public MyItem(String text) {
        height = 50;
        label = new Label(text);
        label.getStyleClass().add("label_view");   //đặt tên classStyle cho element
        this.getChildren().add(label);
        label.setPrefHeight(height);
        this.setPrefHeight(height);
        this.getStyleClass().add("vbox_item");
    }

    public String getText() {
        return label.getText();
    }

    public void setcustomPadding(int padding) {
        String style = "0 0 0 " + padding;
        this.setStyle("-fx-padding: " + style + ";");
    }

    public void setHeight(int h) {
        height = h;
    }
}
