package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MainController {

    @FXML
    private Button btnDoHalstead;
    @FXML
    private MenuItem miOpenFile;
    @FXML
    private MenuItem miSaveFile;
    @FXML
    private MenuItem miCloseFile;
    @FXML
    private MenuItem miAbout;
    @FXML
    private TextArea txarFileEditor;

    private TypescriptFile currFile = null;

    private EditorModel editModel;

    public static Halstead metrics;

    public void initialize(){
        createNewEditorModel();
        setOpenFileMenuItem();
        setSaveFileMenuItem();
        setCloseFileMenuItem();
        setAboutMenuItem();
        setHalsteadButton();
    }

    private void createNewHalsteadMetrics(TypescriptFile file) throws IOException {
        metrics = new Halstead(file.getPathToFile().toFile());
    }

    private void createNewEditorModel(){
        editModel = new EditorModel();
    }

    private void setOpenFileMenuItem(){
        miOpenFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Выберите файл с кодом Typescript");
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Файлы Groovy", "*.txt"));

                Parent firstL     = txarFileEditor.getParent();
                Scene mainScene  = firstL.getScene();
                Window mainWindow = mainScene.getWindow();

                File file = chooser.showOpenDialog(mainWindow);

                if (file != null) {
                    try {
                        currFile = editModel.load(file.toPath());

                        txarFileEditor.clear();
                        for (String line : currFile.getContent()) {
                            txarFileEditor.appendText(line);
                            txarFileEditor.appendText("\n");
                        }
                    } catch (IOException e) {
                        getAlert("Не удалось открыть файл Groovy!");
                    }
                }
            }
        });
    }

    private void setSaveFileMenuItem(){
        miSaveFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (currFile != null) {
                    currFile = new TypescriptFile(currFile.getPathToFile(), Arrays.asList(txarFileEditor.getText().split("\n")));
                    try {
                        editModel.save(currFile);
                    } catch (IOException e) {
                        getAlert("Не удалось перезаписать Groovy файл!");
                    }
                }
            }
        });
    }

    private void setCloseFileMenuItem(){
        miCloseFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                editModel.close();
            }
        });
    }

    private void setAboutMenuItem(){
        miAbout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Справка");
                alert.setHeaderText(null);
                alert.setContentText("Разработчики программы:\nЯкшук В.Д.\nПлотников В.В.\nПроверила:\nБолтак С.В.");

                Stage scene = (Stage) alert.getDialogPane().getScene().getWindow();
                scene.getIcons().add(new Image(Main.class.getResourceAsStream("groovyLogo.png")));

                alert.showAndWait();
            }
        });
    }

    private void setHalsteadButton(){

        btnDoHalstead.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    createNewHalsteadMetrics(currFile);
                } catch (IOException e) {
                    getAlert("Не удалось определить метрику Холстеда!");
                    return;
                } catch (NullPointerException a){
                    getAlert("Не был выбран файл Groovy!");
                    return;
                }

                try {
                    makeAndShowStatisticWindow();
                } catch (IOException e) {
                    getAlert("Не удалось создать окно метрики!");
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    private void makeAndShowStatisticWindow() throws IOException{
        Stage resStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("resLayout.fxml"));
        resStage.setScene(new Scene(root, 441, 534));
        resStage.initModality(Modality.APPLICATION_MODAL);
        resStage.getIcons().add(new Image(Main.class.getResourceAsStream("groovyLogo.png")));
        resStage.setResizable(false);
        resStage.show();
    }

    private static void getAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        Stage scene = (Stage) alert.getDialogPane().getScene().getWindow();
        scene.getIcons().add(new Image(Main.class.getResourceAsStream("groovyLogo.png")));

        alert.showAndWait();
    }
}
