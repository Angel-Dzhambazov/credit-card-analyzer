package org.example;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApachePoiExcelReaderService implements ExcelReaderService {

    @Override
    public List<List<String>> readExcel(String filePath) throws IOException {
        List<List<String>> data = new ArrayList<>();
        Workbook workbook = null;
        FileInputStream fis = new FileInputStream(filePath);

        // Handle .xlsx and .xls formats
        if (filePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(fis);  // for .xlsx files
        } else if (filePath.endsWith(".xls")) {
            workbook = new HSSFWorkbook(fis);  // for .xls files
        } else {
            throw new IOException("Unsupported file type. Only .xls and .xlsx are supported.");
        }

        // Read the first sheet
        Sheet sheet = workbook.getSheetAt(0);

        // Iterate over rows and cells, similar to the LibreOffice logic
        for (Row row : sheet) {
            List<String> rowData = new ArrayList<>();
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        rowData.add(cell.getStringCellValue().toLowerCase());
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
}
