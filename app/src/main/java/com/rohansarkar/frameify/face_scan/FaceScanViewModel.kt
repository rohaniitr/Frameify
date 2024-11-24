package com.rohansarkar.frameify.face_scan

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohansarkar.frameify.model.Image
import com.rohansarkar.frameify.model.ImageMetadata
import com.rohansarkar.frameify.repository.ScreenshotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * View Model for [FaceScanFragment].
 */
class FaceScanViewModel(
    @SuppressLint("StaticFieldLeak") private val appContext: Context,
) : ViewModel() {

    private val repository = ScreenshotRepository(appContext)

    /**
     * Currently selected screenshot.
     */
    val selectedImage = MutableLiveData<Image>()

    val images: Flow<List<Image>> =
        repository.getFaceDetectionWithTagsFlow().map { it.map { it.toImage() } }

    /**
     * Updates the selected image in the ViewModel.
     */
    fun setSelectedImage(image: Image) {
        selectedImage.postValue(image)
    }

    /**
     * Fetches the list of screenshots from the gallery and updates [imageUris].
     */
    fun fetchImages() = viewModelScope.launch(Dispatchers.IO) {
        // Todo Rohan - Show loading state
        val images = repository.getCameraImages()
        repository.analyseImagesAndUpdateDb(images)
    }

    fun onTagUpdate(tagText: String, metadata: ImageMetadata) {
        val image = selectedImage.value ?: return
        val updatedImage = image.copy(metadata = image.metadata.map {
            if (it.frame == metadata.frame) metadata.copy(tag = tagText) else it
        })
        selectedImage.postValue(updatedImage)
        viewModelScope.launch {
            repository.updateTag(image.uri, metadata.frame, tagText)
        }
    }
}