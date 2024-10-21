package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface VendorCategoryService {
    Map<String, List<String>> loadVendors() throws IOException;
}
