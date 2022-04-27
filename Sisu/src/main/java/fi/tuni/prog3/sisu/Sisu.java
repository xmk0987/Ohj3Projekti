package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
import java.util.*;

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


        // Sisun pääikkuna
        GridPane maingrid = new GridPane();
        Scene mainscene=new Scene(maingrid,500,500);



        Gson gson = new Gson();


        File moduleDir = new File("./json/modules");
        File[] dirList = moduleDir.listFiles();

        //Getting all child modules
        ArrayList<String> module_group_ids = new ArrayList<>();
        String id = "";



        /*
        for(File child : dirList) {
            var jsonfile = gson.fromJson(new FileReader(child), JsonObject.class);
            // Getting roots
            if(jsonfile.get("type").getAsString().equals("DegreeProgramme")){
                String module_type = jsonfile.get("name").getAsJsonObject()
                        .get("fi").getAsString();
                String min_credits  = jsonfile.get("targetCredits")
                        .getAsJsonObject().get("min").getAsString();
                Label degree_name_label = new Label(module_type);
                Label min_credit_label = new Label(min_credits);
                maingrid.addRow(0,degree_name_label);
                maingrid.addRow(0,min_credit_label);
                maingrid.setHgap(100);
                id = "otm-010acb27-0e5a-47d1-89dc-0f19a43a5dca";



            }
        }
        */


        ArrayList<moduleclass> all_modules = new ArrayList<>();
        for(File child : dirList)
        {
            var jsonfile = gson.fromJson(new FileReader(child), JsonObject.class);
            String moduleid = jsonfile.get("id").getAsString();
            String name1 = jsonfile.get("name").getAsJsonObject().get("fi").getAsString();
            moduleclass module = new moduleclass(moduleid, name1);
            all_modules.add(module);


            Set<String> keys = jsonfile.keySet();
            for(String key : keys){
                var a = jsonfile.get(key);
                getvalues(a, all_modules, moduleid);
            }
        }



        for (moduleclass module : all_modules)
        {
            System.out.println(module.get_id());

            System.out.println(module.get_name());

            System.out.println(module.get_ids());

        }







        /*
        File child_file = new File("./json/modules/otm-010acb27-0e5a-47d1-89dc-0f19a43a5dca.json");
        var child_json_file = gson.fromJson(new FileReader(child_file),
                JsonObject.class);

        String child_name = child_json_file.get("name").getAsJsonObject()
                .get("fi").getAsString();

        Label child_degree_label = new Label(child_name);

        maingrid.addRow(1,child_degree_label);
        */


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
    public void getvalues(JsonElement a, ArrayList<moduleclass> all_modules, String moduleid)  {
        String modulei = moduleid;

        try {
            if (a.isJsonObject()) {
                if (a.getAsJsonObject().has("moduleGroupId"))
                {

                    for (moduleclass module : all_modules){
                        if (module.get_id().equals(modulei)){
                            if(module.get_ids().contains(a.getAsJsonObject().get("moduleGroupId").getAsString())) {

                            }
                            else {
                                module.add_id(a.getAsJsonObject().get("moduleGroupId").getAsString());
                            }

                        }
                    }

                }


                for (int i = 0; i < a.getAsJsonObject().size(); i++) {
                    Set<String> keys = a.getAsJsonObject().keySet();
                    for(String key : keys){
                        var b = a.getAsJsonObject().get(key);
                        getvalues(b, all_modules, moduleid);
                    }



                }
            }
            else if (a.isJsonArray()) {

                var jsonData = a.getAsJsonArray();

                for (int i = 0; i < jsonData.size(); i++) {
                    var next_element = jsonData.get(Integer.parseInt(String.valueOf(i))); // Here's your key
                    getvalues(next_element, all_modules, modulei);
                }
            }
            else if (a.isJsonPrimitive()) {


            }
            else {


            }
        }
        catch (Exception E){

        }

    }
    class moduleclass {
        String id;
        String name;
        ArrayList<String> ids = new ArrayList<String>();

        public moduleclass(String new_id, String new_name){
            id = new_id;
            name = new_name;
        }
        public void add_id(String the_new){
            ids.add(the_new);
        }
        public String get_id(){
            return this.id;
        }
        public String get_name() {return this.name;}
        public ArrayList<String> get_ids() {return this.ids;}
    }





    public static void main(String[] args) {
        launch();
    }

}