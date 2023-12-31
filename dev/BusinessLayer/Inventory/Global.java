package BusinessLayer.Inventory;

import DataAccessLayer.DAOs.*;

import java.sql.SQLException;

public class Global {

    public static int Categoryid = 0;
    private static boolean isInitialCategory =false;
    public static int SpecificId = 0;
    private static boolean isInitialSpecific =false;

    public static int reportId = 0;
    private static boolean isInitialReport =false;
    public static int periodicId = 0;
    private static boolean isInitialPeriodic =false;
    public static int discountId = 0;
    private static boolean isInitialDiscount =false;
//

    public static int getNewCategoryid() throws SQLException {

        if(!isInitialCategory && Categoryid == 0 ) {
            Categoryid = CategoryDAO.getInstance().getLastId();
            isInitialCategory = true;
            Categoryid += 1;
            return Categoryid;
        }

        return ++Categoryid;
    }

    /**
     *
     *
     * @return
     */
    public static int getNewSpecificId() throws SQLException {
        if(!isInitialSpecific && SpecificId == 0)
        {
            SpecificId = SpecificProductDAO.getInstance().getLastId();
            isInitialSpecific = true;
            SpecificId += 1;
            return SpecificId;
        }
        return ++SpecificId;
    }

    public static int getNewReportId() throws SQLException {
        if(!isInitialReport && reportId == 0) {
            reportId = ReportDAO.getInstance().getLastId();
            isInitialReport = true;
            reportId += 1;
            return  reportId;
        }

        return ++reportId;
    }

    public static int getNewPeriodicId() {
//        if(!isInitialPeriodic && periodicId == 0) {
//            periodicId = PeriodicReservationDAO.getInstance().getLastId();
//            isInitialReport = true;
//        }
        return periodicId++;
    }

    public static int getNewDiscountId() throws SQLException {
        if(!isInitialDiscount && discountId == 0) {
            discountId = DiscountDAO.getInstance().getLastId();
            isInitialDiscount = true;
            discountId += 1;
            return discountId;
        }
        return ++discountId;
    }

}
