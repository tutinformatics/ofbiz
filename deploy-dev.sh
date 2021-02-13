# Linux
docker-compose -f docker-compose-databases.yml up -d
curl -s -H "Content-Type: application/json" -X PUT "http://localhost:5984/_users" -u ofbiz:ofbiz # This is needed
./gradlew cleanAll loadAll ofbiz

# Windows?
#gradlew.bat ofbiz