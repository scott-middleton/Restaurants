## How long did you spend on the coding test? What would you add to your solution if you had more time? If you didn't spend much time on the coding test then use this as an opportunity to explain what you would add.

I spent approximately 4 to 5 hours on the coding test. If I had more time, I would focus on the following areas for improvement:

* Increase test coverage. The current test coverage only provides an example of testing approaches and is not considered sufficient for a released application.
* Refactor the DetectOutcode UseCase to make it more testable. I was somewhat stretched for time and with more time I would like to fully test
and potentially improve this implementation in general.
* Add UI tests to ensure the user interface is functioning as expected.
* Improve error handling for API calls by presenting more relevant errors to the user based on the response code or error body.
* Implement client-side validation for postcode inputs, such as ensuring the inputs are alphanumeric and of a specified maximum length, or using a postcode format regex.
* Add advanced features such as filters for restaurants based on name, cuisine type, etc, and a pagination system for loading more restaurants as the user scrolls.
* Consider requesting the ACCESS_FINE_LOCATION permission if the current coarse location is deemed insufficiently accurate.

## What was the most useful feature that was added to the latest version of your chosen language? Please include a snippet of code that shows how you've used it.

Although not the latest, one of the most useful features in recent versions of Kotlin is the enforcement of non-exhaustive when statements as errors. 
I have utilized this feature in my code to reduce the potential for developer error. Here is a snippet of code that demonstrates its usage:

```
private fun handleAction(action: RestaurantSearchAction) {
        when (action) {
            is RestaurantSearchAction.OnAutoDetectPostcode -> {
                viewModelScope.launch(Dispatchers.IO) {
                    detectOutCode(onSuccess = { outCode ->
                        searchRestaurants(outCode)
                        _state.value = state.value.copy(currentSearchValue = outCode)
                    }, onFailure = {
                        RestaurantSearchEvent.ShowSnackBarEvent(
                            UiText.StringResource(
                                R.string.search_error_message
                            )
                        )
                    })
                }
            }
            RestaurantSearchAction.OnPermissionsDenied -> {
                viewModelScope.launch {
                    _restaurantSearchEvents.send(
                        RestaurantSearchEvent.ShowSnackBarEvent(
                            UiText.StringResource(
                                R.string.permission_denied_message
                            )
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

With the non-exhaustive when statement, the developer is now required to handle all the RestaurantSearchAction 
types in the handleAction() function, helping reduce the potential for errors.

## How would you track down a performance issue in production? Have you ever had to do this?

* Monitor the performance metrics of the app using tools like Android Profiler or Firebase Performance
Monitoring.
* Identify the specific areas of the code that are causing performance issues using stack traces and
CPU/Memory usage data.
* Test the performance in different scenarios and on different devices to get a better understanding
of the issue.

In this specific application it would seem likely that performance issues could by a by-product of 
parsing, mapping and rendering the restaurants list returned from the api, which could contain a large set of results.
This specific case could be mitigated in some of the suggestions made in the API improvements question below.

## How would you improve the Just Eat APIs that you just used?

* Implement pagination support to effectively retrieve large amounts of data.
* Introduce filtering options based on various criteria to provide more targeted and relevant results, such as filtering restaurants that are open.
* Create a minimal endpoint that only comprises the necessary fields relevant to the list items shown in this feature, optimizing the data retrieval process.