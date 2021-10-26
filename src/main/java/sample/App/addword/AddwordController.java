package sample.App.addword;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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

public class AddwordController implements Initializable {
    @FXML
    private TextField wordField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField speakField;
    @FXML
    private TextField transField;
    @FXML
    private TextField sameField;
    @FXML
    private Label wordLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label speekLabel;
    @FXML
    private Label transLabel;
    @FXML
    private Label sameLabel;
    @FXML
    private Button submitBt;
    @FXML
    private Button clearBt;
    @FXML
    private Pane baseTextbox;
    @FXML
    private Pane addvancedPane;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private CheckBox advancedCheck;
    @FXML
    private Button notifyBt;
    @FXML
    private CheckBox muteBox1;
    @FXML
    private CheckBox muteBox2;
    @FXML
    private CheckBox muteBox3;

    Tooltip notifyTooltip;
    String notifyStyle = """
            -fx-background-color: transparent;
            -fx-background-size: 26;
            -fx-background-position: center;
            -fx-background-repeat: no-repeat;""";
    Thread notifyCheck;

    Database database;
    String[] dataWord;
    String[] dataType;
    String[] dataSpeak;
    String[] dataTrans;
    private int logState;
    private ConsoleApp consoleLog;
    private boolean isHad;
    private int hadLocation;
    private LinkedList<Integer> locaChar;

    private static Button SUBMITBTCPY;
    private static Button CLEARBTCPY;
    private static Pane BASETEXTBOXCPY;
    private static ConsoleApp CONSOLELOGCPY;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addFocusText(wordField, wordLabel);
        addFocusText(typeField, typeLabel);
        addFocusText(speakField, speekLabel);
        addFocusText(transField, transLabel);
        addFocusText(sameField, sameLabel);

