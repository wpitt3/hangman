#!/bin/bash

PWD=$(pwd)
curl -X PUT http://localhost:9200/hangman -d @$PWD/resources/hangmanSchema.json --header "Content-Type: application/json"
