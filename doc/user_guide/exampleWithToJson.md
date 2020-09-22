# Example of `toJsonMapping`

In this guide we want to map a documents like the following to an Exasol table:

```json
{
  "isbn": "1763413749",
  "name": "Accessing NoSQL-Databases in Exasol using Virtual Schemas",
  "topics": ["DynamoDB", "Exasol"]
}
```

We want to map such documents to an Exasol table in the following way:

```
CREATE TABLE BOOKS (
    ISBN        VARCHAR(20),
    NAME        VARCHAR(100),
    TOPICS      VARCHAR(200)
);
```
 
Where `TOPICS` is a `VARCHAR` column containing a JSON strings like `["DynamoDB", "Exasol"]`.

To achieve this we create the following mapping definition:  

```json
{
  "$schema": "https://schemas.exasol.com/edml-1.1.0.json",
  "source": "MY_BOOKS",
  "destinationTable": "BOOKS",
  "description": "Maps MY_BOOKS to BOOKS with toJSON",
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
