package model.service;
import model.dao.DaoFactory;
import model.dao.MatterDao;
import model.entities.Matter;

import java.util.ArrayList;
import java.util.List;

public class MatterService {
    private MatterDao dao = DaoFactory.createMatterDao();

    public List<Matter> findAll() {
        return dao.findAll();
    }
    public void saveOrUpdate(Matter obj){
        if (obj.getId() == null){
            dao.insert(obj);
        } else {
            dao.update(obj);
        }
    }

    public void remove(Matter obj){
        dao.deleteById(obj.getId());
    }
}
