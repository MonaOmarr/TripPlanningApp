package com.example.tripplanner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.tripplanner.models.TripTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * PrefsManager
 *
 * A utility class responsible for saving and loading TripTask objects
 * using SharedPreferences. Tasks are serialized to JSON using Gson.
 *
 * Responsibilities:
 *  - Save a full list of tasks (as JSON) into SharedPreferences.
 *  - Load and return the list of tasks from storage.
 *  - Always return a non-null List to avoid crashes.
 *  - Generate unique IDs for new tasks.
 *
 * This class provides the app's basic form of persistent storage.
 */
public class PrefsManager {

    // Name of SharedPreferences file used by the application
    private static final String PREF_NAME = "trip_planning_prefs";

    // Key under which the JSON string of tasks is stored
    private static final String KEY_TASKS = "tasks_json";

    /**
     * Saves a list of TripTask objects into SharedPreferences.
     *
     * @param context application context
     * @param tasks   the list of tasks to save (if null, an empty list is used)
     */
    public static void saveTasks(Context context, List<TripTask> tasks) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }

        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Convert task list to JSON
        Gson gson = new Gson();
        String json = gson.toJson(tasks);

        // Store JSON string
        editor.putString(KEY_TASKS, json);
        editor.apply(); // apply() is asynchronous and recommended for most uses
    }

    /**
     * Loads the list of TripTask objects stored in SharedPreferences.
     *
     * @param context application context
     * @return a List<TripTask> (never null)
     */
    public static List<TripTask> loadTasks(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String json = prefs.getString(KEY_TASKS, null);

        // If no tasks exist yet, return a new empty list
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }

        try {
            // Convert JSON string back to a list of TripTask objects
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<TripTask>>() {}.getType();
            List<TripTask> list = gson.fromJson(json, type);

            // If somehow null (rare), return empty list
            if (list == null) {
                list = new ArrayList<>();
            }
            return list;

        } catch (Exception e) {
            // If corrupted JSON or any parsing error occurs, avoid crashes
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Generates a unique task ID by scanning the existing list
     * and returning (max ID + 1). Ensures each task has a unique identifier.
     *
     * @param context application context
     * @return new unique ID
     */
    public static int generateNewId(Context context) {
        List<TripTask> tasks = loadTasks(context);

        int maxId = 0;

        // Find maximum ID in the existing list
        for (TripTask t : tasks) {
            if (t.getId() > maxId) {
                maxId = t.getId();
            }
        }

        // Return incremented ID
        return maxId + 1;
    }
}
