package BusinessLayer.Inventory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import BusinessLayer.InveontorySuppliers.*;
import BusinessLayer.exceptions.InventoryException;
import DataAccessLayer.DAOs.DiscountDAO;
import DataAccessLayer.DAOs.SpecificProductDAO;
import DataAccessLayer.DTOs.DiscountDTO;
import DataAccessLayer.DTOs.ProductBranchDTO;
import DataAccessLayer.DTOs.SpecificProductDTO;

public class ProductBranch {

    private Product product;
    private double price;
    private int idealQuantity;
    private int minQuantity;
    private int totalAmount;
    private HashMap<Integer, SpecificProduct> allSpecificProducts; // maps between specificId and its object
    private List<Discount> discountsHistory;
    private Discount discount;
    private ProductBranchDTO productBranchDTO;

    // TODO : check if product exist on suppliers
    private Boolean ExistOnSuppliers;

    public ProductBranch(Product product, double price, int idealQuantity, int minQuantity) {
        this.product = product;
        this.price = price;
        this.idealQuantity = idealQuantity;
        this.minQuantity = minQuantity;
        this.allSpecificProducts = new HashMap<>();
        this.discount = null;
        this.totalAmount = 0;
        this.discountsHistory = new ArrayList<>();
    }

    public ProductBranch(ProductBranchDTO productBranchDTO) throws SQLException {
        ProductController productController = ProductController.getInstance();
        this.product = productController.getProductById(productBranchDTO.getProductDTO().getId());
        this.price = productBranchDTO.getPrice();
        this.idealQuantity = productBranchDTO.getIdealQuantity();
        this.minQuantity = productBranchDTO.getMinQuantity();

        HashMap<Integer, SpecificProduct> specificProductMap = new HashMap<>();
        List<SpecificProductDTO> specificProductDTOS = SpecificProductDAO.getInstance().selectAllById(product.getId());
        // HashMap<Integer, SpecificProductDTO> dtos =
        // productBranchDTO.getAllSpecificProducts();
        if (specificProductDTOS != null) {
            for (SpecificProductDTO specificProductDTO : specificProductDTOS) {
                SpecificProduct specificProduct = new SpecificProduct(specificProductDTO);
                specificProductMap.put(specificProduct.getSpecificId(), specificProduct);
                // SpecificProductDAO.getInstance().insert(dto);
            }
//            this.allSpecificProducts = specificProductMap;

        }

        this.allSpecificProducts = specificProductMap;

        // this.discount =
        // discountController.getDiscountById(productBranchDTO.getDiscountDTO().getId());
        this.discount = null;
        this.totalAmount = allSpecificProducts.size();
        this.discountsHistory = new ArrayList<>();
        this.productBranchDTO = productBranchDTO;
    }

    public Discount getDiscount() {
        return discount;
    }

    public ProductBranchDTO getProductBranchDTO() {
        return productBranchDTO;
    }

    public SpecificProduct getSpecificById(int specificId) {
        return allSpecificProducts.get(specificId);
    }

    public HashMap<Integer, SpecificProduct> getAllSpecificProducts() {
        return allSpecificProducts;
    }

    public double getPrice() throws SQLException {
        this.discount = getCurrentMaxDiscount();
        if (discount != null) {
            return discount.getPriceWithDiscount(price);
        } else
            return price;
    }

    public String getName() {
        return product.getName();
    }

