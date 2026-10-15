# TestApp - Data Sharing via Contacts Content Provider

A modern Android application demonstrating inter-app data sharing and access using Android's system **Contacts Content Provider** (`ContactsContract`).

---

## 📱 Features & Implementation

### 1. Contacts Content Provider Querying
The application accesses device contacts by querying Android's system `ContentResolver` against `ContactsContract.CommonDataKinds.Phone.CONTENT_URI`. Contact records are retrieved asynchronously using Kotlin Coroutines on `Dispatchers.IO` to ensure smooth UI performance.

```kotlin
// Querying contacts via ContentResolver
val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
val projection = arrayOf(
    ContactsContract.CommonDataKinds.Phone._ID,
    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
    ContactsContract.CommonDataKinds.Phone.NUMBER
)

val cursor: Cursor? = requireContext().contentResolver.query(
    uri,
    projection,
    null,
    null,
    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
)
```

### 2. Runtime Permissions
To comply with modern Android security best practices, the app automatically handles runtime permissions for `READ_CONTACTS` using `ActivityResultContracts.RequestPermission()`.

```kotlin
private val requestPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            loadContacts()
        }
    }
```

### 3. iOS-Themed UI & Bottom Navigation
- **Dedicated Tab**: Contacts is integrated as a primary tab in the floating bottom navigation bar (`MainContentFragment`).
- **Clean Layout**: Rendered in an iOS-styled grouped `RecyclerView` displaying contact names and phone numbers.

---

## 📸 Output Screenshot

![Output Screenshot](img.png)

---
*Developed as part of Advanced Android Mobile Application Architecture & Content Provider Data Sharing Project.*
