package model.dao;

import model.entities.Matter;

import java.util.List;

public interface MatterDao {
    void insert(Matter obj);
    void update(Matter obj);
    void deleteById(Integer id);
    Matter findById(Integer id);

    List<Matter> findAll();
}
