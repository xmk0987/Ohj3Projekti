package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/*
Authors:
Paavo Jyrkiäinen H291934
Onni Vitikainen H292259
Otto Ukkonen H291887
 */


public class Sisu extends Application {



    public HashMap<String, Map<String, ArrayList<String>>> readFromJsons() throws IOException {
        HashMap<String, Map<String, ArrayList<String>>> degree_grouping_study = new HashMap<>();

        /*
        File[] module_files = new File("$PROJECT_DIR$/json/modules").listFiles();
        File[] course_files = new File("$PROJECT_DIR$/json/courseunits").listFiles();
        List<String> all_module_files = new ArrayList<String>();
        List<String> all_courseunits_files = new ArrayList<String>();

        assert module_files != null;
        for (File file : module_files) {
            if (file.isFile()) {
                all_module_files.add(file.getName());
            }
        }

        assert course_files != null;
        for (File file : course_files) {
            if (file.isFile()) {
                all_courseunits_files.add(file.getName());
            }
        }
*/
        Gson gson = new Gson();
        // ../json/modules/otm-3990be25-c9fd-4dae-904c-547ac11e8302.json
        // C:/Users/onniv/git_test/Projekti-Ohj3/json/modules/
        //"C:/Users/onniv/git_test/Projekti-Ohj3/json/modules/otm-3990be25-c9fd-4dae-904c-547ac11e8302.json"

        File file = new File("./json/modules/otm-3990be25-c9fd-4dae-904c-547ac11e8302.json");
        var jsonfile = gson.fromJson(new FileReader(file), JsonObject.class);
        String module_type = jsonfile.get("type").getAsString();
        if(module_type.equals("DegreeProgramme")){
            degree_grouping_study.put(module_type, Collections.emptyMap());
        }

        /*
        for (var module_file : all_module_files){
            var jsonfile = gson.fromJson(new FileReader(module_file), JsonObject.class);
            String module_type = jsonfile.get("type").getAsJsonObject().getAsString();
            if(module_type.equals("DegreeProgramme")){
                degree_grouping_study.put(module_type, Collections.emptyMap());
            }


        }
        */

    return degree_grouping_study;

    }

    @Override
    public void start(Stage stage) throws Exception {


        // Alkumenu
        Label welcome=new Label("Tervetuloa Sus Sisuun");
        Label name =new Label("Nimi");
        Label student_number = new Label("Opiskelijanumero");
        welcome.setTextFill(Color.PURPLE);
        TextField namef=new TextField();
        TextField student_numberf=new TextField();
        namef.setPromptText("Syötä nimesi");
        student_numberf.setPromptText("Syötä opiskelijanumero");
        Button okbutton = new Button("OK");
        GridPane root = new GridPane();
        root.addRow(0, welcome);
        root.addRow(1, name, namef);
        root.addRow(2, student_number, student_numberf);
        root.addRow(2, okbutton);
        root.setAlignment(Pos.CENTER);

        // Alkuscenen aukaisu
        Scene scene=new Scene(root,400,130);
        stage.setScene(scene);
        stage.setTitle("Sus Sisu");
        stage.show();

        var degreeees = readFromJsons();
        Map.Entry<String, Map<String, ArrayList<String>>> entry = degreeees.entrySet().iterator().next();

        // Sisun pääikkuna
        GridPane maingrid = new GridPane();
        Scene mainscene=new Scene(maingrid,500,500);
        Label degree_type = new Label(entry.getKey());
        maingrid.addRow(0,degree_type);


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

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.getDialogPane().setContent(grid);


        // Tarkistaa onko salasana tyhjä vai ei. Avaa dialog-ikkunan sen mukaan.
        okbutton.setOnAction(e -> {
            String name_value = namef.getText();
            String student_number_value = student_numberf.getText();
            if(Objects.equals(name_value.trim(), "") ||Objects.equals(student_number_value.trim(), "")){
                alert.show();
            }
            else{
                dialog.show();
                PauseTransition delay = new PauseTransition(Duration.seconds(1));
                delay.setOnFinished( event -> dialog.close() );
                delay.play();

                // Pääikkuna aukeaa latauksen jälkeen
                PauseTransition delay2 = new PauseTransition(Duration.seconds(1));
                delay2.setOnFinished( event -> stage.setScene(mainscene) );
                delay2.play();

            }
            namef.clear();
            student_numberf.clear();
        });



    }

    public static void main(String[] args) {
        launch();
    }

}