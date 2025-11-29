package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.tripplanner.databinding.ActivityAddTaskBinding;
import com.example.tripplanner.models.TripTask;
import com.example.tripplanner.utils.PrefsManager;

import java.util.Calendar;
import java.util.List;

/**
 * AddTaskActivity
 *
 * This screen allows the user to create a new TripTask.
 * Responsibilities:
 *  - Collect task details from the user (title, category, date, budget, notes, importance, done).
 *  - Validate the input (required title and date, valid budget).
 *  - Generate a new unique ID for the task.
 *  - Save the new task in persistent storage using PrefsManager.
 *  - Restore the selected date if the screen is rotated.
 */
public class AddTaskActivity extends AppCompatActivity {

    // ViewBinding for activity_add_task.xml
    private ActivityAddTaskBinding binding;

    // Stores the currently selected date in "dd/MM/yyyy" format
    private String selectedDate = "";

    // Key used to save/restore the selected date across configuration changes
    private static final String KEY_DATE = "form_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Restore previously selected date after configuration change (e.g. rotation)
        if (savedInstanceState != null) {
            selectedDate = savedInstanceState.getString(KEY_DATE, "");
            binding.txtDate.setText(selectedDate);
        }

        // Open the DatePicker when the date TextView is clicked
        binding.txtDate.setOnClickListener(v -> showDatePicker());

        // Save the task when the Save button is clicked
        binding.btnSave.setOnClickListener(v -> saveTask());
    }

    /**
     * Displays a DatePickerDialog to let the user select a date.
     * The chosen date is stored in selectedDate and shown in txtDate.
     */
    private void showDatePicker() {
        // Get the current date as default for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (DatePicker view, int year1, int month1, int dayOfMonth) -> {
                    // Month is 0-based (0 = January), so we add 1
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
     * Reads all input fields, validates them, creates a new TripTask,
     * saves it to SharedPreferences via PrefsManager, and finishes the Activity.
     */
    private void saveTask() {
        // Read values from the form
        String title = binding.edtTitle.getText().toString().trim();
        String budgetStr = binding.edtBudget.getText().toString().trim();
        String notes = binding.edtNotes.getText().toString().trim();
        boolean important = binding.switchImportant.isChecked();
        boolean done = binding.chkDone.isChecked();

        // Validate title (required)
        if (TextUtils.isEmpty(title)) {
            binding.edtTitle.setError("Title is required");
            return;
        }

        // Ensure a date has been selected
        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse budget (optional, default is 0.0 if empty)
        double budget = 0.0;
        if (!TextUtils.isEmpty(budgetStr)) {
            try {
                budget = Double.parseDouble(budgetStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid budget", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Determine category based on selected radio button
        String category = "Other";
        int checkedId = binding.radioGroupCategory.getCheckedRadioButtonId();
        if (checkedId == binding.radioFlight.getId()) {
            category = "Flight";
        } else if (checkedId == binding.radioHotel.getId()) {
            category = "Hotel";
        } else if (checkedId == binding.radioPacking.getId()) {
            category = "Packing";
        }

        // Generate a new unique ID for this task
        int newId = PrefsManager.generateNewId(this);

        // Create the new TripTask object
        TripTask task = new TripTask(
                newId,
                title,
                category,
                selectedDate,
                budget,
                important,
                done,
                notes
        );

        // Load existing tasks, append the new one, and save the list back
        List<TripTask> tasks = PrefsManager.loadTasks(this);
        tasks.add(task);
        PrefsManager.saveTasks(this, tasks);

        Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();

        // Close this screen and return to MainActivity
        finish();
    }

    /**
     * Saves the currently selected date into the outState Bundle
     * so it can be restored after configuration changes.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_DATE, selectedDate);
        super.onSaveInstanceState(outState);
    }
}
