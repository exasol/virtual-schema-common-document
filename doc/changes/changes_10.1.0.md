# Common Virtual Schema for Document Data 10.1.0, released 2023-12-11

Code name: Support `ALTER VIRTUAL SCHEMA SET`

## Summary

This release adds support for `ALTER VIRTUAL SCHEMA SET`. This will allow changing properties like `MAPPING` of document based virtual schemas without dropping and re-creating the virtual schema:

```sql
-- Update EDML mapping of the virtual schema
ALTER VIRTUAL SCHEMA MY_VIRTUAL_SCHEMA SET MAPPING = '...'

-- Enable remote logging or change the log level
ALTER VIRTUAL SCHEMA MY_VIRTUAL_SCHEMA SET DEBUG_ADDRESS = 'host:3000' LOG_LEVEL = 'FINEST'
ALTER VIRTUAL SCHEMA MY_VIRTUAL_SCHEMA SET LOG_LEVEL = 'INFO'
```

## Features

* #52: Added support for `ALTER VIRTUAL SCHEMA SET`
