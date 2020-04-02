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
    )
    ])
  ])
  stage("Stage1"){ 
    git 'https://github.com/farrukh90/cool_website.git'
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
    sh """
    ssh centos@dev1.yakisma.com    sudo systemctl restart httpd 
    """
   }
  stage("Send Notifications to Slack"){ 
    slackSend color: '#BADA55', message: 'Hello, World!' 
  } 
  stage("Send Email to Support"){ 
    mail bcc: '', body: 'Running', cc: 'support@company.com', from: '', replyTo: '', subject: 'Test', to: 'yakisma.omer@gmail.com' 
  } 
} 
