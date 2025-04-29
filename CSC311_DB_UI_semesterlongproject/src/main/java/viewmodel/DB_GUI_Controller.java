package viewmodel;

import dao.DbConnectivityClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class DB_GUI_Controller implements Initializable {

    @FXML
    TextField first_name, last_name, department, email, imageURL;
    @FXML
    ImageView img_view;
    @FXML
    ComboBox<String> major;
    @FXML
    MenuBar menuBar;
    @FXML
    private MenuItem menuAdd, menuEdit, menuDelete, menuClear;
    @FXML
    private MenuItem menuImportCSV, menuExportCSV;
    @FXML
    private TableView<Person> tv;
    @FXML
    Button addBtn, deleteBtn, editBtn;
    @FXML
    private Label sysErrorLabel, fNameLabel, lNameLabel, departmentLabel, majorLabel, emailLabel;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();
    private final ObservableList<String> majorOptions = FXCollections.observableArrayList("CS", " CPIS", "English", "Biology");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);

            major.setItems(majorOptions);
            major.getSelectionModel().selectFirst();

            updateButtonStates();

            first_name.textProperty().addListener((observable, oldValue, newValue) -> validateFormFields());
            last_name.textProperty().addListener((observable, oldValue, newValue) -> validateFormFields());
            department.textProperty().addListener((observable, oldValue, newValue) -> validateFormFields());
            email.textProperty().addListener((observable, oldValue, newValue) -> validateFormFields());
            majorLabel.textProperty().addListener((observable, oldValue, newValue) -> validateFormFields());

            tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateSelectionDependentButtons(newValue != null));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateSelectionDependentButtons(boolean hasSelection){
        editBtn.setDisable(!hasSelection);
        deleteBtn.setDisable(!hasSelection);

        if(menuEdit != null) menuEdit.setDisable(!hasSelection);
        if(menuDelete != null) menuDelete.setDisable(!hasSelection);
    }

    private void updateSystemStatus(String status){
    if(sysErrorLabel != null) {
        sysErrorLabel.setText(status);
    }
    }

    private void validateFormFields(){
        boolean valid = isFormValid();
        addBtn.setDisable(!valid);

        if(menuAdd != null) menuAdd.setDisable(!valid);

        if(tv.getSelectionModel().getSelectedItem() != null){
            editBtn.setDisable(!valid);
            if(menuEdit != null) menuEdit.setDisable(!valid);
        }

        FieldRegex fieldCheck = new FieldRegex();

        if(!(first_name.getText().isEmpty() && last_name.getText().isEmpty() && department.getText().isEmpty() && email.getText().isEmpty())&&
                !fieldCheck.FieldRegex(first_name.getText(), last_name.getText(), department.getText(), email.getText())) {
            sysErrorLabel.setText("There is an an error in one of your fields.");
        }else{
            sysErrorLabel.setText("");
        }

        if(!first_name.getText().isEmpty() && !fieldCheck.firstnameMatch(first_name.getText())){
            fNameLabel.setText("First name invalid");
        }else{
            fNameLabel.setText("");
        }
        if(!last_name.getText().isEmpty() && !fieldCheck.lastNameMatch(last_name.getText())){
            lNameLabel.setText("Last name invalid");
        }else{
            lNameLabel.setText("");
        }
        if(!department.getText().isEmpty() && !fieldCheck.departmentMatch(department.getText())){
            departmentLabel.setText("Department invalid");
        }else{
            departmentLabel.setText("");
        }
        if(!email.getText().isEmpty() && !fieldCheck.emailMatch(email.getText())){
            emailLabel.setText("Email invalid");
        }else{
            emailLabel.setText("");
        }
    }

    private boolean isFormValid(){
        FieldRegex fr = new FieldRegex();
        String majorValue = major.getValue() != null ? major.getValue() : "";

        boolean check = fr.FieldRegex(first_name.getText(), last_name.getText(), department.getText(), email.getText());

        return check && (!first_name.getText().trim().isEmpty() &&
                !last_name.getText().trim().isEmpty() &&
                !department.getText().trim().isEmpty() &&
                major.getValue() != null &&
                !email.getText().trim().isEmpty());
    }

    private void updateButtonStates(){
        boolean hasSelection = tv.getSelectionModel().getSelectedItem() != null;
        updateSelectionDependentButtons(hasSelection);
        validateFormFields();
    }

    @FXML
    protected void addNewRecord() {
        sysErrorLabel.setText("");fNameLabel.setText("");lNameLabel.setText("");
        departmentLabel.setText("");majorLabel.setText("");emailLabel.setText("");

//        FieldRegex fieldCheck = new FieldRegex();
//        boolean check = fieldCheck.FieldRegex(first_name.getText(), last_name.getText(),department.getText(),email.getText());
//
//        if(!check){
//            sysErrorLabel.setText("There is an error in one of your fields");
//            if(!fieldCheck.firstnameMatch(first_name.getText())){
//                fNameLabel.setText("First name invalid");
//            }
//            if(!fieldCheck.lastNameMatch(last_name.getText())){
//                lNameLabel.setText("Last name invalid");
//            }
//            if(!fieldCheck.departmentMatch(department.getText())){
//                departmentLabel.setText("Department invalid");
//            }
//            if(!fieldCheck.emailMatch(email.getText())){
//                emailLabel.setText("Email invalid");
//            }
//            return;
//        }else {
            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    major.getValue(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
            sysErrorLabel.setText("User added successfully!");
//        }

    }

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        major.getSelectionModel().selectFirst();
        email.setText("");
        imageURL.setText("");
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                major.getValue(), email.getText(),  imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);
    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());

        String personmajor = p.getMajor();
        if(majorOptions.contains(personmajor)){
            major.setValue(personmajor);
        }else{
            major.getSelectionModel().selectFirst();
        }
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    private static enum Major {Business, CSC, CPIS}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }

    @FXML
    protected void importCSV(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file(*.csv)", "*.csv"));

        File file = fileChooser.showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                br.readLine();

                int importedRecords = 0;
                int failedRecords = 0;

                while ((line = br.readLine()) != null) {
                    String[] fields = line.split(",");
                    if (fields.length >= 5) {
                        try {
                            String fName = fields[0].trim();
                            String lName = fields[1].trim();
                            String dept = fields[2].trim();
                            String maj = fields[3].trim();
                            String em = fields[4].trim();
                            String img = fields.length > 5 ? fields[5].trim() : "";

                            // Add the new person to database
                            Person p = new Person(fName, lName, dept, maj, em, img);
                            cnUtil.insertUser(p);

                            // Retrieve the generated ID
                            p.setId(cnUtil.retrieveId(p));

                            // Add to the table
                            data.add(p);
                            importedRecords++;


                        } catch (Exception e) {
                            failedRecords++;
                            System.err.println("Error importing" + line);
                            e.printStackTrace();
                        }
                    } else {
                        failedRecords++;
                    }
                }
                updateSystemStatus("imported " + importedRecords + " records");
            }catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import Error");
            alert.setHeaderText("Error importing CSV");
            alert.setContentText("Error while importing: " + e.getMessage());
            alert.showAndWait();
            updateSystemStatus("Error importing CSV. Culprit: " + e.getMessage());
            }
        }
    }
    @FXML
    protected void exportCSV(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

        File file = fileChooser.showSaveDialog(tv.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write header
                writer.println("FirstName,LastName,Department,Major,Email,ImageURL");

                // Write data
                for (Person person : data) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(escapeCSV(person.getFirstName())).append(",");
                    sb.append(escapeCSV(person.getLastName())).append(",");
                    sb.append(escapeCSV(person.getDepartment())).append(",");
                    sb.append(escapeCSV(person.getMajor())).append(",");
                    sb.append(escapeCSV(person.getEmail())).append(",");
                    sb.append(escapeCSV(person.getImageURL()));
                    writer.println(sb.toString());
                }

                updateSystemStatus("Export complete: " + data.size() + " records exported to " + file.getName());

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Error");
                alert.setHeaderText("Error Exporting CSV");
                alert.setContentText("An error occurred while exporting the file: " + e.getMessage());
                alert.showAndWait();
                updateSystemStatus("Export failed: " + e.getMessage());
            }
        }
    }
    private String escapeCSV(String s) {
        if(s == null) return "";

        if(s.contains(",") || s.contains("\"") || s.contains("\n")){
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }return s;
    }
}