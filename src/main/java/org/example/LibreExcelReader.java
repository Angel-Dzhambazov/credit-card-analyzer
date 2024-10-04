package org.example;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LibreExcelReader {
    public static List<List<String>> readExcel(String filePath) throws IOException {
        List<List<String>> data = new ArrayList<>();
        Workbook workbook = null;
        FileInputStream fis = new FileInputStream(filePath);

        // Determine if the file is XLS or XLSX based on extension
        if (filePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(fis); // for .xlsx files
        } else if (filePath.endsWith(".xls")) {
            workbook = new HSSFWorkbook(fis); // for .xls files
        } else {
            System.out.println("Invalid file type. Only .xls and .xlsx files are supported.");
            fis.close();
            return data;
        }

        // Read the first sheet
        Sheet sheet = workbook.getSheetAt(0);

        // Iterate over rows
        for (Row row : sheet) {
            List<String> rowData = new ArrayList<>();
            // Iterate over columns (cells)
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        rowData.add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            rowData.add(cell.getDateCellValue().toString());
                        } else {
                            rowData.add(String.valueOf(cell.getNumericCellValue()));
                        }
                        break;
                    case BOOLEAN:
                        rowData.add(String.valueOf(cell.getBooleanCellValue()));
                        break;
                    case FORMULA:
                        rowData.add(cell.getCellFormula());
                        break;
                    case BLANK:
                        rowData.add("");
                        break;
                    default:
                        rowData.add("");
                }
            }
            data.add(rowData);
        }

        workbook.close();
        fis.close();
        return data;
    }

    public static void main(String[] args) {
        String excelFilePath = "C:/Users/Svetlanka/Downloads/1y.xls";
        int currentRow = 0; // Track the current row index
        try {
            List<List<String>> excelData = readExcel(excelFilePath);
            // Print out the contents of the Excel file
            int skipRows = 9; // Number of rows to skip
            List<String> shopNames = Arrays.asList("lidl", "kaufland", "billa");
            List<String> stroyMarket = Arrays.asList("krez", "praktiker", "domics");
            List<String> dm = Arrays.asList("dm");
            double moneyInShops = 0.0;

            for (List<String> row : excelData) {
                if (currentRow >= skipRows) {
                    String cellValue = row.get(7).toLowerCase(); // Get the 6th element and convert to lowercase

                    // Check if the 6th element contains any of the shop names
                    for (String shop : dm) {
                        if (cellValue.contains(shop)) {
                            System.out.println(row);
                            moneyInShops += Double.parseDouble(row.get(3));
                            break; // Exit the loop once a match is found
                        }
                    }
//                    System.out.println(row.get(3));
                }
                currentRow++;
            }
            System.out.println(currentRow);
            System.out.println(moneyInShops);
            System.out.println(moneyInShops/12);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
