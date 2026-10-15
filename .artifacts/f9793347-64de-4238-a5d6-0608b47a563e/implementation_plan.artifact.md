# Implement Data Sharing & Access using Contacts Content Provider

This plan outlines the implementation of reading and accessing device contact data using Android's `ContactsContract` Content Provider in the TestApp application.

## User Review Required

> [!IMPORTANT]
> - **Runtime Permissions**: The app will request `android.permission.READ_CONTACTS` at runtime. Users must grant permission to view contacts.
> - **New Navigation Destination**: A new "Contacts" screen (`ContactsFragment`) will be added to the navigation graph and accessible from the main content screen.

## Open Questions

- None. The feature will provide a clean interface to query device contacts via `ContentResolver` and display them in a RecyclerView.

## Proposed Changes

### Android Manifest
#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/AndroidManifest.xml)
- Add `<uses-permission android:name="android.permission.READ_CONTACTS" />`

### Navigation & UI Layouts
#### [MODIFY] [nav_graph.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/res/navigation/nav_graph.xml)
- Add `<fragment android:id="@+id/contactsFragment" android:name="com.example.testapp.ContactsFragment" ... />`
- Add action from `mainContentFragment` to `contactsFragment`.

#### [NEW] [fragment_contacts.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/res/layout/fragment_contacts.xml)
- Layout containing a RecyclerView for contacts and a permission request prompt view / empty view.

#### [NEW] [item_contact.xml](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/res/layout/item_contact.xml)
- Layout for individual contact items displaying name and phone number.

### Data & UI Logic
#### [NEW] [ContactItem.kt](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/java/com/example/testapp/ContactItem.kt)
- Data class for contact representation (`id`, `name`, `phoneNumber`).

#### [NEW] [ContactAdapter.kt](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/java/com/example/testapp/ContactAdapter.kt)
- RecyclerView adapter for rendering contact items.

#### [NEW] [ContactsFragment.kt](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/java/com/example/testapp/ContactsFragment.kt)
- Fragment handling permission checks (`READ_CONTACTS`), querying `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` via `ContentResolver`, and managing RecyclerView data.

#### [MODIFY] [MainContentFragment.kt](file:///C:/Users/ROHAN/AndroidStudioProjects/TestApp/app/src/main/java/com/example/testapp/MainContentFragment.kt)
- Add a button or menu option to navigate to `ContactsFragment`.

## Verification Plan

### Automated Tests
- Build the project using Gradle (`app:assembleDebug`) to ensure no compilation or resource errors.

### Manual Verification
- Deploy the app to the device/emulator.
- Navigate to the new Contacts screen.
- Grant the `READ_CONTACTS` permission when prompted.
- Verify that device contacts load correctly and display their names and phone numbers in the RecyclerView.
