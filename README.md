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
Link to calculateWeeklyChange: [calculate Weekly Change](https://github.com/rramoscode/rramoscode.github.io/pull/3).

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

### Enhancement Three: Database

Implement a new ‘goal_history’ table to track every time a goal is set and when it is achieved. Display a historical list of goal attempts and outcomes.

## Table Structure: 

CREATE TABLE goal_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    goal_weight REAL,
    set_date TEXT,
    achieved_date TEXT
);

Link to goal_history: [Goal History](https://github.com/rramoscode/rramoscode.github.io/pull/4).

### Goal History Table

The application's database has been enhanced to provide more robust user data management and tracking capabilities. This includes dedicated tables for user accounts, daily weight logs, and goal weights. A new goal_history table was added to specifically track user goals, logging each new goal set and marking when it's achieved.

This new structure is implemented in the DatabaseHelper.java file:

```java
// Simplified onCreate method showing table creation
public void onCreate(SQLiteDatabase db) {
    // ... (details are in the source file)
}

// Simplified onUpgrade method for database schema changes
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // ... (details are in the source file)
}
```

### Goal Achievement Tracking

As part of the database enhancements, the application now actively tracks when a user achieves their set weight goal. When a user reaches their goal, the achievement date is recorded in the goal_history table, providing a valuable historical record of their progress.

This is managed by a dedicated method in DataDisplayActivity.java:

```java
// Method to update the achievement date when a goal is met
private void updateGoalAchievementDate() {
    // ... (details are in the source file)
}
```
