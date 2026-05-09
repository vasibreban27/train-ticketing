package com.vasilebreban.trainticketing;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class TestJsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TestJsonUtils() {
    }

    public static Long extractLong(String json, String fieldName) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(json);
            return root.get(fieldName).asLong();
        } catch (Exception exception) {
            throw new RuntimeException("Could not extract field from JSON: " + fieldName, exception);
        }
    }
}