#!/bin/bash

readonly root_dir='virtual-schema-common-document'
readonly schema_path='./src/main/resources/schemas/edml-1.2.0.json'
readonly doc_dir='/tmp/doc/'

prepare() {
    verify_current_directory "$root_dir"
}


verify_current_directory() {
    if [[ $(basename "$PWD") != "$root_dir" ]]
    then
        log "Must be in root directory '$root_dir' to execute this script."
        exit 1
    fi
}

log () {
    echo "$@"
}

prepare
bootprint json-schema $schema_path $doc_dir
firefox "$doc_dir/index.html"