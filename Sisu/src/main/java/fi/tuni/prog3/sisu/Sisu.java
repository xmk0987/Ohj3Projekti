package fi.tuni.prog3.sisu;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.*;

/*
Authors:
Paavo Jyrkiäinen H291934
Onni Vitikainen H292259
Otto Ukkonen H291887
 */


public class Sisu extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Alkumenu
        Label welcome=new Label("Tervetuloa Sus Sisuun");
        Label id=new Label("Opiskelijatunnus tai sähköposti");
        Label password = new Label("Salasana");
        welcome.setTextFill(Color.PURPLE);
        TextField namef=new TextField();
        PasswordField passf=new PasswordField();
        namef.setPromptText("Syötä nimi");
        passf.setPromptText("Syötä salasana");
        Button okbutton = new Button("OK");
        GridPane root = new GridPane();
        root.addRow(0, welcome);
        root.addRow(1, id, namef);
        root.addRow(2, password, passf);
        root.addRow(2, okbutton);
        root.setAlignment(Pos.CENTER);

        Scene scene=new Scene(root,400,130);
        stage.setScene(scene);
        stage.setTitle("Sus Sisu");
        stage.show();

        // Dialog -ikkuna
        Dialog<String> dialog = new Dialog<String>();
        dialog.setTitle(":):):):):):)");
        TextInputDialog name = new TextInputDialog();

        okbutton.setOnAction(e -> {dialog.showAndWait();});
        ButtonType type = new ButtonType("Continue", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(type);
    }

    public static void main(String[] args) {
        launch();
    }

}