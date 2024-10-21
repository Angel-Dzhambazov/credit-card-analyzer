package org.example;

import java.io.IOException;
import java.util.List;

public interface ExcelReaderService {
    List<List<String>> readExcel(String filePath) throws IOException;
}
