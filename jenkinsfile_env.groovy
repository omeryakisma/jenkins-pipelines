node { 
  // comment here
  properties([
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), 
    pipelineTriggers([pollSCM('* * * * *')]),
    parameters([
      choice(
        choices: [
	  'dev1.yakisma.com', 
          'qa1.yakisma.com', 
	  'stage1.yakisma.com', 
          'prod1.yakisma.com'
        ], 
	description: 'Please choose an environment', 
	name: 'ENV'
      ),
      choice(
        choices: [
          'v0.1', 
          'v0.2', 
          'v0.3', 
          'v0.4', 
          'v0.5'
        ],  
        description: 'Which version should we deploy?',  
        name: 'Version'
      ),
      string(
        defaultValue: 'dev',  
        description: 'Which environment should I build the app?',  
        name: 'Environment', 
        trim: true
      )
    ])
  ])
  stage("Pull FarrukH Branch"){ 
    checkout([$class: 'GitSCM', branches: [[name: '*/FarrukH']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/farrukh90/cool_website.git']]])
  }
  stage("Install Prerequisites"){
  sh """
  ssh centos@${ENV}                 sudo yum install httpd -y
  """
  }
  
  stage("Copy Artifacts"){ 
    sh """
    scp -r *  centos@${ENV}:/tmp
    ssh centos@${ENV}              sudo cp -r /tmp/index.html /var/www/html/
    ssh centos@${ENV}              sudo cp -r /tmp/style.css /var/www/html/
    ssh centos@${ENV}              sudo chown centos:centos /var/www/html/
    ssh centos@${ENV}              sudo chmod 777 /var/www/html/*
    ssh centos@${ENV}              sudo systemctl restart httpd 
    """
   } 
  stage("Restart Web Server"){ 
    ws ("tmp/"){
      sh """
        ssh centos@${ENV}    sudo systemctl restart httpd 
      """
    }
  }
  stage("Send Notifications to Slack"){ 
    ws("mnt/"){
      mail bcc: '', body: 'Running', cc: 'support@company.com', from: '', replyTo: '', subject: 'Test', to: 'yakisma.omer@gmail.com' 
    }
  }  
}
