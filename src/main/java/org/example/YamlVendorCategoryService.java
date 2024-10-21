package org.example;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlVendorCategoryService implements VendorCategoryService {

    private String yamlFilePath;

    public YamlVendorCategoryService(String yamlFilePath) {
        this.yamlFilePath = yamlFilePath;
    }

    @Override
    public Map<String, List<String>> loadVendors() throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(yamlFilePath)) {
            if (in == null) {
                throw new IOException("Unable to find " + yamlFilePath);
            }

            // Load the YAML data and normalize vendor names to lowercase
            Map<String, List<String>> vendorsByCategory = yaml.load(in);

            // Create a new map with lowercase vendor names
            Map<String, List<String>> normalizedVendorsByCategory = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : vendorsByCategory.entrySet()) {
                // Convert all vendor names in each category to lowercase
                List<String> lowercaseVendors = entry.getValue().stream()
                        .map(String::toLowerCase)  // Convert to lowercase
                        .collect(Collectors.toList());

                normalizedVendorsByCategory.put(entry.getKey(), lowercaseVendors);
            }

            return normalizedVendorsByCategory;
        }
    }
}
