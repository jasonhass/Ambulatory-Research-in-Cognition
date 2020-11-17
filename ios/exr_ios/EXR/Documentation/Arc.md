#  Arc

-Core data contains the truth for most of the applications data. 
-Preferences contain settings that are trivial and don't require the full backing of core data to operate. 

-Arc contains a list of controllers that it uses to abstract away direct acess to coredata and preferences.
-Each controller simply references coredata and can be used directly to perform certain actions. 
	-It is import ant to note that while you can create instances of controllers to perform tasks, 
	its prefered to use Arc.shared.controller. As the controller stored here could be a variant subclass 
	that is used instead by the outer module containing Arc. 
-Each controller subclass introduces changes to logic specific to the app that is using arc as a product. 

