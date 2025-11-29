package com.example.tripplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.R;
import com.example.tripplanner.models.TripTask;

import java.util.ArrayList;
import java.util.List;

/**
 * TripTaskAdapter
 *
 * This adapter connects the list of TripTask objects to the RecyclerView.
 * Responsibilities:
 *  - Create ViewHolder instances for each list item.
 *  - Bind TripTask data into each ViewHolder.
 *  - Handle click and long-click events for each item.
 *  - Support dynamic search filtering using a full backup list.
 */
public class TripTaskAdapter extends RecyclerView.Adapter<TripTaskAdapter.TaskViewHolder> {

    /**
     * Listener interface for item click and long-press actions.
     * Implemented in MainActivity.
     */
    public interface OnItemClickListener {
        void onItemClick(TripTask task);
        void onItemLongClick(TripTask task, int position);
    }

    private final Context context;

    // The list currently displayed in the RecyclerView
    private List<TripTask> tasks;

    // A full backup of the list used for search filtering
    private List<TripTask> tasksFull;

    private final OnItemClickListener listener;

    /**
     * Constructor initializes the adapter with context, task list, and event listener.
     */
    public TripTaskAdapter(Context context, List<TripTask> tasks, OnItemClickListener listener) {
        this.context = context;
        this.tasks = tasks != null ? tasks : new ArrayList<>();

        // tasksFull must be a complete independent copy
        this.tasksFull = new ArrayList<>(this.tasks);

        this.listener = listener;
    }

    /**
     * Updates the entire data list (used after adding/editing/deleting tasks).
     */
    public void updateList(List<TripTask> newList) {
        this.tasks = newList != null ? newList : new ArrayList<>();
        this.tasksFull = new ArrayList<>(this.tasks);
        notifyDataSetChanged();
    }

    /**
     * Inflates the item_trip_task layout and creates a ViewHolder.
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_trip_task, parent, false);
        return new TaskViewHolder(v);
    }

    /**
     * Binds data from a TripTask object to the ViewHolder UI elements.
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TripTask task = tasks.get(position);

        holder.txtTitle.setText(task.getTitle());
        holder.txtCategory.setText(task.getCategory());
        holder.txtDate.setText(task.getDate());
        holder.chkDone.setChecked(task.isDone());

        // Show IMPORTANT tag only if marked important
        holder.txtImportant.setVisibility(task.isImportant() ? View.VISIBLE : View.GONE);

        // Icon (generic app icon used here to avoid missing-drawable crashes)
        holder.imgIcon.setImageResource(R.mipmap.ic_launcher);

        // Single click → edit task
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(task);
        });

        // Long click → delete task (handled in MainActivity)
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onItemLongClick(task, position);
            return true; // consume the long-click event
        });
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    /**
     * Filters the list based on the user’s query.
     * Matches title, category, or date (case-insensitive).
     */
    public void filter(String query) {
        if (tasksFull == null) tasksFull = new ArrayList<>();

        query = (query == null) ? "" : query.toLowerCase().trim();

        List<TripTask> filtered = new ArrayList<>();

        // If search is empty → show entire list
        if (query.isEmpty()) {
            filtered.addAll(tasksFull);
        } else {
            // Otherwise, apply filtering logic
            for (TripTask t : tasksFull) {
                if (t.getTitle().toLowerCase().contains(query) ||
                        t.getCategory().toLowerCase().contains(query) ||
                        t.getDate().toLowerCase().contains(query)) {

                    filtered.add(t);
                }
            }
        }

        // Replace the displayed list and refresh UI
        tasks = filtered;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class holds references to each UI component in the row layout.
     */
    static class TaskViewHolder extends RecyclerView.ViewHolder {

        ImageView imgIcon;
        TextView txtTitle, txtCategory, txtDate, txtImportant;
        CheckBox chkDone;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtImportant = itemView.findViewById(R.id.txtImportant);
            chkDone = itemView.findViewById(R.id.chkDone);
        }
    }
}
