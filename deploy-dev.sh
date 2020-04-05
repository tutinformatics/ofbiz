docker-compose -p ofbiz -f docker-compose-databases-dev.yml up -d
docker-compose -p ofbiz -f docker-compose-databases-dev.yml scale worker=1
docker-compose -f docker-compose-databases.yml restart master
docker-compose -p ofbiz -f docker-compose-dev.yml up
