- This repository contains the code provide by FETCHR : https://github.com/talal-shobaita/hello-world plus a Groovy file (build.groovy) , a Dockerfile and a dockerentry file to set some Docker variables required for the tomcat docker image
- The build.groovy file will be read by the jenkins pipeline and does the following step : 
    . Checkout the source code , switches to the master branch , build the code and creates a docker image (Which has the .war file which installs in a  a tomcat servlet)

