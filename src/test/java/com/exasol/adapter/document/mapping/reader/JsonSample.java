package com.exasol.adapter.document.mapping.reader;

import java.util.*;

import com.exasol.adapter.document.properties.EdmlInput;

public class JsonSample {

    static final String TOPICS_JSON = lines( //
            "      'topics': {", //
            "        'toJsonMapping': {", //
            "          'description': 'Maps the sub document of this property to a JSON string',", //
            "          'varcharColumnSize': 200", //
            "        }", //
            "      }");

    static final String TOPICS_TABLE = lines( //
            "      'topics': {", //
            "        'toTableMapping': {", //
            "          'mapping': {", //
            "            'toVarcharMapping': {", //
            "              'destinationName': 'NAME'", //
            "            }", //
            "          }", //
            "        }", //
            "      }");

    public static final String[] ADDITIONAL_FIELDS = { //
            lines( //
                    "      'publisher': {", //
                    "        'toVarcharMapping': {", //
                    "          'varcharColumnSize': 100,", //
                    "          'description': 'The name is mapped to a string with max length of 100',", //
                    "          'overflowBehaviour': 'TRUNCATE'", //
                    "        }", //
                    "      }"), //
            lines( //
                    "      'price': {", //
                    "        'toDecimalMapping': {", //
                    "          'decimalPrecision': 8,", //
                    "          'decimalScale': 2", //
                    "        }", //
                    "      }"),
            lines( //
                    "      'author': {", //
                    "        'fields': {", //
                    "          'name': {", //
                    "            'toVarcharMapping': {", //
                    "              'varcharColumnSize': 20,", //
                    "              'destinationName': 'AUTHOR_NAME',", //
                    "              'description': 'Maps the nested property authors.name to column AUTHOR_NAME'", //
                    "            }", //
                    "          }", //
                    "        }", //
                    "      }") };

    static final String DOUBLE_NESTED_TO_TABLE_MAPPING = lines( //
            "      'chapters': {", //
            "        'toTableMapping': {", //
            "          'mapping': {", //
            "            'fields': {", //
            "              'name': {", //
            "                'toVarcharMapping': {", //
            "                }", //
            "              },", //
            "              'figures': {", //
            "                'toTableMapping': {", //
            "                  'mapping': {", //
            "                    'fields': {", //
            "                      'name': {", //
            "                        'toVarcharMapping': {", //
            "                          'destinationName': 'NAME'", //
            "                        }", //
            "                      }", //
            "                    }", //
            "                  }", //
            "                }", //
            "              }", //
            "            }", //
            "          }", //
            "        }", //
            "      }");

    public static JsonSample builder() {
        return new JsonSample();
    }

    public JsonSample basic() {
        return isbn("global").name("");
    }

    private String addSourceReferenceColumn;
    private String source = "MY_BOOKS";
    private String destinationTable = "BOOKS";
    private final List<String> fields = new ArrayList<>();

    public JsonSample() {
        addSourceReferenceColumn("  'addSourceReferenceColumn': true,");
    }

    /**
     * @param key: "", "global" or "local"
     * @return
     */
    public JsonSample isbn(final String key) {
        return withFields(lines( //
                "      'isbn': {", //
                "        'toVarcharMapping': {", //
                "          'varcharColumnSize': 20,", //
                "          'description': 'The isbn is mapped to a string with max length of 20',", //
                "          'overflowBehaviour': 'ABORT',", //
                "          'required': true", //
                keyString(key), //
                "        }", //
                "      }"));
    }

    public JsonSample name(final String key) {
        return withFields(lines( //
                "      'name': {", //
                "        'toVarcharMapping': {", //
                "          'varcharColumnSize': 100,", //
                "          'description': 'The name is mapped to a string with max length of 100',", //
                "          'overflowBehaviour': 'TRUNCATE'", //
                keyString(key), //
                "        }", //
                "      }"));
    }

    private String keyString(final String key) {
        return key.isEmpty() ? "" : String.format(",\n          'key': '%s'", key);
    }

    public JsonSample addSourceReferenceColumn(final String addSourceReferenceColumn) {
        this.addSourceReferenceColumn = addSourceReferenceColumn;
        return this;
    }

    public JsonSample source(final String source) {
        this.source = source;
        return this;
    }

    public JsonSample destinationTable(final String destinationTable) {
        this.destinationTable = destinationTable;
        return this;
    }

    public JsonSample withFields(final String... strings) {
        this.fields.addAll(Arrays.asList(strings));
        return this;
    }

    public String build() {
        return new StringBuilder() //
                .append(lines("{", //
                        "  '$schema': 'https://schemas.exasol.com/edml-1.3.0.json',", //
                        "  'source': '" + this.source + "',", //
                        "  'destinationTable': '" + this.destinationTable + "',", //
                        "  'description': 'Maps " + this.source + " to " + this.destinationTable + "',", //
                        this.addSourceReferenceColumn, //
                        "  'mapping': {", //
                        "    'fields': {", //
                        "")) //
                .append(String.join(",\n", this.fields)) //
                .append(lines( //
                        "", //
                        "    }", //
                        "  }", //
                        "}")) //
                .toString();
    }

    public EdmlInput buildEdmlInput(final String source) {
        return new EdmlInput(this.build(), source);
    }

    static String lines(final String... strings) {
        return String.join("\n", strings).replace('\'', '"');
    }
}
