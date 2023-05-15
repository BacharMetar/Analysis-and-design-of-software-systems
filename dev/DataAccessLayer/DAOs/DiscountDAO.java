package DataAccessLayer.DAOs;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import DataAccessLayer.Repository;
import DataAccessLayer.DTOs.DiscountDTO;

public class DiscountDAO extends DAO<DiscountDTO> {
    private static DiscountDAO instance = null;
    private Repository repo;

    protected DiscountDAO() {
        super("Discounts");
        repo = Repository.getInstance();
    }

    public static DiscountDAO getInstance() {
        if (instance == null) {
            instance = new DiscountDAO();
        }
        return instance;
    }

    @Override
    public DiscountDTO makeDTO(Map<String, Object> row) throws SQLException {
        int id = (int) row.get("id");
        LocalDate startDate = LocalDate.parse((String) row.get("startDate"));
        String endDateString = (String) row.get("endDate");
        LocalDate endDate = null;
        if (endDateString != null) {
            endDate = LocalDate.parse(endDateString);
        }
        double val = (double) row.get("val");
        String dType = (String) row.get("dType");

        return new DiscountDTO(id, startDate, endDate, val, dType);
    }

    public DiscountDTO getById(int id) throws SQLException {
        List<Map<String, Object>> rows = repo.executeQuery("SELECT * FROM Discounts WHERE id = ?;", id);
        if (rows.size() > 0)
            return makeDTO(rows.get(0));

        return null;
    }

    public int getLastId() throws SQLException {
        String query = "SELECT * FROM Discounts WHERE id = (SELECT Max(id) FROM Discounts);";
        List<Map<String, Object>> rows = repo.executeQuery(query);

        if (rows.size() > 0)
            return makeDTO(rows.get(0)).getId();

        return -1;
    }

}
