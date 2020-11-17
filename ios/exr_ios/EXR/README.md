Setup:
1. Add the "arc_core_ios" submodule to your repo.

2. Create an enum/object that conforms to ArcEnvironment
    -import Arc
    -Adopt several variables to configure the environment
    -Create a userTest, dev, qa, and production environment
    -Adopt settings needed for a particular server

3. Create an enum/object that conforms to State
    -This object returns viewControllers for the state that it is currently in.

4. Create an enum/object that conforms to Phase
    -This object can be an extension point for changing test schedule behavior
        based on the date, or study id.

5. (optional) Create a TestSession server request object use this to change the output.
    -Make sure to use this object in test session network requests by
        sub-classing the sessionController object and overriding
        the uploadSession.

6. Create a home view controller

7. Create a contact view controller

8. Create a about view controller

9. Create a help view controller

10. Subclass any Arc controllers that have customizations that need to be made.

11. Define var environment:ArcEnvironment = Environment.dev in app delegate

12. At the top of the app delegate's didFinishLaunchingwithOptions
    -Arc.configureWithEnvironment(environment: environment)
        -Place this declaration on all entry points into the applications.
            (performFetchWithCompletionHandler, userNotificationDidRecieveResponse)

Export:
- Pick from the previously created environments. The environment will handle
    the server that it sends data to.
    - For Production: Use the EXR scheme 
    - For All other QA environments: Use the EXR QA scheme, it will use the appropriate
        target to build firebase into the bundle. 

