package Service_Layer;

import BusinessLayer.Inventory.BranchController;
import BusinessLayer.Inventory.Category;
import BusinessLayer.Inventory.CategoryController;
import BusinessLayer.Inventory.ProductBranch;
import BusinessLayer.InveontorySuppliers.Branch;
import BusinessLayer.InveontorySuppliers.Discount;
import BusinessLayer.InveontorySuppliers.ProductController;


import java.util.List;

public class ProductService {

    ProductController productController;
    CategoryController categoryController;
    BranchController branchController;

    public ProductService() throws Exception {
        this.productController = ProductController.getInstance();
        this.categoryController = CategoryController.getInstance();
        this.branchController = BranchController.getInstance();
//        categoryController.addNewCategory("Tnuva");
//        Category tnuva = categoryController.getCategoryById(0);
//        categoryController.addNewCategory("MIlks drinks",tnuva);
//        Category milkDrinks =categoryController.getCategoryById(1);
//        Product p1 = new Product("Milk",1,"Tnuva",milkDrinks);
//        Product p2 = new Product("Koteg",2,"Tnuva",tnuva);
//        productController.addProduct(p1);
//        productController.addProduct(p2);
//        branchController.addBranch(1,"brnach1");
//        Branch branch = branchController.getBranchById(1);
//        branch.addNewProductBranch(p1,20,50,48);
//        branch.addNewProductBranch(p2,20,50,48);
//        branch.receiveSupply(1);
//        branch.receiveSupply(2);
//        branch.reportFlawProduct(1,1,"FlowProduct!!!");
    }




//        public void addNewProduct(String name, int code, double price, String manufacturer, int min_quantity,int total_quantity){
//        productController.addNewGeneralProduct(name,code,price,manufacturer,min_quantity,total_quantity);
//    }
//    public void addNewProduct(String name, int code, double price, String manufacturer, int min_quantity,int total_quantity, int categoryId, String categoryName, int parentCategory){
//        productController.addNewGeneralProduct(name,code,price,manufacturer,min_quantity,total_quantity,categoryId,categoryName,parentCategory);
//    }

//    public void receiveSupply(int code,String name, double price, int amount, LocalDate expiredDate, String manufacturer){
//        productController.receiveSupply( code, name,  price,  amount,  expiredDate,  manufacturer);
//
//    }


//        public void receiveSupply(int code,String name,double price,int amount, String expiredDate,String manufacturer){
//        GeneralProduct gp = productController.getGeneralProductByCode(code);
//        if (gp != null) {
//           productController.receiveExistSupply(code,price,amount,LocalDate.parse(expiredDate));
//        }
//        else{
//            Scanner scanner = new Scanner(System.in);
//
//        }
//        productController.receiveSupply(code,name,price,amount,LocalDate.parse(expiredDate),manufacturer);
//
//        System.out.println("Supply added successfully");
//    }

    public void addNewCategory(String categoryName, int parentId) throws Exception {
        if(parentId != -1){
            Category parentCategory = categoryController.getCategoryById(parentId);
            if(parentCategory == null)
                throw new Exception("This Parent id doesn't exist");
            categoryController.addNewCategory(categoryName,parentCategory);
        }
        else {
            categoryController.addNewCategory(categoryName);
        }
        for(Category category : categoryController.getAllCategories()){
            System.out.println(category.getName());
        }
    }
    public void sellProduct(int ProductCode, int SpecificId,int branchId) throws Exception {
        branchController.sellProduct(branchId,ProductCode,SpecificId);
    }

    public void reportFlawProduct(int branchId,int productCode,int specifcId, String description) throws Exception {
        branchController.reportFlawProduct(branchId,productCode,specifcId,description);
    }

    public void setDiscountByProducts(int branchId, List<Integer> productsToDiscount, Discount discount) throws Exception {
        branchController.setDiscountOnProducts(branchId,productsToDiscount,discount);


    }
        public void setDiscountByCategories(int branchId,List<Integer> categories, Discount discount) throws Exception {
        branchController.setDiscountOnCategories(branchId,categories,discount);
//        Branch branch = branchController.getBranchById(branchId);
//        List<Category> allSubCategories = categoryController.getListAllSubCategoriesByIds(categories);
//        branch.setDiscountOnCategories(allSubCategories,discount);


    }









}