        addTextChange(wordField);
        addTextChange(typeField);
        addTextChange(speakField);
        addTextChange(transField);
        addTextChange(sameField);
        database = TranslateController.DATABASE;
        dataWord = database.getArrData(Database.WORD);
        dataType = database.getArrData(Database.TYPE);
        dataSpeak = database.getArrData(Database.SPEAK);
        dataTrans = database.getArrData(Database.TRANS);
        notifyTooltip = new Tooltip("");
        notifyBt.setTooltip(notifyTooltip);
        notifyBt.setFocusTraversable(false);
        notifyCheck = new Thread(this::notifyChecking);
        consoleLog = new ConsoleApp();
        consoleLog.setPrefWidth(357);
        consoleLog.setPrefHeight(238);
        consoleLog.setLayoutX(483);
        consoleLog.setLayoutY(43);
        mainPane.getChildren().add(consoleLog);
        logState = 0;
        isHad = false;
        addCpy();
        locaChar = database.getLocalChar();
        Controller.TASK.add(this::updateDatabase);
    }

    private void setmuteBox(boolean visiable) {
        muteBox1.setVisible(visiable);
        muteBox2.setVisible(visiable);
        muteBox3.setVisible(visiable);
    }

    private void notifyChecking() {
        if (!wordField.getText().isEmpty()) {
            int pos = wordField.getText().toLowerCase().charAt(0) - 97;
            notifyBt.styleProperty().set("-fx-background-image: url(\"/assets/work.png\");" + notifyStyle);
            notifyTooltip.setText("this word can be add");
            isHad = false;
            setmuteBox(false);
            for (int i = getCharStart(pos); i < getCharStop(pos); i++) {
                if (wordField.getText().equals(dataWord[i])) {
                    notifyBt.styleProperty().set("-fx-background-image: url(\"/assets/warning.png\");" + notifyStyle);
                    notifyTooltip.setText("this word already exists!\nyou just can modify this word");
                    isHad = true;
                    hadLocation = i;
                    setmuteBox(true);
                    break;
                } else {
                    notifyBt.styleProperty().set("-fx-background-image: url(\"/assets/work.png\");" + notifyStyle);
                    notifyTooltip.setText("this word can be add");
                    isHad = false;
                    setmuteBox(false);
                }
                notifyBt.setVisible(true);
            }
        }
    }

    private void updateDatabase() {
        database.update();
        submitBt.setDisable(true);
        locaChar = database.getLocalChar();
        dataWord = database.getArrData(Database.WORD);
        dataType = database.getArrData(Database.TYPE);
        dataSpeak = database.getArrData(Database.SPEAK);
        dataTrans = database.getArrData(Database.TRANS);
        submitBt.setDisable(false);
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

    public void checkAdvancedClick() {
        showAdvanced(addvancedPane, advancedCheck.isSelected());
    }

    void addCpy() {
        SUBMITBTCPY = submitBt;
        CLEARBTCPY = clearBt;
        BASETEXTBOXCPY = baseTextbox;
        CONSOLELOGCPY = consoleLog;
        submitBt.translateXProperty().set(426);
        submitBt.translateYProperty().set(-63);
        clearBt.translateXProperty().set(426);
        clearBt.translateYProperty().set(-63);
    }

    void addFocusText(TextField textField, Label label) {
        textField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
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
        });
    }

    void addTextChange(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            clearBt.setDisable(wordField.getText().isEmpty()
                    && typeField.getText().isEmpty()
                    && speakField.getText().isEmpty()
                    && transField.getText().isEmpty()
                    && sameField.getText().isEmpty());
            if (!wordField.getText().isEmpty()) {
                submitBt.setDisable(false);
            } else {
                submitBt.setDisable(true);
                notifyBt.setVisible(false);
            }
            if (textField.equals(wordField)) {
                if (notifyCheck.isAlive()) {
                    notifyCheck.stop();
                }
                notifyCheck = new Thread(this::notifyChecking);
                notifyCheck.start();
            }
        });
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

    private boolean check(String[] a, String b, int start, int endl) {
        for (int i = start; i < endl; i++) {
            if (a[i].equals(b)) {
                return false;
            }
        }
        return true;
    }


    public void submitClick(ActionEvent actionEvent) {
        if (isHad) {
            System.out.println(hadLocation);
            MessageBox messageBox = new MessageBox();
            Optional<ButtonType> action = messageBox.show("Are you sure you want modify this word?");
            if (action.isPresent() && action.get() == ButtonType.YES) {
                System.out.println("Từ đã tồn tại");
                Database.getDataLog().add(new String[]{ConsoleApp.WARNING, "Từ đã tồn tại"});
                String[] oldValue = {
                        dataWord[hadLocation],
                        dataType[hadLocation],
                        dataSpeak[hadLocation],
                        dataTrans[hadLocation]};
                String[] newValue = {
                        wordField.getText().toLowerCase(),
                        (muteBox1.isSelected()) ? dataType[hadLocation] : typeField.getText().toLowerCase(),
                        (muteBox2.isSelected()) ? dataSpeak[hadLocation] : speakField.getText().toLowerCase(),
                        (muteBox3.isSelected()) ? dataTrans[hadLocation] : transField.getText().toLowerCase()
                };
                database.changeData(newValue, oldValue);
                updateConsole();
                messageBox.close();
                updateDatabase();
            }
        } else {
            MessageBox messageBox = new MessageBox();
            Optional<ButtonType> action = messageBox.show("Are you sure you want save this word?");
            if (action.isPresent() && action.get() == ButtonType.YES) {
                String[] data;
                if (advancedCheck.isSelected()) {
                    data = new String[]{
                            wordField.getText().toLowerCase(),
                            typeField.getText(),
                            speakField.getText().replace("'", "''"),
                            transField.getText(),
                            sameField.getText()
                    };
                } else {
                    data = new String[]{
                            wordField.getText().toLowerCase(),
                            typeField.getText(),
                            speakField.getText().replace("'", "''"),
                            transField.getText()
                    };
                }
                database.add(data);
                updateConsole();
                messageBox.close();
                updateDatabase();
            }
        }
    }

    void updateConsole() {
        int i = 0;
        for (i = logState; i < Database.getDataLog().size(); i++) {
            if (Database.getDataLog().get(i)[0].equals("work")) {
                consoleLog.addWork(Database.getDataLog().get(i)[1]);
            } else if (Database.getDataLog().get(i)[0].equals("warning")) {
                consoleLog.addWarning(Database.getDataLog().get(i)[1]);
            } else {
                consoleLog.addError(Database.getDataLog().get(i)[1]);
            }
        }
        logState = i;
    }

    public void clearClick(ActionEvent actionEvent) {
        wordField.setText("");
        typeField.setText("");
        speakField.setText("");
        transField.setText("");
        sameField.setText("");
        animation(wordLabel,false);
        wordLabel.styleProperty().set("-fx-background-color: transparent;");
        wordLabel.setDisable(true);

        animation(typeLabel,false);
        typeLabel.styleProperty().set("-fx-background-color: transparent;");
        typeLabel.setDisable(true);

        animation(speekLabel,false);
        speekLabel.styleProperty().set("-fx-background-color: transparent;");
        speekLabel.setDisable(true);

        animation(transLabel,false);
        transLabel.styleProperty().set("-fx-background-color: transparent;");
        transLabel.setDisable(true);
    }

    public static void moveButton(Pane pane) {
        if (pane.getWidth() < 900) {
            moveConsole(CONSOLELOGCPY, true);
            moveTbAnimation(BASETEXTBOXCPY, true, pane.getWidth() / 2);
            moveBtAnimation(SUBMITBTCPY, true);
            moveBtAnimation(CLEARBTCPY, true);
        } else {
            moveTbAnimation(BASETEXTBOXCPY, false, pane.getWidth() / 2);
            moveBtAnimation(SUBMITBTCPY, false);
            moveBtAnimation(CLEARBTCPY, false);
            moveConsole(CONSOLELOGCPY, false);
        }
    }

    public static void moveBtAnimation(Button button, boolean up) {
        if (up) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), button);
            transition.setToX(0);
            transition.setToY(0);
            transition.play();
        } else {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), button);
            transition.setToX(426);
            transition.setToY(-63);
            transition.play();
        }
    }

    public static void moveTbAnimation(Pane pane, boolean move, double w) {
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

    private void showAdvanced(Pane pane, boolean show) {
        if (show) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), pane);
            transition.setToY(70);
            transition.play();
            FadeTransition transition1 = new FadeTransition(Duration.millis(500), pane);
            transition1.setToValue(0);
            transition1.play();
        } else {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), pane);
            transition.setToY(0);
            transition.play();
            FadeTransition transition1 = new FadeTransition(Duration.millis(500), pane);
            transition1.setToValue(1);
            transition1.play();
        }
    }

    private static void moveConsole(ConsoleApp consoleApp, boolean hide) {
        if (hide) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), consoleApp);
            transition.setToX(500);
            transition.play();
            FadeTransition transition1 = new FadeTransition(Duration.millis(200), consoleApp);
            transition1.setToValue(0);
            transition1.play();
        } else {
            TranslateTransition transition = new TranslateTransition(Duration.millis(500), consoleApp);
            transition.setToX(0);
            transition.play();
            FadeTransition transition1 = new FadeTransition(Duration.millis(200), consoleApp);
            transition1.setToValue(1);
            transition1.play();
        }
    }

}
