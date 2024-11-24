package com.rohansarkar.frameify.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rohansarkar.frameify.face_scan.FaceScanViewModel

/**
 * Factory for creating instances of ViewModels which require [appContext].
 */
@Suppress("UNCHECKED_CAST")
class CustomViewModelFactory(
    private val appContext: Context,
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the specified ViewModel class.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FaceScanViewModel::class.java) ->
                FaceScanViewModel(appContext) as T

            else ->
                throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}