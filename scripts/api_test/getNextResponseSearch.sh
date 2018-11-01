#!/usr/bin/env bash

QUERY=${1:-"how to install starchat"}
PORT=${2:-8888}
INDEX_NAME=${3:-index_getjenny_english_0}
curl -v -H "Authorization: Basic $(echo -n 'test_user:p4ssw0rd' | base64)" \
 -H "Content-Type: application/json" -X POST http://localhost:${PORT}/${INDEX_NAME}/get_next_response -d "{
	\"conversationId\": \"1234\",
	\"userInput\": { \"text\": \"${QUERY}\" },
	\"values\": {
		\"returnValue\": \"\",
		\"data\": {\"varname1\": \"value1\", \"varname2\": \"value2\"}
	},
	\"threshold\": 0.0,
	\"maxResults\": 4
}"
