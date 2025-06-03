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
