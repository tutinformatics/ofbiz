# Basic tutorial how to develop Ofbiz in docker

## Prerequisites
- Docker _(or Docker toolbox for Windows)_
- Docker-compose 

## Basic lifecycle
**First launch:**
```bash
# You can use -d for detatched mode
sudo docker-compose up
```
**If you don't need restart _(ftl templates, xml controller, etc)_:**  
Simply edit files as they are mounted into docker so no restart is required! 
   
**For every update that needs recompiling java:**
```bash
# Stop the container (Can be acheived with Ctrl+C if dot detatched)
sudo docker-compose stop ofbiz
# Start it again
sudo docker-compose up
```
**For every update that needs _"loadAll"_:**
```bash
# Stop container (Can be acheived with Ctrl+C if dot detatched)
sudo docker-compose stop ofbiz
# Remove all stopped containers and images
sudo docker system prune -af
# Start again
sudo docker-compose up
```