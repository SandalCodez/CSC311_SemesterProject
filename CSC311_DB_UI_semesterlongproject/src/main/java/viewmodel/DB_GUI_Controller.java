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

import java.io.File;
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

    private void validateFormFields(){
        boolean valid = isFormValid();
        addBtn.setDisable(!valid);

        if(menuAdd != null) menuAdd.setDisable(!valid);

        if(tv.getSelectionModel().getSelectedItem() != null){
            editBtn.setDisable(!valid);
            if(menuEdit != null) menuEdit.setDisable(!valid);
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

        FieldRegex fieldCheck = new FieldRegex();
        boolean check = fieldCheck.FieldRegex(first_name.getText(), last_name.getText(),department.getText(),email.getText());

        if(!check){
            sysErrorLabel.setText("There is an error in one of your fields");
            if(!fieldCheck.firstnameMatch(first_name.getText())){
                fNameLabel.setText("First name invalid");
            }
            if(!fieldCheck.lastNameMatch(last_name.getText())){
                lNameLabel.setText("Last name invalid");
            }
            if(!fieldCheck.departmentMatch(department.getText())){
                departmentLabel.setText("Department invalid");
            }
            if(!fieldCheck.emailMatch(email.getText())){
                emailLabel.setText("Email invalid");
            }
            return;
        }else {
            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    major.getValue(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
            sysErrorLabel.setText("User added successfully!");
        }

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

}