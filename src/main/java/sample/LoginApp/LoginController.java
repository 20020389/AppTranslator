package sample.LoginApp;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import sample.App.Database;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    @FXML
    private TextField userField;
    @FXML
    private TextField passField;
    @FXML
    private TextField dataBoxField;       //Textbox của user, password và name database
    @FXML
    private PasswordField passBox;              //Textbox của password
    @FXML
    private CheckBox rememberBox;
    @FXML
    private Button showPass;
    @FXML
    private Button loginButton;      //button hiện pass , button đăng nhập
    @FXML
    private Label loginFailedLog;            //hiện thông báo đăng nhập csdl thất bại
    @FXML
    private Label userLabel;
    @FXML
    private Label passLabel;
    @FXML
    private Label dataLabel;    //label trong textbox

    public static JsonAccReader reader;               //đọc thông tin acc
    boolean passwordShowing;                   //hiện pass hay không

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        passwordShowing = false;
        reader = new JsonAccReader();
        rememberBox.setSelected(reader.getIsremember());
        loginButton.requestFocus();
        if(rememberBox.isSelected()) {      // nếu chọn nhớ pass ở lần login trước thì loaddata
            userField.setText(reader.getUser());          // load tên đăng nhập
            passField.setText(reader.getPassword());
            passBox.setText(reader.getPassword());
            dataBoxField.setText(reader.getCsdl());
            animation(passLabel,true);       //đưa label in textbox về đúng vị trí
            passLabel.styleProperty().set("-fx-background-color: #2e3440; -fx-text-fill: rgba(255,255,255,0.7);");
            passLabel.setDisable(false);
            if (!reader.getCsdl().isEmpty()) {
                animation(dataLabel,true);
                dataLabel.styleProperty().set("-fx-background-color: #2e3440; -fx-text-fill: rgba(255,255,255,0.7);");
                dataLabel.setDisable(false);
            }
        }
        //khi text trong textbox thay đổi ẩn thông báo đăng nhập thất bại
        userField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginFailedLog.setVisible(false);
        });
        dataBoxField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginFailedLog.setVisible(false);
        });
        //sự kiện khi focus textbox
        addFocusEvent(userField, userLabel);
        addFocusEvent(passField, passLabel);
        addFocusEvent(passBox, passLabel);
        addFocusEvent(dataBoxField, dataLabel);
        //sự kiện kết nối passbox và pass_show với nhau
        addListenerTextbox(passBox, passField);
        addListenerTextbox(passField, passBox);
        Main.EVENT_CLOSE = new EventHandler<WindowEvent>() {
            public void handle(WindowEvent w) {
                if (Main.status == Main.Status.LOGIN) {
                    if(rememberBox.isSelected()) {
                        reader.saveData(userField.getText(), passField.getText(), dataBoxField.getText(),true);
                    }
                    else {
                        reader.saveData(null,null,null,false);
                    }
                }
            }
        };

        addTextBoxKey(userField);
        addTextBoxKey(passField);
        addTextBoxKey(passBox);
        addTextBoxKey(dataBoxField);
    }

    void addListenerTextbox(TextInputControl text1, TextInputControl text2) {
        text1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (text1.isVisible()) {
                text2.setText(text1.getText());
                loginFailedLog.setVisible(false);
            }
        });
    }


    public void loginClick() {
        Database.login(userField.getText(), passField.getText(), dataBoxField.getText(),null);
        if (Database.Logined) {
            try {
                if(rememberBox.isSelected()) {
                    reader.saveData(userField.getText(), passField.getText(), dataBoxField.getText(),true);
                }
                else {
                    reader.saveData(null,null,null,false);
                }
                Main.changeWindow("App/mainscene.fxml",950,600);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            loginFailedLog.setVisible(true);
        }
    }

    /** hàm xảy ra khi nhấn showpass.
     * @param actionEvent
     */
    public void showPassClick(ActionEvent actionEvent) {
        if (!passwordShowing)  {
            showPass.styleProperty().set("-fx-background-image: url(\"/assets/eye_show.png\");" +
                    "-fx-background-color: transparent;");
            passField.setVisible(true);
            passBox.setVisible(false);
            passwordShowing = true;
        }
        else {
            showPass.styleProperty().set("-fx-background-image: url(\"/assets/eye_hide.png\");" +
                    "-fx-background-color: transparent;");
            passField.setVisible(false);
            passBox.setVisible(true);
            passwordShowing = false;
        }
    }

    /**hàm thêm sk focus cho textbox
     * @param textField
     * @param label
     */
    void addFocusEvent(TextInputControl textField, Label label) {
        textField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if(newPropertyValue) {
                    animation(label,true);
                    label.styleProperty().set("-fx-background-color: #2e3440; -fx-text-fill: #88bebc;");
                    label.setDisable(false);
                }
                else {
                    if (textField.getText().length() == 0) {
                        animation(label,false);
                        label.styleProperty().set("-fx-background-color: transparent;");
                        label.setDisable(true);
                    }
                    else {
                        label.styleProperty().set("-fx-background-color: #2e3440; -fx-text-fill: rgba(255,255,255,0.7);");
                    }
                }
            }
        });
    }


    /**hàm animation khi nhấn textbox
     * @param label
     * @param up
     */
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

    void addTextBoxKey(TextField textField) {
        textField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                loginClick();
            }
        });
    }

}
