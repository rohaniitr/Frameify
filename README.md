# Frameify App

## Description
Frameify is a Kotlin-based application designed to process images, detect faces, and allow tagging of detected face regions. It leverages Room Database for local storage, the MediaPipe library for face detection, and coroutine-based concurrency for efficient image processing.

## Architecture
The project follows the **MVVM (Model-View-ViewModel)** architecture pattern:

- **Feature Module:** Includes UI components and features such as `/face_scan` and `/ui_widget`.
- **Core Module:** Contains shared functionalities like `/image_process` and `/repository`.
- **Local Module:** Handles database logic under `/db`.
- **Model Module:** Includes data models and resides at the bottom-most layer.
- **App Module:** The top-level module that packages everything together.

**Note:** While modular architecture is not implemented due to time constraints, the structure is organized to facilitate easy modularization in the future.

### Key Assumptions
- Dependency Injection (DI) is not implemented.
- Unit tests are not included due to time limitations.

## How to Run the App
1. Open the application.
2. Grant the required storage permissions when prompted.
3. The app will scan the device’s **camera folder** for images.
4. Images containing faces are displayed in a list, with detected faces wrapped in bounding boxes.
5. Select a bounding box to add tags related to a specific face.

## Approach
1. The app reads images from the **camera directory**.
2. Images are processed using the **MediaPipe library** to detect faces.
3. Metadata for each image and detected face is stored in a local **Room Database**:
    - **FaceDetection Table:** Stores image URI and face detection status.
    - **ImageFrame Table:** Stores face bounding box information and associated tags.
4. Processing runs on the **IO thread** for efficient performance, with a concurrency strategy of processing 5 batches (of size 20 images each) simultaneously.
5. Images are processed only if their metadata is not already in the database.
6. The app uses **Flow** to fetch and display images with faces, ensuring real-time updates in the UI.

## Improvements
1. **Timestamp-Based Optimization:**
    - Implement a timestamp-based strategy to avoid checking all images repeatedly.
    - Note: This approach may miss user-modified images.

2. **Background Processing:**
    - Use a **Worker** to process images even when the app is closed.
    - Optionally, schedule a periodic worker to keep the database updated.

3. **Database Cleanup:**
    - Implement logic to delete records for images that are no longer present in the gallery.

## Technologies Used
- **MediaPipe**: For face detection.
- **Room Database**: For local storage of image metadata.
- **Kotlin Coroutines**: For concurrency.
- **MVVM Architecture**: For structured code and maintainability.

---
Feel free to explore the app and manage your images efficiently!

