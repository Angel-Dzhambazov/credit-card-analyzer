package org.example;

import java.util.List;
import java.util.Map;

public class TransactionAnalyzer {

    private VendorCategoryService vendorCategoryService;

    public TransactionAnalyzer(VendorCategoryService vendorCategoryService) {
        this.vendorCategoryService = vendorCategoryService;
    }

    public double analyzeTransactions(List<List<String>> transactions, int skipRows) throws Exception {
        Map<String, List<String>> vendors = vendorCategoryService.loadVendors();
        int currentRow = 0;
        double totalSpent = 0.0;

        for (List<String> row : transactions) {
            if (currentRow >= skipRows) {
                String cellValue = row.get(7).toLowerCase(); // Get the 6th element and convert to lowercase
                for (Map.Entry<String, List<String>> category : vendors.entrySet()) {
                    for (String vendor : category.getValue()) {
                        if (cellValue.contains(vendor)) {
                            totalSpent += Double.parseDouble(row.get(3));
                            break;
                        }
                    }
                }
            }
            currentRow++;
        }
        return totalSpent;
    }
}
