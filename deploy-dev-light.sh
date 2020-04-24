sudo docker image rmi $(sudo docker image ls -f='dangling=true' -q) -f || true
docker-compose -p ofbiz -f docker-compose-databases-dev-light.yml up -d
docker-compose -p ofbiz -f docker-compose-dev.yml up
sudo docker image rmi $(sudo docker image ls -f='dangling=true' -q) -f || true
