#!/bin/bash

docker pull docker.elastic.co/elasticsearch/elasticsearch:6.5.4

docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.5.4

curl -vX PUT http://localhost:9200/my_index
curl -vX PUT http://localhost:9200/my_index -d @src/main/resources/hangmanSchema.json --header "Content-Type: application/json"t-Type: application/json"