package model.service;

import model.dao.DaoFactory;
import model.dao.FormulaDao;
import model.entities.Formula;

import java.util.List;

public class FormulaService {

    //dependencia injetada usando padrao factory
    private FormulaDao dao = DaoFactory.createFormulaDao();

    public List<Formula> findAll() {
        return dao.findAll();
    }
    public void saveOrUpdate(Formula obj){
        if (obj.getId() == null){
            dao.insert(obj);
        } else {
            dao.update(obj);
        }
    }

    public void remove(Formula obj){
        dao.deleteById(obj.getId());
    }
}
