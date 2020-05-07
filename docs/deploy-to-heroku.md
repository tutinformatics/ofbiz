Tutorial on how to create a jar with dependencies locally and how to push it to heroku

First: (Recommended to use some othet deploy environment. Heroku servers are not beefy enough)
1) Minge `framework/entity/config/entityengine.xml` ning vahetage (Ctrl + F) `datasource-name="localcitus` ära (Ctrl + R) `datasource-name="localderby` vastu ära
2) gradlew.bat runShadow
3) heroku plugins:install java
4) heroku create --no-remote
5) heroku deploy:jar target/my-app.jar --app <name_here>

All others:

1) gradlew.bat runShadow
2) heroku deploy:jar target/my-app.jar --app <name_here>

Kui teil aga on admin õigused seate CI/CD püsti panna järgnevalt

1) Go to https://dashboard.heroku.com/new-app and select `Europe` as region
2) Under Deploy tab - Deployment method GitHub -> Connect to `tutinformatics/ofbiz` repository, choose your branch and `Enable Automatic Deploys`
3) Minge `framework/entity/config/entityengine.xml` ning vahetage (Ctrl + F) `datasource-name="localcitus` ära (Ctrl + R) `datasource-name="localderby` vastu ära

Seejärel tehke failist koopia ning pange deploy kausta. Ning deploy olukorras kirjutage faili sisu ümber. Xml fail revertida!

4) Under Settings tab - `Config Vars` - `GRADLE_TASK`=`test` - see jooksutab teste staging etapis

5) Press `Deploy Branch` just to make sure it finds everything it's supposed to find. If not, go back to step two

Mingi selline pilt võiks tekkida kui oled asju vastavalt konfinud
```
-----> Gradle app detected
-----> Installing JDK 11... done
-----> Building Gradle app...
-----> executing ./gradlew cleanAll loadAll ofbiz
```

