package DataAccessLayer.DAOs;

import java.sql.ResultSet;
import java.sql.SQLException;

import DataAccessLayer.Repository;
import DataAccessLayer.DTOs.ProductBranchDTO;

public class ProductBranchDAO extends DAO<ProductBranchDTO> {
    private static ProductBranchDAO instance = null;
    private ProductsDAO productDAO;
    private Repository repo;

    private ProductBranchDAO() {
        super("ProductBranch");
        productDAO = ProductsDAO.getInstance();
        repo = Repository.getInstance();
    }

    public static ProductBranchDAO getInstance() {
        if (instance == null) {
            instance = new ProductBranchDAO();
        }
        return instance;
    }

    @Override
    public ProductBranchDTO makeDTO(ResultSet rs) throws SQLException {
        if (!rs.next())
            throw new SQLException("Can't make DTO from nothing!");

        int productId = rs.getInt("productId");
        int branchId = rs.getInt("branchId");
        double price = rs.getDouble("price");
        int minQuantity = rs.getInt("minQuantity");

        return new ProductBranchDTO(productDAO.getById(productId), branchId, price, minQuantity);
    }

    public ProductBranchDTO getByProductAndBranch(int productId, int branchId) throws SQLException {
        ResultSet rs = repo.executeQuery("SELECT * FROM " + tableName + " WHERE productId = ? AND branchId = ?;",
                productId, branchId);
        ProductBranchDTO dto = makeDTO(rs);
        rs.close();
        return dto;
    }

}
