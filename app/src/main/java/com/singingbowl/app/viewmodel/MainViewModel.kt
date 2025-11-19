package com.singingbowl.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.singingbowl.app.SingingBowlApp
import com.singingbowl.app.audio.AudioEngine

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val audioEngine = (application as SingingBowlApp).audioEngine
    
    // Play state
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying
    
    // Channel states
    private val _channelEnabled = MutableLiveData(booleanArrayOf(true, true, true, true))
    val channelEnabled: LiveData<BooleanArray> = _channelEnabled
    
    // BGM state
    private val _isBgmPlaying = MutableLiveData(false)
    val isBgmPlaying: LiveData<Boolean> = _isBgmPlaying
    
    private val _isBgmLooping = MutableLiveData(true)
    val isBgmLooping: LiveData<Boolean> = _isBgmLooping
    
    private val _bgmLoaded = MutableLiveData(false)
    val bgmLoaded: LiveData<Boolean> = _bgmLoaded
    
    private val _bgmFileName = MutableLiveData("")
    val bgmFileName: LiveData<String> = _bgmFileName
    
    // Waveform data
    private val _waveformData = MutableLiveData<Array<FloatArray>>()
    val waveformData: LiveData<Array<FloatArray>> = _waveformData
    
    private val waveformListener = object : AudioEngine.WaveformListener {
        override fun onWaveformUpdate(channelData: Array<FloatArray>) {
            _waveformData.postValue(channelData)
        }
    }
    
    init {
        audioEngine.addWaveformListener(waveformListener)
        // Set default frequencies for singing bowl tones
        audioEngine.setChannelFrequency(0, 432f)  // Root
        audioEngine.setChannelFrequency(1, 528f)  // Solar plexus
        audioEngine.setChannelFrequency(2, 639f)  // Heart
        audioEngine.setChannelFrequency(3, 741f)  // Throat
        
        // Set default LFO for natural sound
        for (i in 0 until 4) {
            audioEngine.setChannelLfoRate(i, 0.5f)
            audioEngine.setChannelLfoDepth(i, 0.3f)
        }
    }
    
    fun togglePlay() {
        if (audioEngine.isPlaying()) {
            audioEngine.stop()
            _isPlaying.value = false
        } else {
            audioEngine.start()
            _isPlaying.value = true
        }
    }
    
    fun toggleChannelEnabled(channel: Int) {
        val newState = !audioEngine.isChannelEnabled(channel)
        audioEngine.setChannelEnabled(channel, newState)
        val current = _channelEnabled.value?.copyOf() ?: booleanArrayOf(true, true, true, true)
        current[channel] = newState
        _channelEnabled.value = current
    }
    
    fun setChannelEnabled(channel: Int, enabled: Boolean) {
        audioEngine.setChannelEnabled(channel, enabled)
        val current = _channelEnabled.value?.copyOf() ?: booleanArrayOf(true, true, true, true)
        current[channel] = enabled
        _channelEnabled.value = current
    }
    
    fun setChannelFrequency(channel: Int, frequency: Float) {
        audioEngine.setChannelFrequency(channel, frequency)
    }
    
    fun getChannelFrequency(channel: Int): Float {
        return audioEngine.getChannelFrequency(channel)
    }
    
    fun setChannelVolume(channel: Int, volume: Float) {
        audioEngine.setChannelVolume(channel, volume)
    }
    
    fun getChannelVolume(channel: Int): Float {
        return audioEngine.getChannelVolume(channel)
    }
    
    fun setChannelLfoRate(channel: Int, rate: Float) {
        audioEngine.setChannelLfoRate(channel, rate)
    }
    
    fun getChannelLfoRate(channel: Int): Float {
        return audioEngine.getChannelLfoRate(channel)
    }
    
    fun setChannelLfoDepth(channel: Int, depth: Float) {
        audioEngine.setChannelLfoDepth(channel, depth)
    }
    
    fun getChannelLfoDepth(channel: Int): Float {
        return audioEngine.getChannelLfoDepth(channel)
    }
    
    fun setMasterVolume(volume: Float) {
        audioEngine.setMasterVolume(volume)
    }
    
    fun getMasterVolume(): Float {
        return audioEngine.getMasterVolume()
    }
    
    // BGM controls
    fun loadBgm(uri: Uri, fileName: String) {
        audioEngine.loadBgm(uri)
        _bgmLoaded.value = true
        _bgmFileName.value = fileName
    }
    
    fun toggleBgm() {
        if (audioEngine.isBgmPlaying()) {
            audioEngine.pauseBgm()
            _isBgmPlaying.value = false
        } else {
            audioEngine.playBgm()
            _isBgmPlaying.value = true
        }
    }
    
    fun toggleBgmLoop() {
        val newState = !audioEngine.isBgmLooping()
        audioEngine.setBgmLooping(newState)
        _isBgmLooping.value = newState
    }
    
    fun setBgmVolume(volume: Float) {
        audioEngine.setBgmVolume(volume)
    }
    
    fun getBgmVolume(): Float {
        return audioEngine.getBgmVolume()
    }
    
    fun setBgmFadeIn(seconds: Float) {
        audioEngine.setBgmFadeIn(seconds)
    }
    
    fun getBgmFadeIn(): Float {
        return audioEngine.getBgmFadeIn()
    }
    
    fun setBgmFadeOut(seconds: Float) {
        audioEngine.setBgmFadeOut(seconds)
    }
    
    fun getBgmFadeOut(): Float {
        return audioEngine.getBgmFadeOut()
    }
    
    override fun onCleared() {
        super.onCleared()
        audioEngine.removeWaveformListener(waveformListener)
        audioEngine.stop()
    }
}
