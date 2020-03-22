docker-compose -p ofbiz -f docker-compose-databases.yml up -d
docker-compose -p ofbiz -f docker-compose-databases.yml scale worker=1
docker-compose -f docker-compose-databases.yml restart master
docker-compose -p ofbiz -f docker-compose-dev.yml up -d
curl -s -H "Content-Type: application/json" -X PUT "http://localhost:5984/_users" -u ofbiz:ofbiz
