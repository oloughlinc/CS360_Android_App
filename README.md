# CS360_FinalProject

# What user needs was this app designed to address?

This app is an event tracker in which users can enter new event titles, descriptions, dates, and priorities. They can then view these events and a grid and be notified of events on the day they are set to occur.

# What screens and features were necessary to support user needs and produce a user-centered UI for the app? How did your UI designs keep users in mind? Why were your designs successful?

This app has two activities, a login and an event grid. The event grid also inflates and shows a UI fragment that handles entering or updating an event. The UI is designed to be simple, consistent, and streamlined. Everything the user needs to do is available directly from the event grid activity.

# How did you approach the process of coding your app? What techniques or strategies did you use? How could those be applied in the future?

The design started with an overview of the application requirements. The user needs were investigated and research was done on similar, successful apps in the Android marketplace. After this, a UI design was put together on paper along with a plan for the app design architecture, including how the database and activities interact with each other. Finally, the UI was built in Android Studio and the backing controller and model we completed.

This design strategy is an effective way to scope the project before coding starts as well as investigating the design itself to ensure a quality product. 

# How did you test to ensure your code was functional? Why is this process important and what did it reveal?

UI testing was done on a mobile emulator running in Android Studio. This allowed us to test directly on the working UI with supporting code and see how the app reacts in real time. Another effective testing strategy (which was not implemented here) is including unit tests on the model and controller to ensure functionality of these components before a UI is attached.  

# Considering the full app design and development process, from initial planning to finalization, where did you have to innovate to overcome a challenge?

This was my first app. I don't believe there is much particular innovation, but the entire process was well documented start to finish and this helped to meet the challenge of creating this piece of software as a solo developer. 

# In what specific component from your mobile app were you particularly successful in demonstrating your knowledge, skills, and experience?

The functionality I am happiest with the outcome of is the event grid. Items are organized in a very clean way and the edit and delete buttons are interactable and hidden from view until an event is selected. This took a bit more code in the controller to accomplish but the results were successful. 
