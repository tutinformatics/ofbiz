docker rm -vf $(docker ps -aq) &&
  docker rmi -f $(docker images -aq) &&
  docker volume prune -f

docker container stop "$(docker container ls -aq)"

rm -rf build
rm -rf ./runtime/catalina/work/*

docker system prune -af
