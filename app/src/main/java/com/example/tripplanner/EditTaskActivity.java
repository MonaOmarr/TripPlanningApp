package com.example.tripplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.tripplanner.databinding.ActivityEditTaskBinding;
import com.example.tripplanner.models.TripTask;
import com.example.tripplanner.utils.PrefsManager;

import java.util.Calendar;
import java.util.List;

/**
 * EditTaskActivity
 *
 * This screen allows the user to:
 *  - View the details of an existing TripTask.
 *  - Edit and update the task information (title, category, date, budget, notes, importance, done).
 *  - Delete the task completely.
 *
 * The task to be edited is identified by an ID passed in the Intent extras ("task_id").
 * All tasks are stored and retrieved using PrefsManager (SharedPreferences + JSON).
 */
public class EditTaskActivity extends AppCompatActivity {

    // ViewBinding for activity_edit_task.xml
    private ActivityEditTaskBinding binding;

    // ID of the task received from the Intent
    private int taskId = -1;

    // Reference to the currently edited task
    private TripTask currentTask;

    // List of all tasks loaded from storage
    private List<TripTask> tasks;

    // Currently selected date in "dd/MM/yyyy" format
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout using ViewBinding
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the task ID from the launching Intent
        taskId = getIntent().getIntExtra("task_id", -1);

        // Load all tasks from SharedPreferences
        tasks = PrefsManager.loadTasks(this);

        // Find the specific task to edit
        findTaskById();
        if (currentTask == null) {
            // If no matching task is found, show message and close the Activity
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fill the form fields with the existing task data
        fillFormWithTask();

        // Set up listeners for date picking, updating, and deleting
        binding.txtDate.setOnClickListener(v -> showDatePicker());
        binding.btnUpdate.setOnClickListener(v -> updateTask());
        binding.btnDelete.setOnClickListener(v -> deleteTask());
    }

    /**
     * Searches the loaded task list for a task whose ID matches taskId
     * and assigns it to currentTask.
     */
    private void findTaskById() {
        if (tasks == null) return;
        for (TripTask t : tasks) {
            if (t.getId() == taskId) {
                currentTask = t;
                break;
            }
        }
    }

    /**
     * Populates all form fields with the data from currentTask:
     *  - Title, budget, notes, date, importance, done state, and category radio buttons.
     */
    private void fillFormWithTask() {
        // Basic text fields
        binding.edtTitle.setText(currentTask.getTitle());
        binding.edtBudget.setText(String.valueOf(currentTask.getBudget()));
        binding.edtNotes.setText(currentTask.getNotes());

        // Date
        binding.txtDate.setText(currentTask.getDate());
        selectedDate = currentTask.getDate();

        // Switches / checkboxes
        binding.switchImportant.setChecked(currentTask.isImportant());
        binding.chkDone.setChecked(currentTask.isDone());

        // Radio buttons for category based on string value
        switch (currentTask.getCategory()) {
            case "Flight":
                binding.radioGroupCategory.check(binding.radioFlight.getId());
                break;
            case "Hotel":
                binding.radioGroupCategory.check(binding.radioHotel.getId());
                break;
            case "Packing":
                binding.radioGroupCategory.check(binding.radioPacking.getId());
                break;
            default:
                // If category is not one of the known ones, clear selection
                binding.radioGroupCategory.clearCheck();
                break;
        }
    }

    /**
     * Opens a DatePickerDialog allowing the user to select a new date.
     * The selected date is stored in selectedDate and shown in txtDate.
     */
    private void showDatePicker() {
        // Use the current date as the default in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (DatePicker view, int year1, int month1, int dayOfMonth) -> {
                    // Month is 0-based in Calendar, so we add 1
                    selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                    binding.txtDate.setText(selectedDate);
                },
                year,
                month,
                day
        );

        dp.show();
    }

    /**
     * Reads the form fields, validates the input, updates the currentTask object,
     * saves the task list back to SharedPreferences, and closes the Activity.
     */
    private void updateTask() {
        // Read user input from fields
        String title = binding.edtTitle.getText().toString().trim();
        String budgetStr = binding.edtBudget.getText().toString().trim();
        String notes = binding.edtNotes.getText().toString().trim();
        boolean important = binding.switchImportant.isChecked();
        boolean done = binding.chkDone.isChecked();

        // Validate title
        if (TextUtils.isEmpty(title)) {
            binding.edtTitle.setError("Title required");
            return;
        }

        // Validate date selection
        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse budget (optional, defaults to 0.0)
        double budget = 0.0;
        if (!TextUtils.isEmpty(budgetStr)) {
            try {
                budget = Double.parseDouble(budgetStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid budget", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Determine category based on the selected radio button
        String category = "Other";
        int checkedId = binding.radioGroupCategory.getCheckedRadioButtonId();
        if (checkedId == binding.radioFlight.getId()) {
            category = "Flight";
        } else if (checkedId == binding.radioHotel.getId()) {
            category = "Hotel";
        } else if (checkedId == binding.radioPacking.getId()) {
            category = "Packing";
        }

        // Update the currentTask object with the new values
        currentTask.setTitle(title);
        currentTask.setBudget(budget);
        currentTask.setNotes(notes);
        currentTask.setImportant(important);
        currentTask.setDone(done);
        currentTask.setDate(selectedDate);
        currentTask.setCategory(category);

        // Save the whole list back to SharedPreferences
        PrefsManager.saveTasks(this, tasks);

        Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();

        // Close this screen and return to MainActivity
        finish();
    }

    /**
     * Deletes the currentTask from the list, saves the updated list,
     * shows a confirmation message, and closes the Activity.
     */
    private void deleteTask() {
        // Remove current task from in-memory list
        tasks.remove(currentTask);

        // Persist changes
        PrefsManager.saveTasks(this, tasks);

        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();

        // Return to previous screen
        finish();
    }
}