    public int getIdealQuantity() {
        return idealQuantity;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public List<Discount> getDiscountsHistory() {
        return discountsHistory;
    }

    public void changeProductStatus(int specificProduct, ProductStatus.status status) {
        SpecificProduct sp = allSpecificProducts.get(specificProduct);
        if (sp != null)
            sp.setStatus(status);
    }

    public void setFlowDescription(int specificProduct, String description) {
        SpecificProduct sp = allSpecificProducts.get(specificProduct);
        if (sp != null)
            sp.setFlawDescription(description);
    }

    public boolean applyDiscount(Discount discountNew) throws SQLException {
        if(discountsHistory.size() == 0)
            loadDiscountHistory();
        discountsHistory.add(discountNew);
        Discount maxDiscount = getCurrentMaxDiscount();
        this.discount = maxDiscount;
        if (discountNew == maxDiscount) {
            return true;
        }
        return false;
    }

    public int getCode() {
        return product.getId();
    }

    public int getMinQuantity() {
        return this.minQuantity;
    }

    public void setMinQuantity(int newMinQuantity) {
        this.minQuantity = newMinQuantity;
    }

    public Discount getCurrentMaxDiscount() throws SQLException {
        if (discountsHistory.isEmpty()) {
            loadDiscountHistory();
        }
        Discount maxDiscount = null;
        double currentPrice = price;
        for (Discount dis : discountsHistory) {
            if (dis.getStart_date().isBefore(LocalDate.now()) && dis.getEnd_date().isAfter(LocalDate.now())) {
                if (dis instanceof DiscountPercentage) {
                    double p = price * (1 - (((DiscountPercentage) dis).getDiscountValue()/100));
                    if (currentPrice > p) {
                        maxDiscount = dis;
                        currentPrice = p;
                    }
                } else if (dis instanceof DiscountFixed) {
                    double p = Math.max(0, price - ((DiscountFixed) dis).getDiscountValue());
                    if (currentPrice > p) {
                        maxDiscount = dis;
                        currentPrice = p;
                    }
                }
            }
        }
        return maxDiscount;
    }

    private void loadDiscountHistory() throws SQLException {
        List<DiscountDTO> allProductDiscount = DiscountDAO.getInstance().selectAllById(product.getId());

        if (allProductDiscount != null) {
            for (DiscountDTO discountDTO : allProductDiscount) {
                String type = discountDTO.getdType();
                if (type == "Fixed") {
                    DiscountFixed discount1 = new DiscountFixed(discountDTO);
                    discountsHistory.add(discount1);
                } else {
                    DiscountPercentage discount2 = new DiscountPercentage(discountDTO);
                    discountsHistory.add(discount2);
                }

            }
        }
    }

    public List<SpecificProduct> getAllExpired() throws SQLException {
        if (allSpecificProducts.isEmpty()) {
            loadSpecificByProductId(product.getId());
        }
        List<SpecificProduct> allExpired = new ArrayList<>();
        for (SpecificProduct sp : allSpecificProducts.values()) {
            if (sp.getIsExpired()) {
                if (sp.getStatus() != ProductStatus.status.SOLD) {
                    allExpired.add(sp);
                    if (sp.getStatus() == ProductStatus.status.ON_STORAGE
                            || sp.getStatus() == ProductStatus.status.ON_SHELF) {
                        sp.setStatus(ProductStatus.status.EXPIRED);
                        totalAmount--;
                    }
                }
            }
        }
        return allExpired;
    }

    public List<SpecificProduct> getAllFlaws() throws SQLException {
        if (allSpecificProducts.isEmpty()) {
            loadSpecificByProductId(getCode());
        }
        List<SpecificProduct> allFlaws = new ArrayList<>();
        for (SpecificProduct sp : allSpecificProducts.values()) {
            if (sp.getStatus() == ProductStatus.status.IS_FLAW) {
                allFlaws.add(sp);
            }
        }
        return allFlaws;
    }

    public List<SpecificProduct> receiveSupply(int amount, double buyPrice, LocalDate expiredDate, int branchId)
            throws SQLException {
        List<SpecificProduct> addedSpecific = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            SpecificProductDTO specificProductDTO = new SpecificProductDTO(Global.getNewSpecificId(), getCode(),
                    branchId, buyPrice, -1, ProductStatus.status.ON_STORAGE, "", expiredDate, LocalDate.now());
            SpecificProduct newSpecific = new SpecificProduct(specificProductDTO);
            addedSpecific.add(newSpecific);
            allSpecificProducts.put(newSpecific.getSpecificId(), newSpecific);
            totalAmount++;
        }
        return addedSpecific;
    }

    public void transferToShop(int amount) throws SQLException {
        List<SpecificProduct> onStorageProducts = getOnStorageProducts();
        if (amount > onStorageProducts.size())
            amount = onStorageProducts.size();
        for (int i = 0; i < amount; i++) {
            onStorageProducts.get(i).setStatus(ProductStatus.status.ON_SHELF);
        }
    }

