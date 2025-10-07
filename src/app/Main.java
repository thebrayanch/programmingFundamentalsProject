package app;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;


// Main class for processing sales data and generating reports.

 //This class reads input files with information about salesmen, products,
 //and sales transactions. Then, it generates two CSV reports:
 //1. Sales report by salesman (sorted descending by total revenue).
 //2. Sales report by product (sorted descending by total quantity).

 //Author: Daniel Rincon
 
public class Main {

    // Maps to store info
    private static Map<String, String> salesmenMap = new HashMap<>(); // id -> full name
    private static Map<String, Product> productsMap = new HashMap<>(); // id -> product
    private static Map<String, Double> salesmanRevenue = new HashMap<>();
    private static Map<String, Integer> productQuantities = new HashMap<>();

    public static void main(String[] args) {
        try {
            loadSalesmenInfo("sellers.txt");
            loadProductsInfo("products.txt");
            processSalesFiles("sales"); // Folder containing all sales files
            generateSalesReport("sales_report.csv");
            generateProductsReport("products_report.csv");
            System.out.println("Reports generated successfully.");
        } catch (Exception e) {
            System.err.println("Error while processing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    // Load salesmen information from file.
    // Format: DocumentType;DocumentNumber;FirstName;LastName
     
    private static void loadSalesmenInfo(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            String[] parts = line.split(";");
            if (parts.length >= 4) {
                String id = (parts[0].trim() + parts[1].trim()).toUpperCase(); // normalize key
                String name = parts[2].trim() + " " + parts[3].trim();
                salesmenMap.put(id, name);
               

            }
        }
    }

    
    //Load products information from file.
    //Format: ProductID;ProductName;Price
     
    private static void loadProductsInfo(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            String[] parts = line.split(";");
            if (parts.length >= 3) {
                String id = parts[0].trim();
                String name = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                productsMap.put(id, new Product(id, name, price));
            }
        }
    }

    
    //Process all sales files in a given folder.
    
    private static void processSalesFiles(String folderName) throws IOException {
        File folder = new File(folderName);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IOException("Sales folder not found: " + folderName);
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            processSalesFile(file);
        }
    }

    
   // Process a single sales file.
   // Format:
   // DocumentType;DocumentNumber
   // ProductID;Quantity
   // ProductID;Quantity
         private static void processSalesFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        if (lines.isEmpty()) return;

        String[] header = lines.get(0).split(";");
        if (header.length < 2) return;


        String salesmanId = (header[0].trim() + header[1].trim()).toUpperCase();
      


        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(";");
            if (parts.length >= 2) {
                String productId = parts[0].trim();
                int quantity = Integer.parseInt(parts[1].trim());

                if (productsMap.containsKey(productId)) {
                    Product p = productsMap.get(productId);
                    double total = p.getPrice() * quantity;

                    salesmanRevenue.put(salesmanId,
                            salesmanRevenue.getOrDefault(salesmanId, 0.0) + total);

                    productQuantities.put(productId,
                            productQuantities.getOrDefault(productId, 0) + quantity);
                }
            }
        }
    }

   
   //Generate sales report by salesman.
    private static void generateSalesReport(String filename) throws IOException {
        List<Map.Entry<String, Double>> sorted = salesmanRevenue.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Double> entry : sorted) {
                String name = salesmenMap.getOrDefault(entry.getKey(), "Unknown");
                writer.write(name + ";" + String.format("%.2f", entry.getValue()));
                writer.newLine();
            }
        }
    }

    
     //* Generate products report by quantity sold.
     
    private static void generateProductsReport(String filename) throws IOException {
        List<Map.Entry<String, Integer>> sorted = productQuantities.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Integer> entry : sorted) {
                Product p = productsMap.get(entry.getKey());
                if (p != null) {
                    writer.write(p.getId() + ";" + p.getName() + ";" +
                            String.format("%.2f", p.getPrice()) + ";" + entry.getValue());
                    writer.newLine();
                }
            }
        }
    }

    
    // Product class (inner class for simplicity).
    
    private static class Product {
        private String id;
        private String name;
        private double price;

        public Product(String id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
    }
}