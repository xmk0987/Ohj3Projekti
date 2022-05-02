package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
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

        // Tabien luominen
        TabPane tabPane = new TabPane();
        Tab tab1 = new Tab();
        Tab tab2 = new Tab();
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tab1.setText("Opiskelijan tiedot");
        tab2.setText("Opinnot");
        Scene mainscene=new Scene(tabPane,500,500);

        GridPane maingrid = new GridPane();
        GridPane studentinfo = new GridPane();

        tab1.setContent(studentinfo);
        tab2.setContent(maingrid);

        // Opiskelijan tiedot (tab1)
        Label studentname = new Label("Nimi: ");
        Label studentnumber = new Label("Opiskelijanumero: ");
        studentinfo.setHgap(30);
        studentinfo.addRow(0, studentname);
        studentinfo.addRow(1, studentnumber);

        // Module filejen läpikäynti
        Gson gson = new Gson();
        File moduleDir = new File("./json/modules");
        File[] dirList = moduleDir.listFiles();
        ArrayList<Module> all_modules = new ArrayList<>();
        assert dirList != null;
        for(File child : dirList)
        {
            var jsonfile = gson.fromJson(new FileReader(child), JsonObject.class);
            String moduleid = jsonfile.get("id").getAsString();
            String modulename = jsonfile.get("name").getAsJsonObject().get("fi").getAsString();
            String moduletype = jsonfile.get("type").getAsString();

            Module module = new Module(moduleid, modulename, moduletype);
            all_modules.add(module);

            Set<String> keys = jsonfile.keySet();
            for(String key : keys){
                var a = jsonfile.get(key);
                getValues(a, all_modules, moduleid);
            }
        }

        ArrayList<Course> all_courses = new ArrayList<>();
        File courseDir1 = new File("./json/courseunits");
        File[] dirList1 = courseDir1.listFiles();
        assert dirList1 != null;
        for(File child1 : dirList1)
        {
            try {
                JsonObject jsonfile1 = gson.fromJson(new FileReader(child1), JsonObject.class);
                String courseid = jsonfile1.get("id").getAsString();
                String coursename = jsonfile1.get("name").getAsJsonObject().get("fi").getAsString();
                String coursecredits = jsonfile1.get("credits").getAsJsonObject().get("max").getAsString();
                String groupid = jsonfile1.get("groupId").getAsString();

                Course course = new Course(courseid, coursename, coursecredits, groupid);
                all_courses.add(course);
            }
            catch (Exception ignored){}
        }

        // TreeTablen luominen
        ArrayList<Module> root_modules = new ArrayList<>();

        for(Module module : all_modules) {
            if(module.get_type().equals("DegreeProgramme")){
                root_modules.add(module);
            }
        }

        TreeItem<String> root_module_item = new TreeItem<>(root_modules.get(0).get_name());
        root_module_item.setExpanded(true);

        TreeTableColumn<String, String> treeTableColumn = new TreeTableColumn<>("Valitse opinnot");
        treeTableColumn.setPrefWidth(400);

        final TreeTableView<String> treeTableView = new TreeTableView<>(root_module_item);
        treeTableView.getColumns().add(treeTableColumn);

        treeTableColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue()));

        for ( Module module: all_modules){
            if( module.get_type().equals("DegreeProgramme")){
                link_module_course_ids(module, all_modules, all_courses, root_module_item);
            }
        }


        // Treetablen lisäys pääikkunaan
        maingrid.addRow(1, treeTableView);
        maingrid.setPadding(new Insets(10));
        maingrid.setPrefSize(200,200);

        // Väärä salasana/nimi Dialog -ikkuna
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Couldn't log in!");
        alert.setContentText("Wrong name or student number!");

        // Oikea salasana/nimi Dialog -ikkuna
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Log in Successful!");
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

                // Opiskelijan tietojen päivitys 1. tabiin
                Label s_name2 = new Label(name_value);
                Label s_studentnumber2 = new Label(student_number_value);
                studentinfo.addRow(0, s_name2);
                studentinfo.addRow(1, s_studentnumber2);
            }
            namef.clear();
            student_numberf.clear();
        });
    }

    // Yhdistää kaikki modulit ja kurssit keskenään ja luo niistä TreeItemin.
    public void link_module_course_ids(Module module_class, ArrayList<Module> all_modules, ArrayList<Course> all_courses, TreeItem<String> root){
        ArrayList<String> module_ids = module_class.child_module_ids;
        ArrayList<String> module_course_ids = module_class.child_course_ids;

        TreeItem<String> childNode1 = new TreeItem<>(module_class.get_name());

        // Alustaa oikein
        if (!(module_class.module_type.equals("DegreeProgramme"))){
            root.getChildren().addAll(childNode1);
        }

        // Yhdistää kaikki modulet toisiinsa
        for (String id : module_ids){
            for (Module module : all_modules){
                if (module.get_id().equals(id)){
                    if (!Objects.equals(module_class.module_type, "DegreeProgramme")){
                        link_module_course_ids(module, all_modules, all_courses,childNode1);
                    }
                    else{
                        link_module_course_ids(module, all_modules, all_courses,root);
                    }
                }
            }
        }

        // Lisää kaikki modulit modulin alle jos sillä on anymodule value
        if(module_class.anymodulerule){
            for(Module module: all_modules){
                if(module.module_type.equals("StudyModule") && !module.anycourseunitrule){
                    link_module_course_ids(module,all_modules,all_courses, childNode1);
                }
            }
        }

        // Lisää kaikki kurssit modulen alle jos sillä on anycourse value
        if(module_class.anycourseunitrule){
            for (Course course : all_courses){
                TreeItem<String> childNode2 = new TreeItem<>(course.get_name() + " " + course.get_cr() + "op");
                childNode1.getChildren().addAll(childNode2);
            }
        }

        // Lisää kaikki kurssit modulin alle jos siihen kuuluu kursseja
        for(Course course : all_courses){
            for(String course_id : module_course_ids){
                if (course_id.equals(course.get_groupid())) {
                    TreeItem<String> childNode2 = new TreeItem<>(course.get_name() + " " + course.get_cr() + "op");
                    childNode1.getChildren().addAll(childNode2);
                }
            }
        }
    }

    // Iteroi module tiedoston jokaisen objektin lävitse. Hakee moduleGroupId:n, CourseUnitGroupId:n,
    // AnyCourseUnitRule arvon ja AnyModuleRule arvon
    public void getValues(JsonElement a, ArrayList<Module> all_modules, String moduleid){
        try {
            if (a.isJsonObject()) {
                if (a.getAsJsonObject().has("moduleGroupId")) {
                    for (Module module : all_modules){
                        if (module.get_id().equals(moduleid)){
                            if(!(module.get_ids().contains(a.getAsJsonObject().get("moduleGroupId").getAsString()))) {
                                module.add_id(a.getAsJsonObject().get("moduleGroupId").getAsString());
                            }
                        }
                    }
                }
                if (a.getAsJsonObject().has("courseUnitGroupId")) {
                    for (Module module : all_modules){
                        if (module.get_id().equals(moduleid)){
                            if(!(module.get_course_ids().contains(a.getAsJsonObject().get("courseUnitGroupId").getAsString()))) {
                                module.add_course_ids(a.getAsJsonObject().get("courseUnitGroupId").getAsString());
                            }
                        }
                    }
                }
                if (a.getAsJsonObject().has("type")) {
                    if (Objects.equals(a.getAsJsonObject().get("type").getAsString(), "AnyCourseUnitRule")) {
                        for (Module module : all_modules) {
                            if (module.get_id().equals(moduleid)) {
                                module.add_anycourseunitrule();
                            }
                        }
                    }
                }
                if (a.getAsJsonObject().has("type")) {
                    if (Objects.equals(a.getAsJsonObject().get("type").getAsString(), "AnyModuleRule")) {
                        for (Module module : all_modules) {
                            if (module.get_id().equals(moduleid)) {
                                module.add_anymodulerule();
                            }
                        }
                    }
                }
                for (int i = 0; i < a.getAsJsonObject().size(); i++) {
                    Set<String> keys = a.getAsJsonObject().keySet();
                    for(String key : keys){
                        var b = a.getAsJsonObject().get(key);
                        getValues(b, all_modules, moduleid);
                    }
                }
            } else if (a.isJsonArray()) {
                var jsonData = a.getAsJsonArray();
                for (int i = 0; i < jsonData.size(); i++) {
                    var next_element = jsonData.get(Integer.parseInt(String.valueOf(i)));
                    getValues(next_element, all_modules, moduleid);
                }
            }
        }
        catch (Exception ignored){}
    }

    static class Module {
        String id;
        String name;
        String module_type;
        boolean anycourseunitrule = false;
        boolean anymodulerule = false;
        ArrayList<String> child_course_ids = new ArrayList<String>();
        ArrayList<String> child_module_ids = new ArrayList<String>();

        public Module(String new_id, String new_name, String moduletype) {
            id = new_id;
            name = new_name;
            module_type = moduletype;
        }

        public void add_id(String the_new) {
            child_module_ids.add(the_new);
        }

        public void add_anycourseunitrule() {
            this.anycourseunitrule = true;
        }

        public void add_anymodulerule() {
            this.anymodulerule = true;
        }

        public void add_course_ids(String new_child_id) {
            child_course_ids.add(new_child_id);
        }

        public String get_type(){
            return this.module_type;
        }

        public String get_id() {
            return this.id;
        }

        public String get_name() {
            return this.name;
        }

        public ArrayList<String> get_ids() {
            return this.child_module_ids;
        }

        public ArrayList<String> get_course_ids() {
            return this.child_course_ids;
        }
    }
    static class Course {
        String id;
        String name;
        String cr;
        String groupid;

        public Course(String new_id, String new_name, String new_cr, String new_groupid) {
            id = new_id;
            name = new_name;
            cr = new_cr;
            groupid = new_groupid;
        }

        public String get_cr(){
            return this.cr;
        }

        public String get_name() {
            return this.name;
        }

        public String get_groupid() {
            return this.groupid;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}