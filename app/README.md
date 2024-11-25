# Firebase Auth Demo App

## Overview

The Firebase Auth Demo App is a mobile application built using Jetpack Compose that allows users to report lost items and manage found items. The app integrates with Firebase for authentication and Firestore for data storage, providing a seamless experience for users to track and manage lost and found items.

## Features

- User authentication using Firebase Authentication
- Reporting lost items with details such as name, category, description, and location
- Uploading images of lost items
- Viewing and managing found items
- Filtering and searching for items
- Responsive UI built with Jetpack Compose
- Location services integration for reporting item locations

## Technologies Used

- **Kotlin**: The primary programming language for Android development.
- **Jetpack Compose**: Modern toolkit for building native UI in Android.
- **Firebase**: Backend services for authentication and data storage.
- **Coil**: Image loading library for Android.
- **Material Design**: UI components and design principles for a modern look and feel.

## Getting Started

### Prerequisites

- Android Studio (Arctic Fox or later)
- Kotlin 1.5 or later
- An active Firebase project

### Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/LancemDev/LostLink.git
   cd LostLink
   ```

2. **Open the project in Android Studio**.

3. **Configure Firebase**:
    - Go to the [Firebase Console](https://console.firebase.google.com/).
    - Create a new project or use an existing one.
    - Add an Android app to your Firebase project and follow the setup instructions.
    - Download the `google-services.json` file and place it in the `app/` directory of your project.

4. **Add dependencies**:
   Ensure that the following dependencies are included in your `build.gradle` files:

   ```groovy
   // Project-level build.gradle
   buildscript {
       dependencies {
           classpath 'com.google.gms:google-services:4.3.10' // Check for the latest version
       }
   }

   // App-level build.gradle
   dependencies {
       implementation 'com.google.firebase:firebase-auth-ktx:21.0.1' // Check for the latest version
       implementation 'com.google.firebase:firebase-firestore-ktx:24.0.1' // Check for the latest version
       implementation 'androidx.compose.ui:ui:1.0.5' // Check for the latest version
       implementation 'androidx.compose.material3:material3:1.0.0' // Check for the latest version
       implementation 'io.coil-kt:coil-compose:2.1.0' // Check for the latest version
   }
   ```

5. **Sync the project** to download the dependencies.

### Running the App

1. Connect your Android device or start an emulator.
2. Run the app from Android Studio.

## Usage

- **Authentication**: Users can sign up and log in using their email and password.
- **Report Lost Item**: Users can fill out a form to report a lost item, including uploading an image.
- **View Found Items**: Users can view a list of found items and their details.
- **Search and Filter**: Users can search for items by name and filter by category.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Thanks to the Firebase team for providing excellent backend services.
- Thanks to the Jetpack Compose team for creating a modern UI toolkit for Android development.