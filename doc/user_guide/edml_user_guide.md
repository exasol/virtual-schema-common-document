# Getting Started With the Exasol Document Mapping Language (EDML)

For creating a Virtual Schema for document data you have to define a mapping from the document structure to a relational structure. This is done using the Exasol Document Mapping Language (EDML)
([reference](https://schemas.exasol.com/#exasol-document-mapping-language-edml)).

Usually you write these definitions by hand. An exception are parquet input files for which you can use the [Exasol Parquet EDML generator](https://github.com/exasol/parquet-edml-generator/) to create an initial version that you then can customize further.

We recommend using an editor with JSON-Schema support for creating the files. This makes it easier to write the definition.

You can then use this mapping definition when creating the virtual schema. For that you upload the mapping definition as a file to BucketFS. Afterwards you set the property `MAPPING` in the `CREATE VIRTUAL SCHEMA` command to the path of the mapping definition in BucketFS.

You can also upload multiple mapping definitions into one folder and point to this folder. The adapter will then pick up all definitions.

For testing and automated creation of Virtual Schemas it's also possible to inline the EDML definition into the `MAPPING` property. Our tip: Don't use this if you're manipulating the EDML definitions by hand. Instead, use a proper editor with JSON-Schema support and upload the files. Editing inlined files is just too confusing. To inline the definitions you simply provide the mapping definition instead of the BucketFS path:

```
MAPPING = '{ "$schema": ... }'
```

If you want to provide multiple mapping definitions inline you can use a JSON array:

```
MAPPING = '[{ "$schema": ... }, { "$schema": ... }]'
```

This guide explains how to define EDML mappings in general. For data source specifics, check the corresponding virtual schema. Different data sources use different data formats. In this guide we use JSON.

For mapping multiple document sets, you can create multiple files, upload them to a folder and BucketFS and reference this folder.

## General Configuration

This is an example for mapping a CSV file to an Exasol table:

```json
{
  "$schema": "https://schemas.exasol.com/edml-2.1.0.json",
  "source": "data/CsvWithHeaders.csv",
  "destinationTable": "BOOKS",
  "description": "Mapping for the BOOKS table"
}
```

The following sections explain the properties:

### Source

The `source` property describes the source of the data. Its syntax and meaning depends on the virtual schemas for different data sources. For example the S3 virtual schema expects the S3 path for the object. Check the corresponding user guide of the virtual schema for details:

* [AWS S3](https://github.com/exasol/s3-document-files-virtual-schema/blob/main/doc/user_guide/user_guide.md)
* [Azure BLOB storage](https://github.com/exasol/azure-blob-storage-document-files-virtual-schema/blob/main/doc/user_guide/user_guide.md)
* [Azure Data Lake Storage Gen2](https://github.com/exasol/azure-data-lake-storage-gen2-document-files-virtual-schema/blob/main/doc/user_guide/user_guide.md)
* [Google Cloud Storage](https://github.com/exasol/google-cloud-storage-document-files-virtual-schema/blob/main/doc/user_guide/user_guide.md)

### Destination Table

The `destinationTable` property of a mapping defines the name of the virtual table to which the data is mapped. Please note that its value must be unique for all mapping entries. Creating a virtual schema with duplicate values for `destinationTable` will fail.

#### Mapping Multiple Files to a Single Destination Table

If you want to map multiple files with the same schema to the same table, please specify all files in the `source` property. See the following Virtual Schema specific user guides for details:

* [AWS S3](https://github.com/exasol/s3-document-files-virtual-schema/blob/main/doc/user_guide/user_guide.md#mapping-multiple-files)
* [Azure BLOB storage](https://github.com/exasol/azure-blob-storage-document-files-virtual-schema/blob/main/doc/user_guide/user_guide.md#mapping-multiple-files)
* [Azure Data Lake Storage Gen2](https://github.com/exasol/azure-data-lake-storage-gen2-document-files-virtual-schema/blob/main/doc/user_guide/user_guide.md#mapping-multiple-files)
* [Google Cloud Storage](https://github.com/exasol/google-cloud-storage-document-files-virtual-schema/blob/main/doc/user_guide/user_guide.md#mapping-multiple-files)

### Source Reference Column

Some dialects support reading one table from multiple sources. For example the [files-virtual-schemas](https://github.com/exasol/virtual-schema-common-document-files) allow you to load each row from a different file. In that case you may want to add the filename as a column to the Exasol table. That allows you to query on it and by that only read the required files.

To do so, set `"addSourceReferenceColumn": true` in the root object of your EDML definition. The adapter will then automatically add a column named `SOURCE_REFERENCE` to the end of the table:

```json
{
  "$schema": "https://schemas.exasol.com/edml-2.1.0.json",
  "source": "data/*.csv",
  "destinationTable": "BOOKS",
  "addSourceReferenceColumn": true
}
```

You can use this property for all dialects. Typically, it will, however, only give you additional information, if you load data from multiple sources.

The `SOURCE_REFERENCE` column has a maximum size of 2000 characters. The adapter will throw an exception when a source reference exceeds this.

## Define Mapping of Fields to Table Columns

EDML allows you to specify custom mapping from fields in source files to Exasol table columns. Virtual schema support two options for the mapping:
* [Automatic Mapping Inference](#automatic-mapping-inference)
* [Explicit Mapping Definition](#explicit-mapping-definition)

Automatic mapping inference is only supported for [file based virtual schemas](https://github.com/exasol/virtual-schema-common-document-files) using Parquet and CSV files. All other virtual schemas and file formats require an explicit mapping definition.

### Automatic Mapping Inference

To use automatic mapping inference, just omit the `mapping` element from the EDML definition. The virtual schema will then infer the mapping from the schema of the source. Currently this is only supported for Parquet and CSV files.

#### Notes

* The files specified in the `source` must be available when creating the virtual schema. If the files are not available, the `CREATE VIRTUAL SCHEMA` command will fail.
  * When you don't use automatic mapping inference (i.e. you specify the `mapping` element) you can still create the virtual schema as before without `source` files being available.
* The adapter will detect the mapping based on the schema of the first file. Please make sure that all files specified as `source` are using the same schema, else the mapping may be wrong.
* The adapter will detect the mapping when the virtual schema is created. If the schema of the `source` files changes, please drop and re-create the virtual schema to run the auto-inference again.
* Creating the virtual schema with auto-inference will take longer because the adapter needs to read files from the `source`.
* Please see [below](#automatic-mapping-inference-for-csv-files) for details about auto-inference for CSV files.

#### Column Name Conversion

By default the virtual schema will convert source column names to `UPPER_SNAKE_CASE` for Exasol column names during automatic mapping inference. If you want to use the original name from the source file, you can add property `autoInferenceColumnNames` to the EDML definition. This property supports the following values:
* `CONVERT_TO_UPPER_SNAKE_CASE`: Convert column names to `UPPER_SNAKE_CASE` (default).
* `KEEP_ORIGINAL_NAME`: Do not convert column names, use column name from source.

Example:

```json
{
  "$schema": "https://schemas.exasol.com/edml-2.1.0.json",
  "source": "data/CsvWithHeaders.csv",
  "destinationTable": "BOOKS",
  "autoInferenceColumnNames": "KEEP_ORIGINAL_NAME"
}
```

Exasol identifiers like column names must conform to certain criteria, see the [SQL Identifier documentation](https://docs.exasol.com/db/latest/sql_references/basiclanguageelements.htm#SQLIdentifier) for details. If the column names in your source files are invalid Exasol SQL identifiers, the mapping or queries may fail. In this case we recommend using option `CONVERT_TO_UPPER_SNAKE_CASE`.

### Explicit Mapping Definition

If automatic mapping inference is not supported for the required file format (e.g. JSON) or does not work as expected, you can define the mapping manually. The structure of the mapping follows the structure of the document data.

#### Examples
##### Simple Example

Given the following JSON document:

```json
{
  "isbn": "1763413749",
  "name": "Accessing NoSQL-Databases in Exasol using Virtual Schemas",
  "author": {
    "name": "Jakob Braun"
  }
}
```

We want to map documents like that to an Exasol table with the following structure:

```sql
CREATE TABLE BOOKS (
    ISBN        VARCHAR(20),
    NAME        VARCHAR(100),
    AUTHOR_NAME VARCHAR(20)
);
```

The nested property `author.name` is mapped to the column `AUTHOR_NAME`.

In order to let the adapter create the described mapping we create the following definition in the `mapping` property:

```json
{
  "$schema": "https://schemas.exasol.com/edml-2.1.0.json",
  "source": "<data source specific source description>",
  "destinationTable": "BOOKS",
  "description": "Example mapping",
  "mapping": {
    "fields": {
      "isbn": {
        "toVarcharMapping": {
          "varcharColumnSize": 20,
          "description": "The isbn is mapped to a string with max length of 20",
          "overflowBehaviour": "ABORT",
          "required": true
        }
      },
      "name": {
        "toVarcharMapping": {
          "varcharColumnSize": 100,
          "description": "The name is mapped to a string with max length of 100",
          "overflowBehaviour": "TRUNCATE"
        }
      },
      "author": {
        "fields": {
          "name": {
            "toVarcharMapping": {
              "varcharColumnSize": 20,
              "destinationName": "AUTHOR_NAME",
              "description": "Maps the nested property authors.name to column AUTHOR_NAME"
            }
          }
        }
      }
    }
  }
}
```

Next we save this definition to a file, upload it to a bucket in BucketFS and reference it in the `CREATE VIRTUAL SCHEMA` call.

After running [creating a virtual schema](../../README.md) (for example with the schema named `BOOKSHOP`) we can query the table using:

```
SELECT * FROM BOOKSHOP.BOOKS;
```

##### Example of `toJsonMapping`

Document data can contain nested lists. Consider for example the following document:

```json
{
  "isbn": "1763413749",
  "name": "Accessing NoSQL-Databases in Exasol using Virtual Schemas",
  "topics": [
    "DynamoDB",
    "Exasol"
  ]
}
```

We want to map such documents to an Exasol table in the following way:

```sql
CREATE TABLE BOOKS (
    ISBN        VARCHAR(20),
    NAME        VARCHAR(100),
    TOPICS      VARCHAR(200)
);
```

Where `TOPICS` is a `VARCHAR` column containing JSON strings like `["DynamoDB", "Exasol"]`.

To achieve this we create the following mapping definition:

```json
{
  "$schema": "https://schemas.exasol.com/edml-2.1.0.json",
  "source": "<data source specific source description>",
  "destinationTable": "BOOKS",
  "description": "Example mapping",
  "mapping": {
    "fields": {
      "isbn": {
        "toVarcharMapping": {
          "varcharColumnSize": 20,
          "overflowBehaviour": "ABORT"
        }
      },
      "name": {
        "toVarcharMapping": {
          "varcharColumnSize": 100,
          "overflowBehaviour": "TRUNCATE"
        }
      },
      "topics": {
        "toJsonMapping": {
          "description": "Maps the sub document of this property to a JSON string",
          "varcharColumnSize": 200
        }
      }
    }
  }
}
```

The toJsonMapping will map the nested document `topics` to a JSON string in a `TOPICS` column.

##### Example of `toTableMapping`

We again want to map the document with a nested list. But this time we want to map the nested list to a second table that references the original one using a foreign key.

```json
{
  "isbn": "1763413749",
  "name": "Accessing NoSQL-Databases in Exasol using Virtual Schemas",
  "topics": [
    "DynamoDB",
    "Exasol"
  ]
}
```

In addition we know that `isbn` is a unique property (for example since it is a primary key in the data source).

We want to map such documents to the following relational structure:

![Class diagram](mappingToTable.png)

Note that `BOOKS_TOPICS` uses `ISBN` as FOREIGN KEY.

To achieve this we create the following mapping definition:

```json
{
  "$schema": "https://schemas.exasol.com/edml-2.1.0.json",
  "source": "<data source specific source description>",
  "destinationTable": "BOOKS",
  "description": "Example mapping",
  "mapping": {
    "fields": {
      "isbn": {
        "toVarcharMapping": {
          "varcharColumnSize": 20,
          "overflowBehaviour": "ABORT",
          "key": "global"
        }
      },
      "name": {
        "toVarcharMapping": {
          "varcharColumnSize": 100,
          "overflowBehaviour": "TRUNCATE"
        }
      },
      "topics": {
        "toTableMapping": {
          "mapping": {
            "toVarcharMapping": {
              "destinationName": "NAME"
            }
          }
        }
      }
    }
  }
}
```

The Virtual Schema adapter automatically adds a foreign key to the table. In the example above, it adds the column `BOOKS_ISBN` to the `BOOKS_TOPICS` table. It did pick the `ISBN` column, because we marked it as a key column.

#### Key Types

There are two different key types: `global` and `local`. The difference only plays a role when mapping multi-level nested lists.

Consider the following example:

A book contains multiple chapters and a chapter again can contain multiple figures. If in that example a chapter has a global key, that means, it is unique over all existing chapters (also across books). If it defines a local key, it is only unique over all chapters of that book.

#### Autogenerated Keys

If the data source supports it, the virtual schema adapter can also fetch the keys from the data source. In that case, the adapter will use a column as foreign key that is a unique key in the data source.

If you did not mark any column as key and the adapter could not detects any key column, it will add an `INDEX` column. These columns contain the position of the element in the nested list. So in the example from above `DynamoDB` will receive the index 0 and `Exasol` the index 1.

#### Supported Conversion

This adapter can convert input data to the requested column type. For example if the input is a number and the requested column is a string the adapter can convert the number to string.

The conversion is done per value. That means that it's ok if in one row the input value is an integer value and the next row is a boolean value. The adapter can convert both to the requested output column.

That's, however, not always the best option. For that reason, you can configure how the adapter should behave if the input data does not match the requested column format. You can configure this for example using the `nonStringBehaviour`:

* `ABORT`: Abort the whole query with an exception
* `NULL`: Return `NULL` instead
* `CONVERT_OR_ABORT` try to convert and abort the query if not convertible
* `CONVERT_OR_NULL` try to convert and return `NULL` if not convertible

All mappings pass through null values. That means, if the source value is a null-value, the adapter converts it to `NULL`. The only exception is the `toJsonMapping` which converts null values to JSON null values.

##### `toVarcharMapping` Conversions

* Nested object: Not convertible
* Nested list: Not convertible
* String: No conversion needed
* Decimal value: String representation of decimal (e.g: `"1.23"`)
* Double value: String representation of decimal (e.g: `"1.23"`)
* Boolean value: `"true"` or `"false"`
* Binary data: Converted to Base64 encoded data string
* Date: Date as string (e.g: `"2021-09-27"`)
* Timestamp: Timestamp as UTC timestamp (e.g: `"2021-09-21T08:18:38Z"`)

##### `toBoolMapping` Conversions

* Nested object: Not convertible
* Nested list: Not convertible
* String: The adapter tries to parse the string as a boolean, e.g. `"True"` -> `true`. If not possible (e.g: `"abc"`) the adapter handles the value as not convertible.
* Decimal value: Not convertible
* Double value: Not convertible
* Boolean value: No conversion needed
* Binary data: Not convertible
* Date: Not convertible
* Timestamp: Not convertible

##### `toDecimalMapping` Conversions

* Nested object: Not convertible
* Nested list: Not convertible
* String: The adapter tries to parse the string as a number. E.g: `"1.23"` -> `1.23`. If not possible (e.g: `"abc"`) the adapter handles the value as not convertible.
* Decimal value: No conversion needed
* Double value: Converted to decimal
* Boolean value: `true` -> 1, `false` -> 0
* Binary data: Not convertible
* Date: Date as UTC milliseconds time value
* Timestamp: Timestamp as UTC timestamp in milliseconds (floored)

##### `toDoubleMapping` Conversions

* Nested object: Not convertible
* Nested list: Not convertible
* String: The adapter tries to parse the string as a number. E.g: `"1.23"` -> `1.23`. If not possible (e.g: `"abc"`) the adapter handles the value as not convertible.
* Decimal value: Converted to floating-point.
* Double value: No conversion needed
* Boolean value: `true` -> 1, `false` -> 0
* Binary data: Not convertible
* Date: Date as UTC milliseconds time value
* Timestamp: Timestamp as UTC timestamp in milliseconds (floored)

##### `toDateMapping` Conversions

* Nested object: Not convertible
* Nested list: Not convertible
* String: Not convertible
* Decimal value: Interpreted as UTC timestamp in milliseconds
* Double value: Interpreted as UTC timestamp in milliseconds
* Boolean value: Not convertible
* Binary data: Not convertible
* Date: No conversion needed
* Timestamp: Converted to date (looses time information)

##### `toTimestampMapping` Conversions

Please note that EDML only supports data type `TIMESTAMP`. `TIMESTAMP WITH LOCAL TIME ZONE` is not supported.

* Nested object: Not convertible
* Nested list: Not convertible
* String: Not convertible
* Decimal value: Interpreted as UTC timestamp in milliseconds
* Double value: Interpreted as UTC timestamp in milliseconds
* Boolean value: Not convertible
* Binary data: Not convertible
* Date: Converted to timestamp
* Timestamp: No conversion needed

##### `toJsonMapping` Conversions

The `toJsonMapping` always converts the input value to a JSON string. For that reason there is no property like `nonStringBehaviour`.

* Nested object: Converted to JSON string
* Nested list: Converted to JSON string
* String: Converted to JSON string
* Decimal value: Converted to JSON number
* Double value: Converted to JSON number
* Boolean value: Converted to JSON boolean
* Binary data: Converted to JSON string with Base64 encoded data
* Date: Date as JSON string (e.g: `"2021-09-27"`)
* Timestamp: Timestamp as UTC timestamp (e.g: `"2021-09-21T08:18:38Z"`)

### CSV Support

#### CSV File Headers

For CSV files VSD provides the optional JSON object `additionalConfiguration`. In this object you can set `csv-headers` to `true` if the CSV files have a header. If the CSV files don't have a header you can omit this whole block or set `csv-headers` to `false`.

Example:

```json
{
  "$schema": "https://schemas.exasol.com/edml-2.1.0.json",
  "source": "data/CsvWithHeaders.csv",
  "destinationTable": "BOOKS",
  "description": "Maps MY_BOOKS to BOOKS",
  "addSourceReferenceColumn": true,
  "additionalConfiguration": {
    "csv-headers": true
  },
  "mapping": {
    "fields": {
      "id": {
        "toVarcharMapping": {
          "destinationName": "ID"
        }
      }
    }
  }
}
```

##### Mapping CSV Files With Header

When you want to map CSV files with header you use the column name from the CSV header.

The following example maps CSV column with header "id" to database table column "ID":

```json
  "mapping": {
    "fields": {
      "id": {
        "toVarcharMapping": {
          "destinationName": "ID"
        }
      }
    }
  }
```

##### Mapping CSV Files Without Header

When you want to map CSV files without header then you use column index as field name. The index is zero-based, so start counting at 0.

The following example maps the first CSV column (index 0) to database table column "ID":
```json
  "mapping": {
    "fields": {
      "0": {
        "toVarcharMapping": {
          "destinationName": "ID"
        }
      }
    }
  }
```

### Whitespace in CSV Files

Please note that VSD trims whitespace in CSV column header names. If a CSV file contains header `id, name, value`, you can specify fields `"id"`, `"name"` and `"value"` in your mapping instead of `"id"`, `" name"` and `" value"`.

Values however are not trimmed, so if your CSV contains values with leading or trailing whitespace, this will also appear in the Exasol table. If necessary you can trim the whitespace in your SQL query using Exasol's built-in function [`TRIM`](https://docs.exasol.com/db/latest/sql_references/functions/alphabeticallistfunctions/trim.htm).

### Supported Mappings for CSV

VSD supports the following mappings for CSV files:

* `toVarcharMapping`
* `toDecimalMapping`
* `toDoubleMapping`
* `toBoolMapping`: Strings `true` and `false` are mapped to boolean case insensitively.
* `toDateMapping`: Date values must use format `yyyy-[m]m-[d]d`.
* `toTimestampMapping`: Timestamp values must use format `yyyy-[m]m-[d]d hh:mm:ss[.f...]`.

If your CSV files use an unsupported format for dates or timestamps, please use `toVarcharMapping` for these columns and convert the values to the correct type in your SQL query using Exasol's built-in functions:

* [`CAST`](https://docs.exasol.com/db/latest/sql_references/functions/alphabeticallistfunctions/cast.htm) / [`CONVERT`](https://docs.exasol.com/db/latest/sql_references/functions/alphabeticallistfunctions/convert.htm)
* [`TO_DATE`](https://docs.exasol.com/db/latest/sql_references/functions/alphabeticallistfunctions/to_date.htm) / [`TO_TIMESTAMP`](https://docs.exasol.com/db/latest/sql_references/functions/alphabeticallistfunctions/to_timestamp.htm)
* [`TO_NUMBER`](https://docs.exasol.com/db/latest/sql_references/functions/alphabeticallistfunctions/to_number.htm)

These functions allow specifying a custom format, e.g. `HH24:MI:SS DD-MM-YYYY` for timestamps.

Example:

```sql
SELECT TO_TIMESTAMP(TIMESTAMP_COLUMN, 'HH24:MI:SS DD-MM-YYYY') CONVERTED_TIMESTAMP
FROM TEST_SCHEMA.DATA_TYPES;
```

#### Null and Empty Values

Null and empty values are currently not supported in CSV files. If your CSV files contain special `null` or empty values, please use `toVarcharMapping` and convert the values using Exasol's built-in function [`CASE ... WHEN ... THEN ...`](https://docs.exasol.com/db/latest/sql_references/functions/alphabeticallistfunctions/case.htm).

### Automatic Mapping Inference for CSV Files

See the section [above](#automatic-mapping-inference) for general information about auto-inference.

When the `mapping` element is missing in the EDML definition, VSD will automatically detect 
* whether the CSV file contains a header
* and the data types of the columns

#### CSV Header Presence Detection

VSD tries to detect if a CSV file contains a header or not based on the data types of the first two rows:

* If the first row contains non-string values, VSD assumes there is no header
* If the first and second row contain values with the same types, VSD assumes there is no header
* Else VSD assumes there is a header

If a header is present, VSD will convert the CSV column names to UPPER_SNAKE_CASE and use that as the table column name. Assuming a CSV column has name `userId`, VSD will map this to table column name `USER_ID`.

If no header is present, VSD will map CSV columns to table column names `COLUMN_0`, `COLUMN_1` etc.

In case the automatic header detection fails and wrongly assumes there is no header, you can filter out the header row using a `WHERE` condition in your SQL query.

#### CSV Data Type Detection

VSD detects the column types in the CSV file and converts the values to an appropriate Exasol type:

* Strings: `VARCHAR(2000000)`
* Characters (Strings of length 1): `VARCHAR(1)`
* Boolean values like `true` or `False`: `BOOLEAN`
* Integers between `-2147483648` and `2147483647`: `DECIMAL(10,0)`
* Integers between `-9223372036854775808` and `9223372036854775807` `DECIMAL(20,0)`
* Numbers with a decimal point: `DOUBLE PRECISION`

VSD does not detect date or timestamp types as there are too many formats. Instead, these columns are mapped to `VARCHAR(2000000)`. In order to convert date and timestamps to the correct type, use one of the following Exasol functions:

* If the format matches the Exasol format for date and timestamps, you can use [`CAST`](https://docs.exasol.com/db/latest/sql_references/functions/alphabeticallistfunctions/cast.htm), e.g. `CAST(... AS DATE)` or `CAST(... AS TIMESTAMP)`.
* For dates in a custom format use [`TO_DATE`](https://docs.exasol.com/db/latest/sql_references/functions/alphabeticallistfunctions/to_date.htm).
* For timestamps in a custom format use [`TO_TIMESTAMP`](https://docs.exasol.com/db/latest/sql_references/functions/alphabeticallistfunctions/to_timestamp.htm).

See the [documentation of Exasol's format models](https://docs.exasol.com/db/latest/sql_references/formatmodels.htm#DateTimeFormat) about specifying custom formats for `TO_DATE` and `TO_TIMESTAMP`.

## Reference

* [Schema mapping language schema & reference](https://schemas.exasol.com/#exasol-document-mapping-language-edml)
