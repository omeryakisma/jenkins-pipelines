node { 
    properties([
        parameters([
            choice(
                choices:[ 
                    'version/0.1',  
                    'version/0.2',  
                    'version/0.3',  
                    'version/0.4',  
                    'version/0.5',
                    'version/0.6',
                    'version/0.7',
                    'version/0.8',
                    'version/0.9',
                    'version/1.0'
                ],  
                description: 'Which version of the app should I deploy? ',  
                name: 'Version'
            ),  
            choice(
                choices:[ 
                    'dev1.yakisma.com',  
                    'qa1.yakisma.com',  
                    'stage1.yakisma.com',  
                    'prod1.yakisma.com'
                ],  
                description: 'Please provide an environment to build the application',  
                name: 'ENVIR'
            )
        ])
    ]) 
    stage("Pull  [${ENVIR}, ${Version}]"){ 
       timestamps { 
           ws { 
               checkout([$class: 'GitSCM', branches: [[name: '${Version}']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/farrukh90/artemis.git']]]) 
           } 
       } 
    } 
    stage("Install Prerequisites"){ 
        timestamps { 
            ws{ 
                sh ''' 
                    ssh centos@${ENVIR} sudo yum install epel-release -y 
                    ssh centos@${ENVIR} sudo yum install python-pip -y  
                    ssh centos@${ENVIR} sudo pip install Flask 
                ''' 
            } 
        } 
    }   
    stage("Copy Artemis"){ 
        timestamps { 
            ws { 
                sh ''' 
                    scp -r * centos@${ENVIR}:/tmp 
                ''' 
            } 
        } 
    } 
    stage("Run Artemis"){ 
        timestamps { 
            ws { 
                sh ''' 
                    ssh centos@${ENVIR} nohup python /tmp/artemis.py &
                ''' 
            } 
        } 
    }
    stage("Send slack notifications"){ 
        timestamps { 
            ws { 
                echo "Slack" 
                //slackSend color: '#BADA55', message: 'Hello, World!' 
            } 
        } 
    } 
} 
