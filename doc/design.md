## EDML (de-) Serialization

`dsn~edml~serialization~1`

For the EDML serialization we implemented a serializer and deserializer by hand, based on the Jakarta JSON API.

We did not use an object mapper like json databind or Jackson for the following reasons (in fact we first used Jackson and removed it):

* We want to keep the number of used JSON libs low. We already hava jakarta JSON ad dependency from virtual-schema-common-java. Jackson would be an additional one.
* The polymorphism of MappingDefinition is not supported by jakarta JSON.bind 2.0.0. Support will be added in 3.0.0.
* When serializing a mapping definition we add another hierarchy level:
  ```json
  {
    "mapping":{
      "toJsonMapping": {
        "key": "local"
      } 
    }
  }
  ```
  While in Java the EdmlDefinition (root object) directly references a `ToJsonMapping` object, in the EDML we have another JSON object. Making this structure change with an object mapper is quite tricky (We tried with custom serializer, but it got quite complex and hard to understand).
* Having a non-automatic deserializer allows us to simply add multiple deserializers for different EDML versions. For example by copying the whole deserializer and modifying it.

Needs: impl, utest