docker-compose -p ofbiz -f docker-compose-databases.yml up -d
docker-compose -p ofbiz scale worker=5
docker-compose -p ofbiz -f docker-compose-ofbiz.yml up -d
