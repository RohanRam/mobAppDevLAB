# "Locate Me" Google Maps Integration Plan

This plan implements the "Locate Me" feature, displaying Google Maps inside the app using a WebView embedded with current GPS coordinates obtained via Google Play Services Fused Location Provider.

## User Review Required

> [!IMPORTANT]
> - **Location Permissions**: The app will request `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` at runtime.
> - **Embedded Google Maps**: Uses Google Play Services Location to get latitude/longitude and renders Google Maps inside a `WebView` (`https://maps.google.com/maps?q=lat,lng&z=15&output=embed`), requiring no paid API key configuration.

## Proposed Changes

### Manifest & Dependencies
#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/AndroidManifest.xml)
- Add location permissions (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`).

#### [MODIFY] [build.gradle.kts](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/build.gradle.kts)
- Add `play-services-location` dependency.

### Navigation & Layouts
#### [MODIFY] [nav_graph.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/res/navigation/nav_graph.xml)
- Add `mapFragment` destination and action.

#### [NEW] [fragment_map.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/res/layout/fragment_map.xml)
- Layout containing a WebView for Google Maps and a "Locate Me" floating action button.

#### [MODIFY] [fragment_item_list.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/res/layout/fragment_item_list.xml)
- Add a "Locate Me" button to navigate to the Map screen.

### Logic & Implementation
#### [NEW] [MapFragment.kt](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/java/com/example/testapp/MapFragment.kt)
- Fragment handling runtime location permissions, fetching current GPS coordinates via `FusedLocationProviderClient`, and loading the Google Maps embed URL in the WebView.

#### [MODIFY] [ItemListFragment.kt](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/java/com/example/testapp/ItemListFragment.kt)
- Wire up the "Locate Me" button click listener to navigate to `MapFragment`.

## Verification Plan

### Automated Tests
- Build project using Gradle (`app:assembleDebug`) to verify compilation.

### Manual Verification
- Deploy app to emulator.
- Tap the "Locate Me" button.
- Grant location permission.
- Verify that Google Maps loads inside the app and centers on the emulator's current GPS location.
