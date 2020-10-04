package sample;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class ResultController {

    @FXML
    private TableView<PairEntry> operandTableView;
    @FXML
    private TableView<PairEntry> operatorTableView;
    @FXML
    private Label lbDictOperator;
    @FXML
    private Label lbDictOperand;
    @FXML
    private Label lbCountOperator;
    @FXML
    private Label lbCountOperand;
    @FXML
    private Label lbDictProgram;
    @FXML
    private Label lbProgramLength;
    @FXML
    private Label lbProgramVolume;
    @FXML
    private Button btnExit;

    public void initialize(){
        setOKBtn();
        lbDictOperator.setText(String.valueOf(MainController.metrics.dictOperatorCount));
        lbDictOperand.setText(String.valueOf(MainController.metrics.dictOperandCount));
        lbCountOperator.setText(String.valueOf(MainController.metrics.operatorCount));
        lbCountOperand.setText(String.valueOf(MainController.metrics.operandCount));
        lbDictProgram.setText(String.valueOf(MainController.metrics.dictProgram));
        lbProgramLength.setText(String.valueOf(MainController.metrics.programLength));
        lbProgramVolume.setText(String.valueOf(Math.round(MainController.metrics.volumeLength * 100) / 100.0));
        setOperandTableView();
        setOperatorTableView();
    }

    private void setOKBtn(){
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage wnd = (Stage) btnExit.getParent().getScene().getWindow();
                wnd.close();
            }
        });
    }

    private void setOperandTableView(){
        ArrayList<PairEntry> operandArrayList = new ArrayList<>();
        for (Map.Entry<String, Integer> it : MainController.metrics.operands.entrySet()){
            operandArrayList.add(new PairEntry(it.getKey(), it.getValue()));
        }
        ObservableList<PairEntry> operandEntries = FXCollections.observableList(operandArrayList);



        operandTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        operandTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("count"));
        operandTableView.setItems(operandEntries);
    }

    private void setOperatorTableView(){
        ObservableList<PairEntry> operatorEntries = FXCollections.observableList(MainController.metrics.operatorsArrayList);


        operatorTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
        operatorTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("count"));
        operatorTableView.setItems(operatorEntries);
    }
}
