node { 
  stage("Stage1"){ 
    echo "hello" 
  } 
  stage("Stage2"){ 
    echo "hello" 
  } 
  stage("Ask for Input"){ 
    input 'Should I proceed?' 
  } 
  stage("Stage4"){ 
    echo "hello" 
   } 
  stage("Stage5"){ 
    echo "hello" 
  } 
  stage("Send Notifications to Slack"){ 
    slackSend color: '#BADA55', message: 'Hello, World!' 
  } 
  stage("Send Email to Support"){ 
    mail bcc: '', body: 'Running', cc: 'support@company.com', from: '', replyTo: '', subject: 'Test', to: 'yakisma.omer@gmail.com' 
  } 
} 
