package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Aplication.Main;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import model.entities.Formula;
import model.service.MatterService;
import model.service.FormulaService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FormulaListController implements Initializable, DataChangeListener {
    private FormulaService service;

    @FXML
    private TableColumn<Formula, String> tableColumnMatter;

    @FXML
    private TableView<Formula> tableViewFormula;

    @FXML
    private TableColumn<Formula, Integer> tableColumnId;

    @FXML
    private TableColumn<Formula, String> tableColumnName;

    @FXML
    private TableColumn<Formula, String> tableColumnDescFormula;

    @FXML
    private TableColumn<Formula, Formula> tableColumnEDIT;

    @FXML
    private TableColumn<Formula, Formula> tableColumnREMOVE;

    @FXML
    private Button btNew;

    private ObservableList<Formula> obsList;

    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Formula obj = new Formula();
        createDialogForm(obj,"/com/senac/projetointegradorcomeco/FormulaForm.fxml", parentStage);
    }
    public void setFormulaService(FormulaService service){
        this.service = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();

    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnDescFormula.setCellValueFactory(new PropertyValueFactory<>("descformula"));
        tableColumnMatter.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getMatter().getName()));

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewFormula.prefHeightProperty().bind(stage.heightProperty());

    }

    public void updateTableView(){
        if (service == null){
            throw new IllegalStateException("Service is null!");
        }
        List<Formula> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewFormula.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Formula obj, String absoluteName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            FormulaFormController controller = loader.getController();
            controller.setFormula(obj);
            controller.setService(new FormulaService(), new MatterService());
            controller.loadAssociatedObjects();
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter formula data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();

        } catch (IOException e){
            e.printStackTrace();
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }

    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Formula, Formula>() {
            private final Button button = new Button("Editar");
            @Override
            protected void updateItem(Formula obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "/com/senac/projetointegradorcomeco/FormulaForm.fxml",Utils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Formula, Formula>() {
            private final Button button = new Button("Remover");

            @Override
            protected void updateItem(Formula obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(obj));
            }
        });
    }

    private void removeEntity(Formula obj) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Confirma que quer deletar?");

        if (result.get() == ButtonType.OK){
            if (service == null){
                throw new IllegalStateException("Service estava null");
            }
            try {
                service.remove(obj);
                updateTableView();
            } catch (DbException e){
                Alerts.showAlert("Error removing object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

}
