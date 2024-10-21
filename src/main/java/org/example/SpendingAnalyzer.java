package org.example;

import java.io.IOException;
import java.util.*;

public class SpendingAnalyzer {

    private final ExcelReaderService excelReaderService;
    private final VendorCategoryService vendorCategoryService;

    public SpendingAnalyzer(ExcelReaderService excelReaderService, VendorCategoryService vendorCategoryService) {
        this.excelReaderService = excelReaderService;
        this.vendorCategoryService = vendorCategoryService;
    }

    public void analyzeAndPrintReport(String excelFilePath) throws IOException {
        List<List<String>> excelData = excelReaderService.readExcel(excelFilePath);
        Map<String, List<String>> vendorCategories = vendorCategoryService.loadVendors();

        // Maps for known vendor spendings and unknown vendors
        Map<String, Map<String, Double>> categorizedSpendings = new HashMap<>();
        Map<String, Double> unknownVendorSpendings = new HashMap<>();
        double totalKnownSpendings = 0.0;
        double totalUnknownSpendings = 0.0;

        int skipRows = 11;

        for (int currentRow = skipRows; currentRow < excelData.size(); currentRow++) {
            List<String> row = excelData.get(currentRow);
            String vendorName = "";
            double amountSpent = 0.0;
            try {
                vendorName = row.get(7).toLowerCase(); // Assume the 8th column is the vendor
                amountSpent = Double.parseDouble(row.get(3)); // Assume the 4th column is the amount
            } catch (Exception e) {
                // If an error occurs, print the problematic row and continue processing
//                System.err.println("Error processing row: " + row);
//                e.printStackTrace();
            }

            boolean knownVendor = false;

            // Iterate over each category and check if the vendor belongs to it
            for (Map.Entry<String, List<String>> entry : vendorCategories.entrySet()) {
                String category = entry.getKey();
                List<String> vendors = entry.getValue();

                // Check if the vendor is in the current category
                for (String vendor : vendors) {
                    if (vendorName.contains(vendor.toLowerCase())) {
                        categorizedSpendings.putIfAbsent(category, new HashMap<>());
                        Map<String, Double> categorySpendings = categorizedSpendings.get(category);
                        categorySpendings.put(vendor, categorySpendings.getOrDefault(vendor, 0.0) + amountSpent);
                        totalKnownSpendings += amountSpent;
                        knownVendor = true;
                        break;
                    }
                }
                if (knownVendor) break;
            }

            // If the vendor wasn't found in any category, mark it as unknown
            if (!knownVendor) {
                unknownVendorSpendings.put(vendorName, unknownVendorSpendings.getOrDefault(vendorName, 0.0) + amountSpent);
                totalUnknownSpendings += amountSpent;
            }
        }

        // Print the detailed report
        printDetailedReport(categorizedSpendings, totalKnownSpendings, unknownVendorSpendings, totalUnknownSpendings);
    }

    private void printDetailedReport(Map<String, Map<String, Double>> categorizedSpendings, double totalKnownSpendings,
                                     Map<String, Double> unknownVendorSpendings, double totalUnknownSpendings) {

        /* working code for known vendors
        // Print categorized spendings
        for (Map.Entry<String, Map<String, Double>> categoryEntry : categorizedSpendings.entrySet()) {
            String category = categoryEntry.getKey();
            Map<String, Double> vendors = categoryEntry.getValue();

            System.out.println("Total spent in " + category + ": " + vendors.values().stream().mapToDouble(Double::doubleValue).sum());
            System.out.println("Total spent in " + category + " per month: " + vendors.values().stream().mapToDouble(Double::doubleValue).sum()/55);
            for (Map.Entry<String, Double> vendorEntry : vendors.entrySet()) {
                System.out.println("  - " + vendorEntry.getKey() + ": " + vendorEntry.getValue());
            }
        }
         end of working code for known vendors
         */

        // Print categorized spendings
        categorizedSpendings.entrySet().stream()
                // Sort categories by the total spending in descending order
                .sorted((entry1, entry2) -> Double.compare(
                        entry2.getValue().values().stream().mapToDouble(Double::doubleValue).sum(),
                        entry1.getValue().values().stream().mapToDouble(Double::doubleValue).sum()))
                .forEach(categoryEntry -> {
                    String category = categoryEntry.getKey();
                    Map<String, Double> vendors = categoryEntry.getValue();

                    // Print total spent for the category
                    //new line for commit
                    double totalCategorySpend = vendors.values().stream().mapToDouble(Double::doubleValue).sum();
                    System.out.println("Total spent in " + category + ": " + totalCategorySpend);
                    System.out.println("Total spent in " + category + " per month: " + totalCategorySpend / 55);

                    // Sort vendors by the amount spent in descending order and print them
                    vendors.entrySet().stream()
                            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())  // Sort by value in descending order
                            .forEach(vendorEntry -> System.out.println("  - " + vendorEntry.getKey() + ": " + vendorEntry.getValue()));
                });


        // Print overall known spendings
        System.out.println("Overall total spent from known vendors: " + totalKnownSpendings);

        // Print unknown vendor spendings
        if (!unknownVendorSpendings.isEmpty()) {
            System.out.println("\nSpendings from unknown vendors: " + totalUnknownSpendings);

            // Sort the unknown vendors by the amount spent in descending order
            unknownVendorSpendings.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())  // Sort by value in descending order
                    .forEach(unknownEntry -> System.out.println("  - " + unknownEntry.getKey() + ": " + unknownEntry.getValue()));
        }

    }
}
