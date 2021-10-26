package sample.App.console;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ConsoleApp extends ScrollPane {
    String workStyle = "-fx-text-fill: #88c0d0;";
    String warningStyle = "-fx-text-fill: #ebcb8b;";
    String errorStyle = "-fx-text-fill: #bf616a;";
    private String scrollStyle = "";
    VBox pane;

    public static String WORK = "work";
    public static String WARNING = "warning";
    public static String ERROR = "error";

    public ConsoleApp() {
        pane = new VBox();
        pane.setStyle("-fx-background-color: #323846;");
        this.setContent(pane);
        this.setStyle("-fx-background: #323846;-fx-border-color: #323846;");
        this.getStyleClass().add("myconsole");
        this.getStylesheets().add(getClass().getResource("ConsoleStyle.css").toExternalForm());
        this.viewportBoundsProperty().addListener(changeListener);
        this.setPadding(new Insets(10, 10, 10, 10));
    }

    public void addWork(String text) {
        Label label = new Label("- " + text);
        label.maxWidth(337);
        label.setWrapText(true);
        label.styleProperty().set(workStyle);
        pane.getChildren().add(label);
    }

    public void addWarning(String text) {
        Label label = new Label("- " + text);
        label.maxWidth(337);
        label.setWrapText(true);
        label.styleProperty().set(warningStyle);
        pane.getChildren().add(label);
    }

    public void addError(String text) {
        Label label = new Label("- " + text);
        label.maxWidth(337);
        label.setWrapText(true);
        label.styleProperty().set(errorStyle);
        pane.getChildren().add(label);
    }

    private ChangeListener<Object> changeListener = new ChangeListener<Object>() {
        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            updateView();
        }
    };

    private void updateView() {
        this.setVvalue(1);
    }

}
