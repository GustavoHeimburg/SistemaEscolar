package model.dao.impl;


import db.DB;
import db.DbException;
import model.dao.FormulaDao;
import model.entities.Matter;
import model.entities.Formula;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormulaDaoJDBC implements FormulaDao {
    private Connection conn;

    public FormulaDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Formula obj) {
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement(
                    "insert into formula " +
                            "(Name, DescFormula, MatterId) " +
                            "values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getName());
            st.setString(2, obj.getDescformula());
            st.setInt(3, obj.getMatter().getId());

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()){
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Error! No rows affected!");
            }

        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Formula obj) {
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement(
                    "update formula " +
                            "set Name = ?, DescFormula = ?, MatterId = ? " +
                            "where id = ?");

            st.setString(1, obj.getName());
            st.setString(2, obj.getDescformula());
            st.setInt(3, obj.getMatter().getId());
            st.setInt(4, obj.getId());

            st.executeUpdate();

        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement("delete from formula where Id = ?");

            st.setInt(1, id);

            int rowsAffected = st.executeUpdate();

            if (rowsAffected == 0){
                throw new DbException("Formula inexistente!");
            }

        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Formula findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement("" +
                    "select formula.*, matter.Name as MatName " +
                    "from formula inner join matter " +
                    "on formula.MatterId = matter.Id " +
                    "where formula.Id = ?");

            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()){
                Matter mat = instantiateMatter(rs);
                Formula obj = instantiateFormula(rs, mat);
                return obj;

            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private Matter instantiateMatter(ResultSet rs) throws SQLException {
        Matter mat = new Matter();
        mat.setId(rs.getInt("MatterId"));
        mat.setName(rs.getString("MatName"));
        return mat;
    }

    private Formula instantiateFormula (ResultSet rs, Matter Mat) throws SQLException{
        Formula obj = new Formula();
        obj.setId(rs.getInt("Id"));
        obj.setName(rs.getString("Name"));
        obj.setDescformula(rs.getString("DescFormula"));
        obj.setMatter(Mat);
        return obj;
    }
    @Override
    public List<Formula> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement("" +
                    "select formula.*, matter.Name as MatName " +
                    "from formula inner join matter " +
                    "on formula.MatterId = matter.Id " +
                    "order by Name");

            rs = st.executeQuery();

            List<Formula> list = new ArrayList<>();
            Map<Integer, Matter> map = new HashMap<>();

            while (rs.next()){

                Matter mat = map.get(rs.getInt("MatterId"));

                if (mat == null){
                    mat = instantiateMatter(rs);
                    map.put(rs.getInt("MatterId"), mat);
                }

                Formula obj = instantiateFormula(rs, mat);
                list.add(obj);
            }
            return list;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }


    @Override
    public List<Formula> findByMatter(Matter matter) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement("" +
                    "select formula.*, matter.Name as MatName " +
                    "from formula inner join matter " +
                    "on formula.MatterId = matter.Id " +
                    "where MatterId = ? " +
                    "order by Name");

            st.setInt(1, matter.getId());

            rs = st.executeQuery();

            List<Formula> list = new ArrayList<>();
            Map<Integer, Matter> map = new HashMap<>();

            while (rs.next()){

                Matter mat = map.get(rs.getInt("MatterId"));

                if (mat == null){
                    mat = instantiateMatter(rs);
                    map.put(rs.getInt("MatterId"), mat);
                }

                Formula obj = instantiateFormula(rs, mat);
                list.add(obj);
            }
            return list;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }


}

