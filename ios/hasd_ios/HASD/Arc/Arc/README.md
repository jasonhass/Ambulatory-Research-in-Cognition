#  The App
Handling Differences between pool outs of the app:

	-Different versions of the app will have slightly different behavior, to handle this you can:
		
		> Add the new functionality to the project that includes the core framework.
		
		> If it is present in the core already, sub-class it. To use that subclass look for ##State.swift. ## == some prefix specific to the project, like AC. 
			
	-If all versions of the app should have this functionality or are capable of being updated to later use that functionality, add it directly to the core.
	
	-Environments are a way to supply controller functionality. Check ACEnvironment.swift. The intent here is to allow external frameworks to customize the arc core. 
	
	-Phase.swift is an interface for modifying how scheduling works, you can set the length of study periods and what is taken per session per day. 
	
	

Environments: 
	
	- ACEnvironment is a protocol that allows you to set various configurations such as where data is sent, language, how data is stored and so on. This file is intended to set subclasses for the Arc.shared interface. 
	
	

Surveys:
	
	-All surveys are meant to run through BasicSurveyController.swift
	
		-The style of a survey question in this case will be determined by display(question:), the intent of this function is to style the surveyViewControllers view and update text displayed on the screen. 
		
	-The views backing the controller is called InfoView.swift
	
	

