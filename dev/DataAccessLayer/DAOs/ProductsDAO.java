package DataAccessLayer.DAOs;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import DataAccessLayer.DTOs.CategoryDTO;
import DataAccessLayer.DTOs.ProductDTO;

public class ProductsDAO extends DAO<ProductDTO> {
    private static ProductsDAO instance = null;

    protected ProductsDAO() {
        super("Products");
    }

    public static ProductsDAO getInstance() {
        if (instance == null) {
            instance = new ProductsDAO();
        }
        return instance;
    }

    @Override
    public ProductDTO makeDTO(Map<String, Object> row) throws SQLException {
        int id = (int) row.get("id");
        String name = (String) row.get("name");
        String manufacturer = (String) row.get("manufacturer");
        int categoryId = (int) row.get("categoryId");

        CategoryDTO category = CategoryDAO.getInstance().getById(categoryId);

        return new ProductDTO(id, name, manufacturer, category);
    }

    public ProductDTO getById(int id) throws SQLException {
        String query = "SELECT * FROM Products WHERE id= ?;";
        List<Map<String, Object>> rows = repo.executeQuery(query, id);
        if (rows.size() > 0)
            return makeDTO(rows.get(0));
        return null;
    }

}
