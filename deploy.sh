docker-compose -p ofbiz -f docker-compose-databases.yml up -d
docker-compose -p ofbiz -f docker-compose-databases.yml scale worker=5
docker-compose -f docker-compose-databases.yml restart master
docker-compose -p ofbiz -f docker-compose-ofbiz.yml up -d
