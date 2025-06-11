ePortfolio

# NuMe Project

![NuMe Icon](images/NuMe_app Small.jpeg)


### Code Review and planned enhancement for the NuMe Application
<iframe width="560" height="315" src="https://www.youtube.com/embed/njhcB8VI0jM?si=W7s5Q8Ngxx2LQSpw" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>




### Enhancement One:
Link to the TextWatcher: [TextWatcher](https://github.com/rramoscode/rramoscode.github.io/pull/2/files).

### Goal Weight Input

Users can set a goal weight in the app. As they type, the app provides real-time feedback on whether their goal is **aggressive**, **moderate**, or **above their current weight**.

This feature is implemented using a `TextWatcher` on the input field:

```java
// Simplified example of how the goal weight input is handled
mGoalWeightInput.addTextChangedListener(new TextWatcher() {
    @Override
    public void afterTextChanged(Editable s) {
        // Logic to compare goal weight with current weight and display feedback
        // ... (details are in the source file)
    }
    // other TextWatcher methods omitted for brevity
});
```

### Enhancement Two:
Link to calculateWeeklyChange: [Place Holder](https://github.com/rramoscode/rramoscode.github.io).

### Weekly Weight Change Calculation

The app provides users with a clear summary of their **weekly weight change**. This feature calculates the difference between the user's current weight and their average weight over the past seven days, offering quick insight into their progress.

This is handled by querying the local database for historical weight entries:

```java
// Simplified example of how weekly change is calculated
private void calculateWeeklyChange(double currentWeight) {
    // Logic to retrieve past 7 days of weight data and calculate average
    // ... (details are in the source file)
}
```
