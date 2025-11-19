package com.singingbowl.app.ui.fx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.singingbowl.app.databinding.FragmentFxBinding
import com.singingbowl.app.viewmodel.MainViewModel

class FxFragment : Fragment() {
    
    private var _binding: FragmentFxBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MainViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFxBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupChannelControls()
        setupBgmControls()
    }
    
    private fun setupChannelControls() {
        // Channel 1
        setupChannelSeekBars(
            0,
            binding.seekLfoRate1, binding.seekLfoDepth1, binding.seekVolume1,
            binding.txtLfoRate1, binding.txtLfoDepth1, binding.txtVolume1
        )
        
        // Channel 2
        setupChannelSeekBars(
            1,
            binding.seekLfoRate2, binding.seekLfoDepth2, binding.seekVolume2,
            binding.txtLfoRate2, binding.txtLfoDepth2, binding.txtVolume2
        )
        
        // Channel 3
        setupChannelSeekBars(
            2,
            binding.seekLfoRate3, binding.seekLfoDepth3, binding.seekVolume3,
            binding.txtLfoRate3, binding.txtLfoDepth3, binding.txtVolume3
        )
        
        // Channel 4
        setupChannelSeekBars(
            3,
            binding.seekLfoRate4, binding.seekLfoDepth4, binding.seekVolume4,
            binding.txtLfoRate4, binding.txtLfoDepth4, binding.txtVolume4
        )
    }
    
    private fun setupChannelSeekBars(
        channel: Int,
        lfoRateSeek: SeekBar, lfoDepthSeek: SeekBar, volumeSeek: SeekBar,
        lfoRateTxt: android.widget.TextView, lfoDepthTxt: android.widget.TextView, volumeTxt: android.widget.TextView
    ) {
        // Initialize values
        lfoRateSeek.progress = (viewModel.getChannelLfoRate(channel) * 10).toInt()
        lfoDepthSeek.progress = (viewModel.getChannelLfoDepth(channel) * 100).toInt()
        volumeSeek.progress = (viewModel.getChannelVolume(channel) * 100).toInt()
        
        lfoRateTxt.text = String.format("%.1f Hz", viewModel.getChannelLfoRate(channel))
        lfoDepthTxt.text = "${(viewModel.getChannelLfoDepth(channel) * 100).toInt()}%"
        volumeTxt.text = "${(viewModel.getChannelVolume(channel) * 100).toInt()}%"
        
        // LFO Rate listener
        lfoRateSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val rate = progress / 10f
                    viewModel.setChannelLfoRate(channel, rate)
                    lfoRateTxt.text = String.format("%.1f Hz", rate)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // LFO Depth listener
        lfoDepthSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.setChannelLfoDepth(channel, progress / 100f)
                    lfoDepthTxt.text = "$progress%"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Volume listener
        volumeSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.setChannelVolume(channel, progress / 100f)
                    volumeTxt.text = "$progress%"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    private fun setupBgmControls() {
        // BGM Volume
        binding.seekBgmVolume.progress = (viewModel.getBgmVolume() * 100).toInt()
        binding.txtBgmVolume.text = "${(viewModel.getBgmVolume() * 100).toInt()}%"
        
        binding.seekBgmVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.setBgmVolume(progress / 100f)
                    binding.txtBgmVolume.text = "$progress%"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Fade In
        binding.seekFadeIn.progress = (viewModel.getBgmFadeIn() * 10).toInt()
        binding.txtFadeIn.text = String.format("%.1f s", viewModel.getBgmFadeIn())
        
        binding.seekFadeIn.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val seconds = progress / 10f
                    viewModel.setBgmFadeIn(seconds)
                    binding.txtFadeIn.text = String.format("%.1f s", seconds)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Fade Out
        binding.seekFadeOut.progress = (viewModel.getBgmFadeOut() * 10).toInt()
        binding.txtFadeOut.text = String.format("%.1f s", viewModel.getBgmFadeOut())
        
        binding.seekFadeOut.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val seconds = progress / 10f
                    viewModel.setBgmFadeOut(seconds)
                    binding.txtFadeOut.text = String.format("%.1f s", seconds)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
