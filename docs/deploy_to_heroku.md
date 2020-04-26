Tutorial on how to deploy to heroku

1) Go to https://dashboard.heroku.com/new-app and select `Europe` as region
2) Under Deploy tab - Deployment method GitHub -> Connect to `tutinformatics/ofbiz` repository, choose your branch and `Enable Automatic Deploys`
3) Minge `framework/entity/config/entityengine.xml` ning vahetage (Ctrl + F) `datasource-name="localcitus` ära (Ctrl + R) `datasource-name="localpostgres` vastu ära

Ärge seda muudatust trunk'i pushige!!!

4) Under Settings tab - `Config Vars` - `GRADLE_TASK`=`cleanAll loadAll ofbiz`
5) Press `Deploy Branch` just to make sure it finds everything it's supposed to find. If not, go back to step two

Mingi selline pilt võiks tekkida
```
-----> Gradle app detected
-----> Installing JDK 11... done
-----> Building Gradle app...
-----> executing ./gradlew cleanAll loadAll ofbiz
```

