package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import model.entities.Matter;
import model.entities.Formula;
import model.exeption.ValidationException;
import model.service.MatterService;
import model.service.FormulaService;

import java.net.URL;
import java.util.*;

public class  FormulaFormController implements Initializable {

    private Formula entity;

    private FormulaService service;

    private MatterService matterService;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtDescFormula;

    @FXML
    private ComboBox<Matter> comboBoxMatter;
    @FXML
    private Label labelErrorName;

    @FXML
    private Label labelErrorDescFormula;


    @FXML
    private Button btSave;

    @FXML
    private Button btCancel;

    private ObservableList<Matter> obsList;

    public FormulaFormController() {
    }



    public void setFormula(Formula entity){
        this.entity = entity;
    }

    public void setService(FormulaService service, MatterService matterService){
        this.service = service;
        this.matterService = matterService;
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    @FXML
    public void onBtSaveAction(ActionEvent event) {
        entity.setName(txtName.getText());
        entity.setDescformula(txtDescFormula.getText());
        entity.setMatter(comboBoxMatter.getValue());

        //validacao manual pois nao esta sendo usado framework para injetar dependencia
        if (entity == null){
            throw new IllegalStateException("Entidade nula");
        }
        if (service == null){
            throw new IllegalStateException("Servico nulo");
        }

        try {
            service.saveOrUpdate(entity);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        } catch (DbException e){
            Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), Alert.AlertType.ERROR);
        } catch (ValidationException e){
            setErrorMessages(e.getErrors());
        }
    }

    private void notifyDataChangeListeners() {
        for (DataChangeListener listener : dataChangeListeners){
            listener.onDataChanged();
        }
    }

    private Formula getFormData() {
        Formula obj = new Formula();

        ValidationException exception = new ValidationException("Erro na validacao");

        obj.setId(Utils.tryParseToInt(txtId.getText()));

        if (txtName.getText() == null || txtName.getText().trim().equals("")){
            exception.addError("name", "campo nao pode ser vazio");
        }
        obj.setName(txtName.getText());

        if (txtDescFormula.getText() == null || txtDescFormula.getText().trim().equals("")){
            exception.addError("Descricao formula", "campo nao pode ser vazio");
        }
        obj.setDescformula(txtDescFormula.getText());

        obj.setMatter(comboBoxMatter.getValue());

        if (exception.getErrors().size() > 0){
            throw exception;
        }

        return obj;
    }

    @FXML
    public void onBtCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 70);
        Constraints.setTextFieldMaxLength(txtDescFormula, 9999);
        initializeComboBoxMatter();

    }

    public void updateFormData(){

        if (entity == null){
            throw new IllegalStateException("Entidade nula");
        }

        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
        txtDescFormula.setText(entity.getDescformula());

        Locale.setDefault(Locale.US);

        if (entity.getMatter() == null) {
            comboBoxMatter.getSelectionModel().selectFirst();
        } else {
            comboBoxMatter.setValue(entity.getMatter());
        }

    }

    public void loadAssociatedObjects(){

        if (matterService == null){
            throw new IllegalStateException("MatterService was null");
        }

        List<Matter> list = matterService.findAll();
        obsList = FXCollections.observableArrayList(list);
        comboBoxMatter.setItems(obsList);
    }

    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();

        labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
        labelErrorDescFormula.setText((fields.contains("descformula") ? errors.get("descformula") : ""));
        labelErrorName.getStyleClass().add("button");

    }

    private void initializeComboBoxMatter() {
        Callback<ListView<Matter>, ListCell<Matter>> factory = lv -> new ListCell<Matter>() {
            @Override
            protected void updateItem(Matter item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxMatter.setCellFactory(factory);
        comboBoxMatter.setButtonCell(factory.call(null));
    }

}
