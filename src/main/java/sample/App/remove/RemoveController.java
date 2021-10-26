package sample.App.remove;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import sample.App.Controller;
import sample.App.Database;
import sample.App.console.ConsoleApp;
import sample.App.home.TranslateController;
import sample.control.MessageBox;

import java.net.URL;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;

public class RemoveController implements Initializable {
    public Pane baseTextbox;
    public Pane removePane;
    public ListView<MyItem> listItems;
    public Pane textboxChoosing;
    public TextField textbox1;
    public Label textbox1label;
    public Button clearBt;
    public Pane workPane;
    public Button trashBt;


    ConsoleApp consoleLog;
    private Database database;
    private String[] word;
    private LinkedList<Integer> locaChar;
    private int textChoose;
    private int indexWord;
    private boolean hasWord;

    private static ConsoleApp CONSOLECPY;
    private static Pane BASETBCPY;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        database = TranslateController.DATABASE;
        word = database.getArrData(Database.WORD);
        locaChar = database.getLocalChar();
        textChoose = 0;
        addFocusEvent(textbox1, textbox1label);
        addTextChange(textbox1, listItems, clearBt);
        addTextBoxKey(textbox1, textboxChoosing);
        listItems.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //code in here
                String text = listItems.getSelectionModel().getSelectedItem().getText();
                textbox1.setText(text);
                indexWord = getIndex(word, text);
                if (indexWord == -1) {
                    trashBt.setStyle("-fx-background-image: url(\"/assets/trash-error.png\")");
                    hasWord = false;
                } else {
                    trashBt.setStyle("-fx-background-image: url(\"/assets/trash-work.png\")");
                    hasWord = true;
                }
                //
                textboxChoosing.setLayoutY(0);
                textChoose = 0;
                listItems.setVisible(false);
                textbox1.requestFocus();
                textbox1.positionCaret(textbox1.getText().length());
            }
        });
        consoleLog = new ConsoleApp();
        consoleLog.setPrefWidth(357);
        consoleLog.setPrefHeight(237);
        consoleLog.toBack();
        consoleLog.setTranslateX(400);
        consoleLog.setTranslateY(-105);
        workPane.getChildren().add(consoleLog);
        CONSOLECPY = consoleLog;
        BASETBCPY = baseTextbox;
        hasWord = false;
        Controller.TASK.add(() -> {
           refreshDatabase();
        });
    }

    private boolean check(String s1, String s2) {
        if (s2.indexOf(s1) != -1)
            return true;
        return false;
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
                trashBt.setStyle("-fx-background-image: url(\"/assets/trash.png\")");
                hasWord = false;
                if (!textField.getText().isEmpty()) {
                    String text = textField.getText().replaceAll(" ", "").toLowerCase();
                    if (!text.isEmpty()) {
                        int pos = text.charAt(0) - 97;
                        /*System.out.println(getCharStart(pos) + " " + getCharStop(pos));*/
                        for (int i = getCharStart(pos); i < getCharStop(pos); i++) {
                            if (check(text, word[i])) {
                                vItem = new MyItem(word[i]);
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

                if (textField.getText().length() == 0) {
                    clear_button.setVisible(false);
                } else {
                    clear_button.setVisible(true);
                }
            }
        });
    }

    void addFocusEvent(TextInputControl textField, Label label) {
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    animation(label, true);
                    label.styleProperty().set("-fx-background-color: #323846; -fx-text-fill: #88bebc;");
                    label.setDisable(false);
                } else {
                    if (textField.getText().length() == 0) {
                        animation(label, false);
                        label.styleProperty().set("-fx-background-color: transparent;");
                        label.setDisable(true);
                    } else {
                        label.styleProperty().set("-fx-background-color: #323846; -fx-text-fill: rgba(255,255,255,0.7);");
                    }
                }
            }
        });
    }

    void addTextBoxKey(TextField textField, Pane tbChoosing) {
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    if (textChoose == 0) {
                        String text = textbox1.getText();
                        // code in here
                        indexWord = getIndex(word, text);
                        if (indexWord == -1) {
                            trashBt.setStyle("-fx-background-image: url(\"/assets/trash-error.png\")");
                            hasWord = false;
                        } else {
                            trashBt.setStyle("-fx-background-image: url(\"/assets/trash-work.png\")");
                            hasWord = true;
                        }
                        tbChoosing.setLayoutY(0);
                        textChoose = 0;
                        listItems.setVisible(false);
                        //
                        textbox1.positionCaret(textbox1.getText().length());
                    } else {
                        String text = listItems.getItems().get(textChoose - 1).getText();
                        // code in here
                        indexWord = getIndex(word, text);
                        textbox1.setText(text);
                        if (indexWord == -1) {
                            trashBt.setStyle("-fx-background-image: url(\"/assets/trash-error.png\")");
                            hasWord = false;
                        } else {
                            trashBt.setStyle("-fx-background-image: url(\"/assets/trash-work.png\")");
                            hasWord = true;
                        }
                        tbChoosing.setLayoutY(0);
                        textChoose = 0;
                        listItems.setVisible(false);
                        //
                        textField.positionCaret(textbox1.getText().length());
                    }
                }

                if (keyEvent.getCode() == KeyCode.DOWN) {
                    if (checkChoosing(listItems, textboxChoosing)) {
                        double loca = tbChoosing.getLayoutY();
                        tbChoosing.setLayoutY(loca + 56);
                        textChoose++;
                    }
                }
                if (keyEvent.getCode() == KeyCode.UP) {
                    if (tbChoosing.getLayoutY() > 56) {
                        double loca = tbChoosing.getLayoutY();
                        tbChoosing.setLayoutY(loca - 56);
                        textChoose--;
                    }
                    textbox1.positionCaret(textbox1.getText().length());
                }
            }
        });
    }

    private boolean checkChoosing(ListView list, Pane choose) {
        double list_h = list.getPrefHeight();
        double choose_y = choose.getLayoutY();
        if (choose_y + 56 < list_h) {
            return true;
        }
        return false;
    }

    void animation(Label label, boolean up) {
        if (up) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(150), label);
            transition.setToY(-27);
            transition.setToX(-10);
            transition.play();
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), label);
            scale.setToY(0.8);
            scale.setToX(0.8);
            scale.play();
        } else {
            TranslateTransition transition = new TranslateTransition(Duration.millis(150), label);
            transition.setToY(0);
            transition.setToX(0);
            transition.play();
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), label);
            scale.setToY(1);
            scale.setToX(1);
            scale.play();
        }
    }

    public void clearBtClick(ActionEvent actionEvent) {
        textbox1.clear();
    }

    public static void moveButton(Pane pane) {
        if (pane.getWidth() < 900) {
            moveConsole(CONSOLECPY, true);
            moveTbAnimation(BASETBCPY, true, pane.getWidth() / 2);
        } else {
            moveTbAnimation(BASETBCPY, false, pane.getWidth() / 2);
            moveConsole(CONSOLECPY, false);
        }
    }

    private static void moveTbAnimation(Pane pane, boolean move, double w) {
        if (move) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), pane);
            transition.setToX(w - 236);
            transition.play();
        } else {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), pane);
            transition.setToX(0);
            transition.play();
        }
    }


    private static void moveConsole(ConsoleApp consoleApp, boolean hide) {
        if (hide) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), consoleApp);
            transition.setToX(0);
            transition.setToY(0);
            transition.play();
        } else {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), consoleApp);
            transition.setToX(400);
            transition.setToY(-105);
            transition.play();
        }
    }

    public void removeWordClick(ActionEvent actionEvent) {
        if (hasWord) {
            MessageBox messageBox = new MessageBox();
            Optional<ButtonType> status = messageBox.show("Are you sure you want remove this word?");
            if (status.isPresent() && status.get().equals(ButtonType.YES)) {
                database.remove(textbox1.getText());
                database.update();
                refreshDatabase();
                consoleLog.addWork("successfully to remove word!");
            }
        } else {
            if (textbox1.getText().isEmpty() || listItems.isVisible()) {
                consoleLog.addWarning("you must set your word");
            } else {
                consoleLog.addError("this word does not exist");
            }
        }
    }

    private void refreshDatabase() {
        word = database.getArrData(Database.WORD);
        locaChar = database.getLocalChar();
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
