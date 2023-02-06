## How long did you spend on the coding test? What would you add to your solution if you had more time? If you didn't spend much time on the coding test then use this as an opportunity to explain what you would add.
I spent about 4/5 hours on the test. If I had more time I would like to increase the test coverage particularly for the DetectOutcode Usecase. I was pushed for time and feel it could be written in a way that makes it more
testable in general. Additionally some Compose UI tests would be needed.

I also feel like the api error handling would need to be improved. Currently all failures trigger the same snackbar and these could provide more context. Taking into account the response code/error body could
allow for more relevant errors presented to the user.

It could be beneficial to provide some client side validation on the postcode input. i.e ensure all inputs are alphanumeric and of a maximum length - perhaps a postcode format regex should be used. 

In general this app could also be improved with more advanced features such as filters for restaurants based on cuisine type etc. and potentially pagination system to load more restaurants as the user scrolls.

## What was the most useful feature that was added to the latest version of your chosen language? Please include a snippet of code that shows how you've used it.
The example I can provide is not quite a cutting edge as Kotlin 1.8.0, but in versions beyond 1.6.0 non-exhaustive when statements became errors. I like using sealed classes and
in combination with non-exhaustive when statements the potential for developer error is lessened

```
private fun handleAction(action: RestaurantSearchAction) {
when (action) {
is RestaurantSearchAction.OnAutoDetectPostcode -> {
viewModelScope.launch(Dispatchers.IO) {
detectOutCode(
onSuccess = { outCode ->
searchRestaurants(outCode)
_state.value = state.value.copy(currentSearchValue = outCode)
},
onFailure = {
RestaurantSearchEvent.ShowSnackBarEvent(
UiText.StringResource(R.string.search_error_message)
)
}
)
}
}
RestaurantSearchAction.OnPermissionsDenied -> {
viewModelScope.launch {
_restaurantSearchEvents.send(
RestaurantSearchEvent.ShowSnackBarEvent(
UiText.StringResource(R.string.permission_denied_message)
)
)
}
}
RestaurantSearchAction.OnShowPermissionRationale -> {
viewModelScope.launch {
_restaurantSearchEvents.send(
RestaurantSearchEvent.ShowPermissionRationale(
UiText.StringResource(R.string.permission_rationale_message),
UiText.StringResource(R.string.permission_rationale_action_label)
)
)
}
}
is RestaurantSearchAction.OnSearchByPostcode -> {
searchRestaurants(action.postcode)
}
is RestaurantSearchAction.OnSearchValueUpdated -> {
_state.value = state.value.copy(currentSearchValue = action.value)
}
}
}
```

Here any RestaurantSearchAction types added will be required to be implemented in the handleAction() function

## How would you track down a performance issue in production? Have you ever had to do this?

Monitor the performance metrics of the app using tools like Android Profiler or Firebase Performance Monitoring.
Identify the specific areas of the code that are causing performance issues using stack traces and CPU/Memory usage data.
Test the performance in different scenarios and on different devices to get a better understanding of the issue.
Refactor the code to improve performance and optimize resource usage.


## How would you improve the Just Eat APIs that you just used?

Add support for pagination to allow for more efficient retrieval of large amounts of data.
Add support for filtering based on different criteria to allow for more specific and relevant results.