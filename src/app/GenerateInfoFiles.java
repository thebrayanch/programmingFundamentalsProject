package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


 //Main class to generate pseudo-random input files for sellers, products, and sales.
// Project: Data generation and classification

public class GenerateInfoFiles {

    private static final String[] NAMES = {"Ana", "Luis", "Camila", "Sergio"};
    private static final String[] SURNAMES = {"barajas", "valenzuela", "rincon", "garcia"};
    private static final String[] DOCUMENT_TYPES = {"CC", "TI", "CE"};
    private static final String[] PRODUCT_NAMES = {
            "Laptop", "Phone", "Keyboard", "Mouse", "Monitor", "Headphones", "Speaker"
    };

    
   // Inner class to represent a seller with all its attributes.
    
    private static class Seller {
        String docType;
        long id;
        String name;
        String surname;

        Seller(String docType, long id, String name, String surname) {
            this.docType = docType;
            this.id = id;
            this.name = name;
            this.surname = surname;
        }
    }

    public static void main(String[] args) {
        try {
            int sellerCount = 10;
            int productCount = 15;
            int salesPerSeller = 30;

            // Create sales folder if it does not exist
            File salesFolder = new File("sales");
            if (!salesFolder.exists()) {
                salesFolder.mkdir();
            }

            // Clean old sales files
            for (File f : salesFolder.listFiles()) {
                f.delete();
            }

            // Generate sellers and products
            List<Seller> sellers = createSalesManInfoFile(sellerCount);
            createProductsFile(productCount);

            // Generate sales files for each seller
            for (Seller s : sellers) {
                createSalesMenFile(salesPerSeller, s, productCount, salesFolder);
            }

            System.out.println("Files generated successfully.");
        } catch (Exception e) {
            System.err.println("Error generating files: " + e.getMessage());
        }
    }

    
   //  Generates the seller information file with pseudo-random data.
   //  @param sellerCount Number of sellers to generate
   //  @return A list of sellers generated
   //  @throws IOException If file writing fails
    
    public static List<Seller> createSalesManInfoFile(int sellerCount) throws IOException {
        Random random = new Random();
        List<Seller> sellers = new ArrayList<>();

        try (FileWriter writer = new FileWriter("sellers.txt")) {
            for (int i = 0; i < sellerCount; i++) {
                String type = DOCUMENT_TYPES[random.nextInt(DOCUMENT_TYPES.length)];
                long id = 10000000L + i;
                String name = NAMES[random.nextInt(NAMES.length)];
                String surname = SURNAMES[random.nextInt(SURNAMES.length)];

                Seller s = new Seller(type, id, name, surname);
                sellers.add(s);

                writer.write(type + ";" + id + ";" + name + ";" + surname + "\n");
            }
        }
        return sellers;
    }

    
    // Generates the product information file with pseudo-random data.
    // @param productsCount Number of products to generate
    // @throws IOException If file writing fails
     
    public static void createProductsFile(int productsCount) throws IOException {
        Random random = new Random();
        try (FileWriter writer = new FileWriter("products.txt")) {
            for (int i = 0; i < productsCount; i++) {
                String productName = PRODUCT_NAMES[random.nextInt(PRODUCT_NAMES.length)] + i;
                int price = 1000 + random.nextInt(99000);
                writer.write("P" + i + ";" + productName + ";" + price + "\n");
            }
        }
    }

    
   // Generates the sales file for one seller with pseudo-random data.
   // @param salesCount Number of sales records for the seller
   // @param seller Seller data
   // @param productCount Number of products available
   // @param salesFolder Folder where sales files will be saved
   // @throws IOException If file writing fails
    
    public static void createSalesMenFile(int salesCount, Seller seller, int productCount, File salesFolder) throws IOException {
        Random random = new Random();
        String filename = salesFolder.getPath() + "/sales_" + seller.id + ".txt";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(seller.docType + ";" + seller.id + "\n");
            for (int i = 0; i < salesCount; i++) {
                int productId = random.nextInt(productCount);
                int quantity = 1 + random.nextInt(20);
                writer.write("P" + productId + ";" + quantity + ";\n");
            }
        }
    }
}
