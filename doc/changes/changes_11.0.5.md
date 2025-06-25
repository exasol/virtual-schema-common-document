# Common Virtual Schema for Document Data 11.0.5, released 2025-06-25

Code name: Improve query plan logging

## Summary

This release implements logging for `runQuery` method of the class `DocumentAdapter`. This allows improving logging around the paths that lead to the creation of the query plan.

## Features

* #202: Add more logging around the paths that lead to the creation of the query plan