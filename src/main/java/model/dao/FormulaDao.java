package model.dao;

import model.entities.Formula;
import model.entities.Matter;

import java.util.List;

public interface FormulaDao {
    void insert(Formula obj);
    void update(Formula obj);
    void deleteById(Integer id);
    Formula findById(Integer id);

    List<Formula> findAll();

    List<Formula> findByMatter(Matter matter);
}
