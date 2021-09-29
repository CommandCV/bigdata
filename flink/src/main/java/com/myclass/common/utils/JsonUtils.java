package com.myclass.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myclass.common.entry.Student;

import java.io.IOException;
import java.util.Objects;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode readJson(String json) throws IOException {
        return objectMapper.readTree(json);
    }

    public static <T> T parseJson(String json, Class<T> valueType) throws IOException {
        return objectMapper.readValue(json, valueType);
    }

    public static String getValueFromJsonNode(JsonNode jsonNode, String fieldName) {
        return getValueFromJsonNode(jsonNode, fieldName, null);
    }

    public static String getValueFromJsonNode(JsonNode jsonNode, String fieldName, String defaultValue) {
        JsonNode node = jsonNode.get(fieldName);
        if (Objects.nonNull(node)) {
            return node.asText();
        } else {
            return defaultValue;
        }
    }

    public static void main(String[] args) throws IOException {
        String json1 = "{\"id\": 1, \"name\": \"boy\", \"gender\": \"male\",\"age\": 18,\"address\": \"a1\"}";
        String json2 = "{\"id\": 2, \"name\": \"girl\", \"gender\": \"female\",\"age\": 18,\"address\": \"b1\"}";
        System.out.println(parseJson(json1, Student.class));
        System.out.println(parseJson(json2, Student.class));

        System.out.println(getValueFromJsonNode(readJson(json1), "id"));
        System.out.println(getValueFromJsonNode(readJson(json1), "name"));
        System.out.println(getValueFromJsonNode(readJson(json1), "name11"));
    }


}
