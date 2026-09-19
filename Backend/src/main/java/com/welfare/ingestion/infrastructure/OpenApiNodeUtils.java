package com.welfare.ingestion.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class OpenApiNodeUtils {

    private OpenApiNodeUtils() {
    }

    public static String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value
                .replace("&amp;", "&")
                .replace("&#13;", "\n")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .trim();
        if (normalized.isEmpty() || "-".equals(normalized) || "null".equalsIgnoreCase(normalized)) {
            return null;
        }
        return normalized;
    }

    public static String text(JsonNode node, String... keys) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        for (String key : keys) {
            JsonNode child = child(node, key);
            String value = nodeToString(child);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static Integer integerOrNull(JsonNode node, String... keys) {
        String value;
        if (keys == null || keys.length == 0) {
            value = nodeToString(node);
        } else {
            value = text(node, keys);
        }
        return parseInteger(value);
    }

    public static Integer integerOrZero(JsonNode node, String... keys) {
        Integer value = integerOrNull(node, keys);
        return value == null ? 0 : value;
    }

    public static Integer parseInteger(String value) {
        if (value == null) {
            return null;
        }
        String digits = value.replaceAll("[^0-9-]", "");
        if (digits.isEmpty() || "-".equals(digits)) {
            return null;
        }
        try {
            return Integer.valueOf(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String yn(JsonNode node, String... keys) {
        String value = text(node, keys);
        if (value == null) {
            return null;
        }
        String upper = value.toUpperCase(Locale.ROOT);
        if (upper.startsWith("Y")) {
            return "Y";
        }
        if (upper.startsWith("N")) {
            return "N";
        }
        return null;
    }

    public static JsonNode child(JsonNode node, String key) {
        if (node == null || key == null) {
            return null;
        }
        JsonNode direct = node.get(key);
        if (direct != null && !direct.isMissingNode()) {
            return direct;
        }
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static JsonNode findFirst(JsonNode node, String... keys) {
        if (node == null) {
            return null;
        }
        for (String key : keys) {
            JsonNode found = findRecursive(node, key);
            if (found != null && !found.isMissingNode() && !found.isNull()) {
                return found;
            }
        }
        return null;
    }

    public static List<JsonNode> asList(JsonNode node) {
        List<JsonNode> result = new ArrayList<>();
        if (node == null || node.isMissingNode() || node.isNull()) {
            return result;
        }
        if (node.isArray()) {
            node.forEach(result::add);
            return result;
        }
        result.add(node);
        return result;
    }

    public static List<String> splitValues(String raw) {
        List<String> values = new ArrayList<>();
        String normalized = blankToNull(raw);
        if (normalized == null) {
            return values;
        }
        for (String token : normalized.split("[,|/]|\\^")) {
            String value = blankToNull(token);
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    public static String nodeToString(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        if (node.isValueNode()) {
            return blankToNull(node.asText());
        }
        if (node.isObject()) {
            JsonNode textNode = node.get("");
            if (textNode != null && textNode.isValueNode()) {
                return blankToNull(textNode.asText());
            }
        }
        return null;
    }

    private static JsonNode findRecursive(JsonNode node, String key) {
        JsonNode direct = child(node, key);
        if (direct != null) {
            return direct;
        }
        if (node.isObject()) {
            Iterator<JsonNode> elements = node.elements();
            while (elements.hasNext()) {
                JsonNode found = findRecursive(elements.next(), key);
                if (found != null) {
                    return found;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                JsonNode found = findRecursive(child, key);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
