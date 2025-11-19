package com.singingbowl.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.singingbowl.app.databinding.FragmentHomeBinding
import com.singingbowl.app.viewmodel.MainViewModel

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MainViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupControls()
        setupWaveforms()
        observeViewModel()
    }
    
    private fun setupControls() {
        // Play/Pause button
        binding.btnPlayPause.setOnClickListener {
            viewModel.togglePlay()
        }
        
        // Channel toggles
        binding.toggleCh1.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setChannelEnabled(0, isChecked)
        }
        binding.toggleCh2.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setChannelEnabled(1, isChecked)
        }
        binding.toggleCh3.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setChannelEnabled(2, isChecked)
        }
        binding.toggleCh4.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setChannelEnabled(3, isChecked)
        }
        
        // BGM controls
        binding.btnBgmToggle.setOnClickListener {
            viewModel.toggleBgm()
        }
        binding.toggleBgmLoop.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != viewModel.isBgmLooping.value) {
                viewModel.toggleBgmLoop()
            }
        }
        
        // Master volume
        binding.seekMasterVolume.progress = (viewModel.getMasterVolume() * 100).toInt()
        binding.seekMasterVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.setMasterVolume(progress / 100f)
                    binding.txtMasterVolume.text = "$progress%"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.txtMasterVolume.text = "${(viewModel.getMasterVolume() * 100).toInt()}%"
    }
    
    private fun setupWaveforms() {
        binding.waveform1.setChannelColor(0)
        binding.waveform2.setChannelColor(1)
        binding.waveform3.setChannelColor(2)
        binding.waveform4.setChannelColor(3)
        binding.waveformBgm.setChannelColor(4)
    }
    
    private fun observeViewModel() {
        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.btnPlayPause.text = if (isPlaying) "PAUSE" else "PLAY"
        }
        
        viewModel.channelEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.toggleCh1.isChecked = enabled[0]
            binding.toggleCh2.isChecked = enabled[1]
            binding.toggleCh3.isChecked = enabled[2]
            binding.toggleCh4.isChecked = enabled[3]
        }
        
        viewModel.isBgmPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.btnBgmToggle.text = if (isPlaying) "STOP BGM" else "PLAY BGM"
        }
        
        viewModel.isBgmLooping.observe(viewLifecycleOwner) { isLooping ->
            binding.toggleBgmLoop.isChecked = isLooping
        }
        
        viewModel.bgmLoaded.observe(viewLifecycleOwner) { loaded ->
            binding.btnBgmToggle.isEnabled = loaded
        }
        
        viewModel.waveformData.observe(viewLifecycleOwner) { data ->
            if (data.size >= 5) {
                binding.waveform1.setWaveformData(data[0])
                binding.waveform2.setWaveformData(data[1])
                binding.waveform3.setWaveformData(data[2])
                binding.waveform4.setWaveformData(data[3])
                binding.waveformBgm.setWaveformData(data[4])
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
