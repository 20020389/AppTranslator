package sample.control;

import javafx.geometry.Insets;
import javafx.scene.control.*;

import java.util.Optional;

public class MessageBox extends Dialog<ButtonType>{
    private
    DialogPane dpane;
    private double w;
    private double h;
    Button yesBt, noBt;

    public MessageBox() {
        w = 350;
        h = 110;
        addPaneStyle();
        addOkButton();
        addCancelButton();
        this.setDialogPane(dpane);
        dpane.getStylesheets().add(getClass().getResource("MessageBoxStyle.css").toString());
        this.setWidth(w);
        this.setHeight(h);
        this.setTitle("Database Log");
    }

    private void addPaneStyle() {
        dpane = new DialogPane();
        dpane.setPrefWidth(w);
        dpane.getStyleClass().add("pane");
        dpane.setPadding(new Insets(5, 0, 0, 50));
        dpane.setContentText("Are you sure you want save this word?");
        dpane.getChildren().get(1).setStyle("-fx-text-fill: white");
    }


    private void addOkButton() {
        ButtonType buttonType = ButtonType.YES;
        dpane.getButtonTypes().add(buttonType);
        yesBt = (Button) dpane.lookupButton(dpane.getButtonTypes().get(0));
        yesBt.getStyleClass().add("okButton");
    }

    private void addCancelButton() {
        ButtonType buttonType = ButtonType.CANCEL;
        dpane.getButtonTypes().add(buttonType);
        noBt = (Button) dpane.lookupButton(dpane.getButtonTypes().get(1));
        noBt.getStyleClass().add("cancelButton");
    }

    public Optional<ButtonType> show(String text) {
        if (text.equals("Are you sure you want save this word?")) {
            dpane.setPrefWidth(360);
            dpane.setPrefHeight(110);
        } else {
            dpane.setPrefWidth(400);
            dpane.setPrefHeight(110);
        }
        this.setContentText(text);
        return this.showAndWait();
    }
}
