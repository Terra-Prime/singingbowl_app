package com.singingbowl.app.ui.sounds

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.singingbowl.app.databinding.FragmentSoundsBinding
import com.singingbowl.app.viewmodel.MainViewModel

class SoundsFragment : Fragment() {
    
    private var _binding: FragmentSoundsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MainViewModel by activityViewModels()
    
    private val pickAudioLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val fileName = getFileName(uri)
                viewModel.loadBgm(uri, fileName)
                binding.txtBgmFile.text = fileName
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoundsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupFrequencyInputs()
        setupBgmLoader()
        observeViewModel()
    }
    
    private fun setupFrequencyInputs() {
        // Initialize with current values
        binding.editFreq1.setText(viewModel.getChannelFrequency(0).toInt().toString())
        binding.editFreq2.setText(viewModel.getChannelFrequency(1).toInt().toString())
        binding.editFreq3.setText(viewModel.getChannelFrequency(2).toInt().toString())
        binding.editFreq4.setText(viewModel.getChannelFrequency(3).toInt().toString())
        
        // Set up text watchers
        binding.editFreq1.addTextChangedListener(createFreqWatcher(0))
        binding.editFreq2.addTextChangedListener(createFreqWatcher(1))
        binding.editFreq3.addTextChangedListener(createFreqWatcher(2))
        binding.editFreq4.addTextChangedListener(createFreqWatcher(3))
        
        // Preset buttons
        binding.btnPreset432.setOnClickListener {
            setPreset(432f, 528f, 639f, 741f)
        }
        binding.btnPreset528.setOnClickListener {
            setPreset(528f, 639f, 741f, 852f)
        }
        binding.btnPresetCustom.setOnClickListener {
            setPreset(396f, 417f, 528f, 639f)
        }
    }
    
    private fun createFreqWatcher(channel: Int): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val freq = s.toString().toFloatOrNull()
                if (freq != null && freq in 20f..20000f) {
                    viewModel.setChannelFrequency(channel, freq)
                }
            }
        }
    }
    
    private fun setPreset(f1: Float, f2: Float, f3: Float, f4: Float) {
        binding.editFreq1.setText(f1.toInt().toString())
        binding.editFreq2.setText(f2.toInt().toString())
        binding.editFreq3.setText(f3.toInt().toString())
        binding.editFreq4.setText(f4.toInt().toString())
    }
    
    private fun setupBgmLoader() {
        binding.btnLoadBgm.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "audio/*"
            }
            pickAudioLauncher.launch(intent)
        }
    }
    
    private fun getFileName(uri: android.net.Uri): String {
        var name = "Unknown"
        context?.contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            if (nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
    
    private fun observeViewModel() {
        viewModel.bgmFileName.observe(viewLifecycleOwner) { fileName ->
            if (fileName.isNotEmpty()) {
                binding.txtBgmFile.text = fileName
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
