
node {
  def branchVersion = ""

  stage ('Checkout') {
    // checkout repository
    checkout scm

    // save our docker build context before we switch branches
   // sh "cp -r ./.docker/build tmp-docker-build-context"
    
    // checkout input branch 
    sh "git checkout master"
  }

  stage ('Determine Branch Version') {
    // add maven to path
    withEnv(["MAVEN_HOME=/usr/local/bin"]){

    // determine version in pom.xml
    branchVersion = sh(script: 'mvn -q -Dexec.executable=\'echo\' -Dexec.args=\'${project.version}\' --non-recursive exec:exec', returnStdout: true).trim()
    echo "$branchVersion"
    // compute proper branch SNAPSHOT version
    //pomVersion = pomVersion.replaceAll(/-SNAPSHOT/, "") 
    //branchVersion = env.BRANCH_NAME
     // echo "$branchVersion"
    //branchVersion = branchVersion.replaceAll(/origin\//, "") 
   // branchVersion = branchVersion.replaceAll(/\W/, "-")
   // branchVersion = "${pomVersion}-${branchVersion}-SNAPSHOT"

    // set branch SNAPSHOT version in pom.xml
    sh "mvn versions:set -DnewVersion=${branchVersion}"
    }
  }

  stage ('Java Build') {
    // build .war package
    sh 'mvn clean package -U'
  }
  
  stage ('Docker Build') {
    // prepare docker build context
    //sh "cp target/us.fetchr.sample-${branchVersion}.war ./tmp-docker-build-context"
    
    sh "ls"

    // Build and push image with Jenkins' docker-plugin
    withDockerServer([uri: ""]) {
      withDockerRegistry([credentialsId: 'b9b41a4c-4b81-49e0-8e54-b92bd3fa9fa7', url: "https://0.0.0.0:4243/"]) {
        // we give the image the same version as the .war package
        def image = docker.build("roshaneishara/jenkins-pipeline-hello-world:${branchVersion}", "--build-arg PACKAGE_VERSION=${branchVersion} .")
        image.push()
      }   
    }
  }
}
