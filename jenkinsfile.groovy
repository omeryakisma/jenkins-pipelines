node { 
  // comment here
  properties([
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), 
    pipelineTriggers([pollSCM('* * * * *')])
  ])
  stage("Stage1"){ 
    git 'https://github.com/farrukh90/cool_website.git'
  } 
  stage("Install Prerequisites"){
		sh """
		ssh centos@jenkins_worker1.acirrustech.com              sudo yum install httpd -y
		"""
  }

  stage("Copy Artifacts"){ 
    sh """
    scp -r *  centos@jenkins_worker1.acirrustech.com:/tmp
		ssh centos@jenkins_worker1.yakisma.com              sudo cp -r /tmp/index.html /var/www/html/
		ssh centos@jenkins_worker1.yakisma.com              sudo cp -r /tmp/style.css /var/www/html/
		ssh centos@jenkins_worker1.yakisma.com				      sudo chown centos:centos /var/www/html/
		ssh centos@jenkins_worker1.yakisma.com				      sudo chmod 777 /var/www/html/*
		ssh centos@jenkins_worker1.yakisma.com              sudo systemctl restart httpd 
    """
   } 
  stage("Restart Web Server"){ 
    sh """
		ssh centos@jenkins_worker1.yakisma.com              sudo systemctl restart httpd 
    """
   }
  stage("Send Notifications to Slack"){ 
    slackSend color: '#BADA55', message: 'Hello, World!' 
  } 
  stage("Send Email to Support"){ 
    mail bcc: '', body: 'Running', cc: 'support@company.com', from: '', replyTo: '', subject: 'Test', to: 'yakisma.omer@gmail.com' 
  } 
} 
