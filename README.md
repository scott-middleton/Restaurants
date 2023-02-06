## Implementation
The application follows an MVVM, clean architecture pattern and uses Hilt for dependency injection. Navigation and layouts are handled with Jetpack Compose. The restaurants are fetched from the Just Eat API using Retrofit.

ViewModels hold the state of the data and handle the logic of the user interface, while the view is responsible for displaying the data and handling user interactions. Communication between the view and the ViewModels is done through events and actions. 
The ViewModels also have the ability to emit events that the view can listen for and respond to, allowing for a decoupled relationship between the two. This approach allows for a clear separation of concerns, making the code more maintainable and easier to test. 
Additionally, this approach works well when using Hilt dependency injection as it allows the dependencies to be injected in the ViewModels. This way, the view is not responsible for managing the dependencies, which makes the code more modular and easier to test, as the view and ViewModel can be tested seperately.

## Testing
I provided an example of testing approaches, rather than comprehensive coverage of the enitire app. Specifically, coverage for the Repository, UseCase and ViewModel layers of the Restaurants Search feature, where I use a combination of fakes and mocks.

Additionally I set up the mock web server to simulate API calls made by the repository, and assert that the repository correctly handles the responses from the API. I also used the library turbine to test the Flow stream used in the ViewModel layer.
