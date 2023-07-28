package model.dao;

import db.DB;
import model.dao.impl.FormulaDaoJDBC;
import model.dao.impl.MatterDaoJDBC;

public class DaoFactory {

    public static FormulaDao createFormulaDao(){return new FormulaDaoJDBC(DB.getConnection());}

    public static MatterDao createMatterDao(){
        return new MatterDaoJDBC(DB.getConnection());
    }

}