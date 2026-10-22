# Walkthrough - "Locate Me" Google Maps Integration

Successfully implemented the "Locate Me" feature, which embeds interactive Google Maps inside the app using current location coordinates fetched via Google Play Services Fused Location Provider.

## Changes

### Manifest & Dependencies
#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/AndroidManifest.xml)
- Added `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` permissions.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/build.gradle.kts)
- Added `play-services-location` dependency.

### Navigation & Layouts
#### [MODIFY] [nav_graph.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/res/navigation/nav_graph.xml)
- Added `mapFragment` destination and navigation action from main content.

#### [NEW] [fragment_map.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/res/layout/fragment_map.xml)
- Added layout containing a WebView for rendering Google Maps.

#### [MODIFY] [fragment_item_list.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/res/layout/fragment_item_list.xml)
- Added the "Locate Me" button to the course dashboard.

### Logic & Implementation
#### [NEW] [MapFragment.kt](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/java/com/example/testapp/MapFragment.kt)
- Implemented runtime location permissions handling (`ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`).
- Integrated `FusedLocationProviderClient` to fetch current GPS coordinates.
- Loaded embedded Google Maps URL (`https://maps.google.com/maps?q=lat,lng&z=15&output=embed`) inside the `WebView`.

#### [MODIFY] [ItemListFragment.kt](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/java/com/example/testapp/ItemListFragment.kt)
- Wired up the "Locate Me" button to navigate to `MapFragment`.

## Verification Results

### Automated Tests
- Gradle build (`app:assembleDebug`) completed successfully with zero compilation errors.
