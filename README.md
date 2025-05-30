This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `commonMain`. If preferred, you can add code to the platform-specific folders here too.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…# pagen

## Running the Application

### Android
1. Open the project in Android Studio
2. Select the `composeApp` module in the run configuration
3. Click Run to build and deploy to an Android device/emulator

### iOS
1. Open the `iosApp` folder in Xcode
2. Select your target device/simulator
3. Click Run to build and deploy

### Web (if applicable)
1. Run `./gradlew composeApp:jsBrowserDevelopmentRun`
2. Open http://localhost:8080 in your browser

## Running Tests

### Unit Tests
Run all tests:
```bash
./gradlew test
```


### iOS Tests
1. Open the `iosApp` folder in Xcode
2. Select the test target
3. Click Run Tests

## Development Setup

1. Install JDK 17+
2. Install Android Studio (for Android development)
3. Install Xcode (for iOS development)

## Demo
### iOS
https://github.com/user-attachments/assets/9c76dbb3-a10f-4507-97a7-f60ff5a3a098

 
### Android

https://github.com/user-attachments/assets/7c422589-739c-407e-9be3-91046a03e7e9


