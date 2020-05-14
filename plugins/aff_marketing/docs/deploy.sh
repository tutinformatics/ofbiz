version=v15
sudo docker build -t gcr.io/uplifted-cinema-271812/ofbiz-back:$version .
sudo docker push gcr.io/uplifted-cinema-271812/ofbiz-back:$version
sudo kubectl set image deployment/ofbiz-back ofbiz-back=gcr.io/uplifted-cinema-271812/ofbiz-back:$version
