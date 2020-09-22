# Getting Started With the Exasol Document Mapping Language (EDML)

For creating a Virtual Schema for document data you have to define a mapping 
from the document structure to a relational structure.
This is done using the Exasol Document Mapping Language (EDML) 
([reference](https://exasol.github.io/virtual-schema-common-ducument/schema_doc/index.html)).
You have to defined the mapping in a JSON document, upload it to a bucket in BucketFS and reference 
in the `CREATE VIRTUAL SCHEMA` call.

This guide explains you how to define mappings these mappings in general.
For data source specifics, check the corresponding virtual schema.
Different data source use different data formats.
In this guide we use JSON.
  
For mapping multiple document sets, you can create multiple files, 
upload them to a folder and BucketFS and reference this folder. 

The structure of the mapping follows the structure of the document data.

## Example

Given a DynamoDB table called `MY_BOOKS` that contains the following objects:

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
    AUTHOR_NAME  VARCHAR(20)
);
```
The nested property `author.name` shall be mapped to the column `AUTHOR_NAME`. 

In order to let this adapter create the described mapping we create the following mapping definition:

```json
{
  "$schema": "https://schemas.exasol.com/edml-1.1.0.json",
  "source": "MY_BOOKS",
  "destinationTable": "BOOKS",
  "description": "Maps MY_BOOKS to BOOKS",
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
              "description": "Maps the nested property authors.name to column authorName"
            }
          }
        }
      }
    }
  }
}
```

The `source` property describes the source of the data. 
It's syntax and meaning is different for the different Virtual Schemas for different data sources. Check the corresponding user guide for details. 

Next we save this definition to a file, upload it to a bucket in 
BucketFS and reference it in the `CREATE VIRTUAL SCHEMA` call.

After running [creating a virtual schema](../README.md) (for example with the schema named `BOOKSHOP`) we can query the table using:

```
SELECT * FROM BOOKSHOP.BOOKS;
```

### More Examples
* [Example for toJsonMapping](exampleWithToJson.md)
* [Example for toTableMapping](exampleWithToTable.md)

## Reference
[Schema mapping language reference](https://exasol.github.io/virtual-schema-common-ducument/schema_doc/index.html)
