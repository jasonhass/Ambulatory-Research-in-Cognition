#  Fast Lane
To perform a build you will need to get your user token and create a file called "Localfile" inside the fastlane folder. 
	-Ignore this file in your repo
	-the only line of text needed for this file is "ENV["AUTH_TOKEN"] = "Your_auth_token_from_enterprise_server"
	-It will create builds in your name. 
	
	Once the localfile is created, run: fastlane qabuild
	
	
