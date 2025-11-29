package com.example.tripplanner.models;

/**
 * TripTask
 *
 * This model class represents a single task inside the Trip Planning App.
 * It stores all task-related information and provides getters/setters for use
 * within Activities, Adapters, and when saving/loading data via PrefsManager.
 *
 * Fields:
 *  - id         : unique identifier for the task
 *  - title      : short title describing the task
 *  - category   : category type (Flight / Hotel / Packing / Other)
 *  - date       : deadline or scheduled date in "dd/MM/yyyy" format
 *  - budget     : estimated cost or effort related to the task
 *  - important  : whether the task is marked as important by the user
 *  - done       : whether the task has already been completed
 *  - notes      : optional additional details
 */
public class TripTask {

    // Unique numeric ID for identifying this task
    private int id;

    // Short descriptive name of the task
    private String title;

    // Task category (e.g., Flight, Hotel, Packing)
    private String category;

    // Task date in "dd/MM/yyyy" format
    private String date;

    // Optional budget amount for the task
    private double budget;

    // Whether the task is marked as important
    private boolean important;

    // Whether the task has been completed
    private boolean done;

    // Optional extended notes or description
    private String notes;

    /**
     * Full constructor used when creating or loading a complete TripTask object.
     */
    public TripTask(int id, String title, String category, String date,
                    double budget, boolean important, boolean done, String notes) {

        this.id = id;
        this.title = title;
        this.category = category;
        this.date = date;
        this.budget = budget;
        this.important = important;
        this.done = done;
        this.notes = notes;
    }

    /**
     * Empty constructor (required for frameworks like Gson during deserialization).
     */
    public TripTask() {}

    // ------------------- Getters & Setters -------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public boolean isImportant() { return important; }
    public void setImportant(boolean important) { this.important = important; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
