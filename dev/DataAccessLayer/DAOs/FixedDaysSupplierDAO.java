package DataAccessLayer.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DataAccessLayer.Repository;
import DataAccessLayer.DTOs.FixedDaysSupplierDTO;
import DataAccessLayer.DTOs.SupplierDTO;

public class FixedDaysSupplierDAO extends DAO<FixedDaysSupplierDTO> {
    SupplierDAO supplierDAO;

    private static FixedDaysSupplierDAO instance = null;

    public static FixedDaysSupplierDAO getInstance(){
        if(instance == null)
            instance = new FixedDaysSupplierDAO();
        return instance;
    }

    protected FixedDaysSupplierDAO() {
        super("FixedDaysSuppliers");
        supplierDAO = SupplierDAO.getInstance();
    }

    @Override
    public void insert(FixedDaysSupplierDTO dataObject) throws SQLException {
        supplierDAO.insert(dataObject.getSuper());
        super.insert(dataObject);
    }

    @Override
    public void update(FixedDaysSupplierDTO newDataObject) throws SQLException {
        supplierDAO.update(newDataObject.getSuper());
        super.update(newDataObject);
    }

    @Override
    public void delete(FixedDaysSupplierDTO dataObject) throws SQLException {
        supplierDAO.delete(dataObject.getSuper());
        super.delete(dataObject);
    }

    public void deleteById(int supplierId) throws SQLException {
        List<FixedDaysSupplierDTO> dtos = getById(supplierId);
        for(FixedDaysSupplierDTO dto: dtos ){
            delete(dto);
        }
        
    }

    @Override
    public FixedDaysSupplierDTO makeDTO(ResultSet rs) throws SQLException {
        if (!rs.next())
            return null;

        int supplierId = rs.getInt("supplierId");
        int dayOfSupply = rs.getInt("dayOfSupply");

        SupplierDTO supplierDTO = supplierDAO.getById(supplierId);
        return new FixedDaysSupplierDTO(supplierDTO, dayOfSupply);
    }

    public List<FixedDaysSupplierDTO> getById(int supplierId) throws SQLException {
        List<FixedDaysSupplierDTO> fixedDaysSupplierDTOs = new ArrayList<FixedDaysSupplierDTO>();
        Connection con = Repository.getInstance().connect();
        String query = "SELECT * FROM FixedDaysSuppliers WHERE supplierId= ?;";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setInt(1, supplierId);
        ResultSet supRS = statement.executeQuery();
        con.close();
        while (supRS.next()){
            fixedDaysSupplierDTOs.add(makeDTO(supRS));
        }
        statement.close();
        supRS.close();
        con.close();

        return fixedDaysSupplierDTOs;
    }

}
