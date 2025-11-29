package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import com.example.tripplanner.adapters.TripTaskAdapter;
import com.example.tripplanner.databinding.ActivityMainBinding;
import com.example.tripplanner.models.TripTask;
import com.example.tripplanner.utils.PrefsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity
 *
 * This is the main screen of the Trip Planning App.
 * Responsibilities:
 * - Load the saved list of trip tasks from SharedPreferences (via PrefsManager).
 * - Display tasks in a RecyclerView using TripTaskAdapter.
 * - Allow the user to search/filter tasks with a SearchView.
 * - Navigate to AddTaskActivity to create new tasks.
 * - Navigate to EditTaskActivity to edit an existing task when clicked.
 * - Show a confirmation dialog and delete a task on long click.
 */
public class MainActivity extends AppCompatActivity {

    // ViewBinding for activity_main.xml
    private ActivityMainBinding binding;

    // Adapter and in-memory list of tasks
    private TripTaskAdapter adapter;
    private List<TripTask> tasks = new ArrayList<>();

    // Key used to save/restore the current search query during configuration changes
    private static final String KEY_SEARCH = "search_query";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout using ViewBinding instead of findViewById
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Restore previously typed search query (for example after rotation)
        if (savedInstanceState != null) {
            currentSearchQuery = savedInstanceState.getString(KEY_SEARCH, "");
        }

        // Load tasks from persistent storage and initialize UI components
        loadTasksFromPrefs();
        setupRecyclerView();
        setupFab();
        setupSearchView();
    }

    // ---------------- Helper methods ----------------

    /**
     * Loads the list of TripTask objects from SharedPreferences using PrefsManager.
     * If loading fails (e.g. corrupted JSON), an empty list is created to avoid crashes.
     */
    private void loadTasksFromPrefs() {
        try {
            List<TripTask> loaded = PrefsManager.loadTasks(this);
            if (loaded == null) {
                // No saved tasks yet
                tasks = new ArrayList<>();
            } else {
                tasks = loaded;
            }
        } catch (Exception e) {
            // In case of corrupted data or other unexpected error, use empty list
            tasks = new ArrayList<>();
            e.printStackTrace();
        }
    }

    /**
     * Sets up the RecyclerView with a LinearLayoutManager and attaches
     * the TripTaskAdapter. Also defines item click and long-click behavior.
     */
    private void setupRecyclerView() {
        adapter = new TripTaskAdapter(this, tasks, new TripTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TripTask task) {
                // Open EditTaskActivity to edit the selected task
                Intent i = new Intent(MainActivity.this, EditTaskActivity.class);
                i.putExtra("task_id", task.getId());
                startActivity(i);
            }

            @Override
            public void onItemLongClick(TripTask task, int position) {
                // Show a confirmation dialog before deleting the task
                showDeleteDialog(task, position);
            }
        });

        // Use a vertical list layout for the RecyclerView
        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerTasks.setAdapter(adapter);
    }

    /**
     * Configures the FloatingActionButton to open AddTaskActivity
     * when the user wants to create a new task.
     */
    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(i);
        });
    }

    /**
     * Sets up the SearchView to filter the list of tasks in real time
     * as the user types. Also reapplies any restored query after rotation.
     */
    private void setupSearchView() {
        // Apply previously restored search query, if any
        if (!currentSearchQuery.isEmpty() && adapter != null) {
            adapter.filter(currentSearchQuery);
        }

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Save current search and apply filter when user submits the query
                currentSearchQuery = query;
                if (adapter != null) {
                    adapter.filter(query);
                }
                return true; // We handled the event
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Save current search and filter dynamically as user types
                currentSearchQuery = newText;
                if (adapter != null) {
                    adapter.filter(newText);
                }
                return true;
            }
        });
    }

    /**
     * Shows a confirmation dialog before deleting a task.
     * If the user confirms, the task is removed from the list,
     * saved to SharedPreferences and the adapter is refreshed.
     *
     * @param task     the task to be deleted (used to show its title)
     * @param position the position of the task in the RecyclerView list
     */
    private void showDeleteDialog(TripTask task, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete task")
                .setMessage("Are you sure you want to delete \"" + task.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Extra safety check for valid index
                    if (position >= 0 && position < tasks.size()) {
                        // Remove from in-memory list
                        tasks.remove(position);
                        // Persist updated list
                        PrefsManager.saveTasks(MainActivity.this, tasks);
                        // Update adapter's data and re-apply current filter
                        adapter.updateList(tasks);
                        adapter.filter(currentSearchQuery);
                    }
                })
                .setNegativeButton("Cancel", null) // Do nothing if user cancels
                .show();
    }

    // ---------------- Lifecycle ----------------

    /**
     * onResume is called when returning to this activity (e.g. after
     * adding or editing a task). We reload tasks in case they changed
     * and refresh the RecyclerView with the latest data.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Reload tasks in case something changed in Add/Edit activities
        loadTasksFromPrefs();
        if (adapter != null) {
            adapter.updateList(tasks);
            adapter.filter(currentSearchQuery);
        }
    }

    /**
     * Saves the current search query so that it can be restored
     * after configuration changes (like screen rotation).
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_SEARCH, currentSearchQuery);
        super.onSaveInstanceState(outState);
    }
}
