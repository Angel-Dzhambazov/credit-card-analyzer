package org.example;

import java.io.IOException;
import java.util.List;

public class MainApplication {

    public static void main(String[] args) {
//        String excelFilePath = "C:/Users/AngelDzhambazov/Downloads/bbbe35f8-b063-4899-be7a-708fac1e8f7b.xls";
        String excelFilePath = "C:/Users/AngelDzhambazov/Downloads/all spendings my fibnk.xls";
        String yamlFilePath = "vendors.yaml";
        int skipRows = 9;

        try {
            // Initialize services
            VendorCategoryService vendorService = new YamlVendorCategoryService(yamlFilePath);
            ExcelReaderService excelReaderService = new ApachePoiExcelReaderService();
            SpendingAnalyzer spendingAnalyzer = new SpendingAnalyzer(excelReaderService, vendorService);

            // Read the Excel file
            List<List<String>> excelData = excelReaderService.readExcel(excelFilePath);

            // Analyze transactions and print results
            spendingAnalyzer.analyzeAndPrintReport(excelFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
