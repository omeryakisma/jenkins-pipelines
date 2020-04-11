node { 
	properties([
        parameters([
            choice(
                choices:[
				    '0.1', 
				    '0.2', 
				    '0.3', 
				    '0.4', 
				    '0.5',
				    '0.6',
				    '0.7',
				    '0.8',
				    '0.9',
				    '1.0',
			    ], 
		        description: 'Which version of the app should I deploy? ', 
		        name: 'Version'
            ), 
	        choice(
                choices: [
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
	stage("Pull ${Version} For ${ENVIR}"){
		timestamps {
			ws {
				checkout([$class: 'GitSCM', branches: [[name: '${Version}']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/farrukh90/artemis.git']]]) 
            }
		}
	}
	stage("Get Credentials"){
		timestamps {
			ws{
				sh '''
					ssh centos@${ENVIR} aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 771745960392.dkr.ecr.us-east-1.amazonaws.com/artemis
				'''
			}
		}
	}
	stage("Build Docker Image"){     // make sure docker is installed on the vm
		timestamps {                 // echo 1 > /proc/sys/vm/drop_caches
			ws {
				sh '''   
			   		ssh centos@${ENVIR} docker build -t artemis:${Version} .
				'''
			}
		}
	}
	stage("Tag Image"){
			timestamps {
				ws {
					sh '''
						ssh centos@${ENVIR} docker tag artemis:${Version} 771745960392.dkr.ecr.us-east-1.amazonaws.com/artemis:${Version}
					'''
					}
				}
			}
	stage("Push Image"){  // into DockerHub, ECR, Jenkins and Artifactory, an image folder/repository
		timestamps {
			ws {
				sh '''
					ssh centos@${ENVIR} docker push 771745960392.dkr.ecr.us-east-1.amazonaws.com/artemis:${Version}
				'''
			}
		}
	}
}
