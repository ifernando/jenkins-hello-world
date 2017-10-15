
node {
  def branchVersion = ""

  stage ('Checkout') {
    // checkout repository
    checkout scm

  
    
    // checkout input branch 
    sh "git checkout master"
  }

  stage ('Determine Branch Version') {
    // add maven to path
    withEnv(["MAVEN_HOME=/usr/local/bin"]){

    // determine version in pom.xml
    branchVersion = sh(script: 'mvn -q -Dexec.executable=\'echo\' -Dexec.args=\'${project.version}\' --non-recursive exec:exec', returnStdout: true).trim()
    echo "$branchVersion"
   

    // set branch SNAPSHOT version in pom.xml
    sh "mvn versions:set -DnewVersion=${branchVersion}"
    }
  }

  stage ('Java Build') {
    // build .war package
    sh 'mvn clean package -U'
  }
  
  stage ('Docker Build') {
    
    
    sh "ls"

    // Build and push image with Jenkins' docker-plugin
    withDockerServer([uri: ""]) {
      withDockerRegistry([credentialsId: '35a1f568-4a8e-4a2c-b415-728763bd8538', url: "https://0.0.0.0:4243/"]) {
        // we give the image the same version as the .war package
        def image = docker.build("roshaneishara/jenkins-pipeline-hello-world:${branchVersion}", "--build-arg PACKAGE_VERSION=${branchVersion} .")
        image.push()
      }   
    }
  }
}
