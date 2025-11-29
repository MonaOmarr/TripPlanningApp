#📌 Trip Planning App – Android Project

A simple and user-friendly Android application that allows users to create, manage, and organize trip-related tasks. The app supports adding tasks, editing them, deleting them, and searching through all saved tasks.
All task data is stored locally using SharedPreferences with Gson for JSON serialization.

✨ Features

➕ Add new trip tasks

✏️ Edit existing tasks

❌ Delete tasks with confirmation dialog

🔍 Search tasks instantly (by title, category, or date)

⭐ Mark tasks as important

✔ Mark tasks as done

🗂 Categories: Flight, Hotel, Packing, Other

📅 Date selection using DatePickerDialog

💾 Persistent storage using SharedPreferences

🎨 Clean UI using RecyclerView + CardView

📱 Screens & UI Components

MainActivity
Displays all tasks, search bar, and the FloatingActionButton.

AddTaskActivity
Used to create a new task with category, date, notes, and budget.

EditTaskActivity
Used to update or delete an existing task.

RecyclerView + CardView
For efficient and modern task listing.

🛠️ Technologies Used

Java

Android SDK

RecyclerView & ViewHolder Pattern

ViewBinding

Material Design Components (FAB, CardView)

SharedPreferences + Gson

DatePickerDialog

📦 Project Structure
com.example.tripplanner
│
├── MainActivity.java
├── AddTaskActivity.java
├── EditTaskActivity.java
│
├── adapters/
│ └── TripTaskAdapter.java
│
├── models/
│ └── TripTask.java
│
├── utils/
│ └── PrefsManager.java
│
└── res/
├── layout/
├── drawable/
├── mipmap/
└── values/

🔧 Data Storage

The app uses:

SharedPreferences to store all tasks

Gson to convert the list into JSON

Auto-generated IDs for new tasks

No external database required.

📅 Future Improvements

Replace SharedPreferences with Room Database

Add notifications/reminders

Add sorting (by date, category, or importance)

Add cloud sync

Add trip categories and multiple trip lists

📄 License

You can add one of these depending on what you want:

MIT

Apache 2.0

None (private)
