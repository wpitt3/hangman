#!/bin/bash

curl -vX PUT http://localhost:9200/hangman -d @src/main/resources/hangmanSchema.json --header "Content-Type: application/json"
