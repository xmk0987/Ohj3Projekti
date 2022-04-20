package fi.tuni.prog3.sisu;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.*;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Objects;

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

        // Väärä salasana/nimi Dialog -ikkuna
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Couldn't log in!");
        alert.setContentText("Wrong email or password!");

        // Oikea salasana/nimi Dialog -ikkuna
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Log in Succesfull");
        okbutton.setOnAction(e -> {confirmation.showAndWait();});

        // Loading dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("LOADING DIALOG");
        dialog.setHeaderText("LOADING INFORMATION FROM SISU");

        ProgressBar progressBar = new ProgressBar();
        VBox vBox = new VBox(progressBar);
        GridPane grid = new GridPane();
        grid.add(vBox, 1, 0);
        grid.setHgap(10);
        grid.setVgap(10);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        // Tarkistaa onko salasana tyhjä vai ei. Avaa dialog-ikkunan sen mukaan.
        okbutton.setOnAction(e -> {
            String name_value = namef.getText();
            String password_value = passf.getText();
            if(Objects.equals(name_value.trim(), "") ||Objects.equals(password_value.trim(), "")){
                alert.show();
            }
            else{
                dialog.show();
                PauseTransition delay = new PauseTransition(Duration.seconds(5));
                delay.setOnFinished( event -> dialog.close() );
                delay.play();
            }
            namef.clear();
            passf.clear();
        });




    }

    public static void main(String[] args) {
        launch();
    }

}