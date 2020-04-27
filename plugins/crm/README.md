**OFBiz Deployment in Heroku <br>**
Using GitHub Integration and AWS RDS Database <br><br>
_Prerequisites<br>_
The setup I ended up using consisted of a direct CI from our tutinformatics/ofbiz repository using Heroku?s GitHub integration and Heroku Dashboard. 

For it to work, you need to have Martin Rebane grant access to your Heroku Dashboard and give your GitHub account maintainer access to the repository in question.

Instead of the original plan, which entailed using Heroku's in-built PostgreSQL database (the free tier is limited to 10 000 lines, which equates to approximately one table in OFBiz) I suggest using a service similar to AWS RDS. 

AWS has lots of options for students and is partially included in GitHub Education as well.

All in all, you need the following: <br>
GitHub Education package<br>
Heroku account connected to said package<br>
A free dyno (you can get a hobby dyno through GitHub Education)<br>
AWS account connected to said package<br>
A free RDS database instance running PostgreSQL 11.5<br>


Creating a Heroku Pipeline and App

First, create a new pipeline, name it and connect it to a branch from the repository you previously requested access to. 

Secondly you should create a new app space on your dyno, choose a region and add it to your newly created pipeline.
Creating an AWS RDS instance
We used AWS Educate and Vocareum accounts to gain 100$ worth of AWS credits to our Educate account. Both need to be tied to your TalTech email account and take a bit of time to be verified by the service providers.

Once you have access to your AWS Console, you’ll be able to configure an RDS PostgreSQL instance.

On the next page you will be able to configure your RDS instance. The easiest and most fool-proof way would be to use the free tier template. 

You will need to configure additional database connections in entityengine.xml (please refer to entityengine.xml in this branch and look for rdspostgres configrations).

You will need rdspostgres, rdspostolap and rdsposttenant definitions. 

Every one of them will need the correct jdbc-url pointing to your RDS instance and database.

After creating a 'stage' task (containing cleanAll and loadAll task callbacks) in your build.gradle file and positioning it at the end of all previous task configurations you should be able to commit to the branch configured in Heroku and it should trigger an automatic build on your dyno.

You will need an additional Procfile defined in the root of your branch to trigger gradlew Ofbiz as well. (def below) <br>
_**web: chmod a+x gradlew && ./gradlew ofbiz --debug**_
<br>
This will give your script the necessary rights to run as well as the debug flag for additional output.

To follow the deploy logs you will need to install Heroku CLI on your local machine.

After installation and logging in (heroku login, you will be prompted to open a browser tab and to enter your credentials) you can use the following command to see the tail end of your dyno's logs.<br>
**_heroku logs --tail --app your-app-name_**

At the end of the day, though, I did not find a way past Heroku's limitations on RAM and the project failed to deploy either by timing out (default boot timeout is 60 sec) or by crashing due to the dyno running out of RAM.

I ended up deploying OFBiz locally through a forwarded port on a spare machine at home.
