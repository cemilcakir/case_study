# People Listing App

A simple Android application that displays a list of people using the MVVM architecture pattern, Kotlin, Coroutines, Hilt for dependency injection, and the repository pattern.

## Features

- Fetches a list of people from a data source
- Displays the list of people on the screen

## Architecture

This project follows the MVVM (Model-View-ViewModel) architecture pattern. The different components of the architecture are:

- **Model**: Represents the data and business logic of the application.
- **View**: Responsible for displaying the user interface and capturing user interactions.
- **ViewModel**: Acts as an intermediary between the View and the Model, handling data operations and exposing data to the View.
- **Repository**: Manages the data operations, abstracting the data sources (local or remote) from the ViewModel.

## Tech Stack

The following technologies and libraries are used in this project:

- Kotlin: The programming language used for developing the Android application.
- Coroutines: Used for managing asynchronous operations and background tasks.
- Hilt: A dependency injection library for managing dependencies in the application.
- StateFlow: A state holder for managing and emitting values asynchronously.
- RecyclerView: Used for displaying the list of people in a scrollable and efficient manner.

## Installation

To run the project locally, follow these steps:

1. Clone the repository: `git clone https://github.com/cemilcakir/case_study.git`
2. Open the project in Android Studio.
3. Build and run the application on an emulator or a physical device.

Make sure you have the latest version of Android Studio and the Android SDK installed.