package com.rohansarkar.frameify.face_scan

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohansarkar.frameify.databinding.DialogTagInputBinding
import com.rohansarkar.frameify.databinding.FragmentFaceScanBinding
import com.rohansarkar.frameify.model.Image
import com.rohansarkar.frameify.util.CustomViewModelFactory
import com.rohansarkar.frameify.util.HorizontalSpaceItemDecoration
import com.rohansarkar.frameify.util.dpToPixels
import com.rohansarkar.frameify.util.load
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Displays list of screenshots from the device.
 */
class FaceScanFragment : Fragment() {

    /**
     * View binding for [FaceScanFragment].
     */
    private lateinit var binding: FragmentFaceScanBinding

    private lateinit var viewModel: FaceScanViewModel

    private val imageAdapter = ImageAdapter()

    /**
     * Callback for permission request.
     */
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Storage permission granted. Start process for reading screenshots.
                viewModel.fetchImages()
            } else {
                // Permission denied. Close the app.
                activity?.finish()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(
            this,
            CustomViewModelFactory(requireActivity().applicationContext)
        )[FaceScanViewModel::class.java]
        binding = FragmentFaceScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        initListeners()
        observeValues()
    }

    override fun onResume() {
        super.onResume()
        when {
            haveStoragePermission() -> viewModel.fetchImages()
            else -> requestStoragePermission()
        }
    }

    /**
     * All layout initialisation should reside here.
     */
    private fun initLayout() {
        initRecyclerView()
    }

    /**
     * All Live Data should be observed here.
     */
    private fun observeValues() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.images.collect { faceDetectionList ->
                    if (imageAdapter.itemCount == 0) {
                        faceDetectionList.firstOrNull()?.let {
                            it.isSelected = true
                            viewModel.setSelectedImage(it)
                        }
                    }
                    imageAdapter.submitList(faceDetectionList)
                }
            }
        }

        viewModel.selectedImage.observe(viewLifecycleOwner) { screenshot ->
            onImageChanged(screenshot)
        }
    }

    /**
     * All listener should be subscribed here.
     */
    private fun initListeners() {
        imageAdapter.setItemClickListener(::onImageClick)

        binding.vgOverlay.onBoxClickListener = { metadata ->
            showTagInputDialog { tagText ->
                viewModel.onTagUpdate(tagText, metadata)
            }
        }
    }

    /**
     * Image selected from bottom recycler view.
     */
    private fun onImageClick(image: Image) {
        viewModel.setSelectedImage(image)
    }

    /**
     * Updates the displayed image on the screen with the selected screenshot.
     */
    private fun onImageChanged(image: Image) {
        binding.ivSelectedScreenshot.load(image.uri)
        binding.vgOverlay.setResults(image)
    }

    private fun initRecyclerView() {
        binding.rvScreenshot.addItemDecoration(
            HorizontalSpaceItemDecoration(4.dpToPixels(requireContext()))
        )
        binding.rvScreenshot.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvScreenshot.adapter = imageAdapter
    }

    private fun showTagInputDialog(onSave: (String) -> Unit) {
        val dialog = Dialog(requireContext())
        val binding =
            DialogTagInputBinding.inflate(layoutInflater) // ViewBinding for the dialog layout
        dialog.setContentView(binding.root)

        // Set dialog properties
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Transparent background for rounded corners
        dialog.setCancelable(true) // Allow dismissing the dialog by clicking outside

        // Automatically open the keyboard when the dialog opens
        // Fixme : Fix this hack later!
        dialog.setOnShowListener {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                binding.etInput.requestFocus()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.etInput, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        // Handle save button click
        binding.btnSave.setOnClickListener {
            onSave(binding.etInput.text.toString().trim())
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ) // Set size for the dialog
    }

    /**
     * Checks if we already have Storage/Media permission.
     *
     * NOTE : [READ_EXTERNAL_STORAGE] permission is deprecated from [Build.VERSION_CODES.TIRAMISU] onwards.
     */
    private fun haveStoragePermission(): Boolean {
        val permissionStatus = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
        } else {
            ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
        }
        return (permissionStatus == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Request for Storage/Media permission.
     *
     * NOTE : [READ_EXTERNAL_STORAGE] permission is deprecated from [Build.VERSION_CODES.TIRAMISU] onwards.
     */
    private fun requestStoragePermission() = requestPermissionLauncher.launch(
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) READ_EXTERNAL_STORAGE
        else READ_MEDIA_IMAGES
    )
}