# TestApp - Android UI Experiment

## Overview
This project is an experiment in building a modern Android application using standard Jetpack libraries. The focus is on implementing a clean, responsive UI using `ConstraintLayout` and ensuring a seamless visual experience with `Edge-to-Edge` display support.

## Concept & Technology
The application demonstrates the following core Android development concepts:

*   **Jetpack Compose & Interoperability**: While the project uses XML layouts, it is built with modern Jetpack libraries (`Activity-KTX`, `AppCompat`, `ConstraintLayout`).
*   **Edge-to-Edge Display**: Utilizing `enableEdgeToEdge()` and `WindowInsetsCompat` to draw behind system bars for a truly immersive experience.
*   **ConstraintLayout Chains**: Implementing a vertical "packed" chain to center multiple UI elements perfectly on the screen.
*   **R8 Optimization**: Configuration of custom keep rules in a dedicated `keepRules` directory.
*   **Kotlin First**: Clean implementation using Kotlin for activity logic.

## Scenario
The application displays a simple user profile screen. This scenario demonstrates how to:
1.  Center a group of text elements vertically.
2.  Maintain hierarchy and spacing using ConstraintLayout attributes.
3.  Handle system insets (Status Bar and Navigation Bar) dynamically.

## Project Structure
The project follows a standard Android structure with a specific organization for keep rules:

```text
TestApp/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/example/testapp/
│   │       │   └── MainActivity.kt        # Core logic & Edge-to-Edge setup
│   │       ├── keepRules/
│   │       │   └── rules.keep             # Custom R8/ProGuard rules
│   │       └── res/
│   │           ├── layout/
│   │           │   └── activity_main.xml  # Main UI layout (ConstraintLayout)
│   │           ├── values/
│   │           │   ├── colors.xml         # UI Colors
│   │           │   ├── strings.xml        # Application strings (Name, ID)
│   │           │   └── themes.xml         # App Themes
│   │           └── (other resource folders)
│   ├── build.gradle.kts                   # Module-level configuration
│   └── ...
├── build.gradle.kts                       # Project-level configuration
└── settings.gradle.kts                    # Project settings
```

## Output
Below is the visual representation of the application running on an Android device:

> [!NOTE]
> **Screenshot Placeholder**
> *Instructions to add screenshot: Run the app on an emulator/device, take a screenshot, and save it as `output.png` in the project root.*

![App Output](output.png)

---
*Created as part of an Android development experiment.*