    private List<SpecificProduct> getOnStorageProducts() throws SQLException {
        if (allSpecificProducts.size() == 0) {
            loadSpecificByProductId(getCode());
        }
        List<SpecificProduct> onStorageProducts = new ArrayList<>();
        for (SpecificProduct sp : allSpecificProducts.values()) {
            if (sp.getStatus() == ProductStatus.status.ON_STORAGE)
                onStorageProducts.add(sp);
        }
        return onStorageProducts;
    }

    private List<SpecificProduct> getOnSelfProducts() {
        List<SpecificProduct> OnSelfProducts = new ArrayList<>();
        for (SpecificProduct sp : allSpecificProducts.values()) {
            if (sp.getStatus() == ProductStatus.status.ON_SHELF)
                OnSelfProducts.add(sp);
        }
        return OnSelfProducts;
    }

    private SpecificProduct getSpecificProduct(int specificId) {
        return allSpecificProducts.get(specificId);
    }

    /**
     * sell a product and update the ServiceLayer.PersentationLayer.inventory
     * quantity
     *
     * @param specificId
     */
    public SpecificProduct sellProduct(int specificId) throws Exception {
        SpecificProduct sp = getSpecificProduct(specificId);
        if (sp != null) {
            if (sp.getStatus() == ProductStatus.status.ON_SHELF || sp.getStatus() == ProductStatus.status.ON_STORAGE) {
                sp.setStatus(ProductStatus.status.SOLD);
                totalAmount--;
                // updating sellPrice
                UpdateSellPrice(sp);
                System.out.println(getPrice());
            } else
                throw new Exception("cannot sell products not from shelf");
        }
        return sp;
    }

    private void UpdateSellPrice(SpecificProduct sp) throws SQLException {
        double sellPrice = getPrice();
        sp.setSellPrice(sellPrice);
    }

    public boolean checkForDeficiency() {
        if (totalAmount < minQuantity) {
            return true;
        }
        return false;
    }

    public void reportFlawProduct(int specificId, String description) throws Exception {
        SpecificProduct specificProduct = allSpecificProducts.get(specificId);
        if (specificProduct == null)
            throw new Exception("this specific product doesn't exist");
        ProductStatus.status status = specificProduct.getStatus();
        if (specificProduct.getStatus() != ProductStatus.status.SOLD)
            specificProduct.setStatus(ProductStatus.status.IS_FLAW);
        specificProduct.setFlawDescription(description);
        // change total amount only if the product was for sale
        if (status == ProductStatus.status.ON_SHELF || status == ProductStatus.status.ON_STORAGE) {
            totalAmount--;
        }
    }

    public List<SpecificProduct> getOnShelfProduct() {
        List<SpecificProduct> result = new ArrayList<>();
        for (SpecificProduct specificProduct : allSpecificProducts.values()) {
            if (specificProduct.getStatus() == ProductStatus.status.ON_SHELF) {
                result.add(specificProduct);
            }
        }
        return result;
    }

    public int getExpiredAmount() {
        int result = 0;
        for (SpecificProduct specificProduct : allSpecificProducts.values()) {
            if (specificProduct.getIsExpired())
                result++;
        }
        return result;
    }

    public String getManufacturer() {
        return product.getManufacturer();
    }

    public Category getCategory() throws Exception {
        return product.getCategory();
    }

    public String getCategoryName() throws Exception {
        return product.getCategoryName();
    }

    public int getCategoryId() {
        return product.getCategoryId();
    }

    public boolean existInCategories(List<Category> allSubCategories) throws SQLException, InventoryException {
        boolean result = false;
        for (Category category : allSubCategories) {
            if (product.getCategory().getId() == category.getId()) {
                result = true;
                break;
            }
        }

        return result;
    }

    public void loadSpecificByProductId(int id) throws SQLException {
        if (this.allSpecificProducts.size() == 0) {
            List<SpecificProductDTO> all = SpecificProductDAO.getInstance().selectAllById(id);
            if (all != null) {
                for (SpecificProductDTO specificProductDTO : all) {
                    SpecificProduct specificProduct = new SpecificProduct(specificProductDTO);
                    allSpecificProducts.put(specificProduct.getSpecificId(), specificProduct);
                }
            }
        }
    }
}
