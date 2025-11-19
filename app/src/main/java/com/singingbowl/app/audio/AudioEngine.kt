package com.singingbowl.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import kotlin.math.PI
import kotlin.math.sin

class AudioEngine(private val context: Context) {
    
    companion object {
        const val SAMPLE_RATE = 44100
        const val BUFFER_SIZE = 2048
        const val NUM_CHANNELS = 4
    }
    
    // Channel parameters
    data class ChannelParams(
        var frequency: Float = 432f,
        var volume: Float = 0.5f,
        var enabled: Boolean = true,
        var lfoRate: Float = 0f,      // LFO frequency in Hz (0 = off)
        var lfoDepth: Float = 0f,     // LFO depth (0-1)
        var phase: Double = 0.0,
        var lfoPhase: Double = 0.0
    )
    
    private val channels = Array(NUM_CHANNELS) { ChannelParams() }
    private var masterVolume = 0.7f
    private var isPlaying = false
    
    // Audio track for tone generation
    private var audioTrack: AudioTrack? = null
    private var generatorThread: Thread? = null
    
    // BGM player
    private var mediaPlayer: MediaPlayer? = null
    private var bgmVolume = 0.5f
    private var bgmLooping = true
    private var bgmFadeIn = 0f  // seconds
    private var bgmFadeOut = 0f // seconds
    private var isBgmPlaying = false
    private var bgmUri: Uri? = null
    
    // Waveform data for visualization
    private val waveformData = Array(NUM_CHANNELS + 1) { FloatArray(BUFFER_SIZE) }
    private var waveformListeners = mutableListOf<WaveformListener>()
    
    interface WaveformListener {
        fun onWaveformUpdate(channelData: Array<FloatArray>)
    }
    
    fun addWaveformListener(listener: WaveformListener) {
        waveformListeners.add(listener)
    }
    
    fun removeWaveformListener(listener: WaveformListener) {
        waveformListeners.remove(listener)
    }
    
    init {
        initAudioTrack()
    }
    
    private fun initAudioTrack() {
        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(minBufferSize, BUFFER_SIZE * 2))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }
    
    fun start() {
        if (isPlaying) return
        isPlaying = true
        
        audioTrack?.play()
        
        generatorThread = Thread {
            val buffer = ShortArray(BUFFER_SIZE)
            
            while (isPlaying) {
                generateBuffer(buffer)
                audioTrack?.write(buffer, 0, buffer.size)
                
                // Notify waveform listeners
                waveformListeners.forEach { it.onWaveformUpdate(waveformData.clone()) }
            }
        }.apply { 
            priority = Thread.MAX_PRIORITY
            start() 
        }
    }
    
    fun stop() {
        isPlaying = false
        generatorThread?.join(500)
        generatorThread = null
        audioTrack?.pause()
        audioTrack?.flush()
    }
    
    private fun generateBuffer(buffer: ShortArray) {
        val mixBuffer = FloatArray(BUFFER_SIZE)
        
        // Clear waveform data
        for (i in 0 until NUM_CHANNELS + 1) {
            waveformData[i] = FloatArray(BUFFER_SIZE)
        }
        
        // Generate each channel
        for (ch in 0 until NUM_CHANNELS) {
            val params = channels[ch]
            if (!params.enabled || params.volume <= 0) continue
            
            val channelBuffer = FloatArray(BUFFER_SIZE)
            
            for (i in 0 until BUFFER_SIZE) {
                // Calculate LFO modulation
                var lfoMod = 1f
                if (params.lfoRate > 0 && params.lfoDepth > 0) {
                    val lfoValue = sin(2 * PI * params.lfoPhase).toFloat()
                    lfoMod = 1f - params.lfoDepth + params.lfoDepth * (lfoValue + 1f) / 2f
                    params.lfoPhase += params.lfoRate / SAMPLE_RATE
                    if (params.lfoPhase >= 1.0) params.lfoPhase -= 1.0
                }
                
                // Generate sine wave
                val sample = sin(2 * PI * params.phase).toFloat() * params.volume * lfoMod
                channelBuffer[i] = sample
                mixBuffer[i] += sample
                
                // Update phase
                params.phase += params.frequency / SAMPLE_RATE
                if (params.phase >= 1.0) params.phase -= 1.0
            }
            
            waveformData[ch] = channelBuffer
        }
        
        // Apply master volume and convert to short
        for (i in 0 until BUFFER_SIZE) {
            val sample = (mixBuffer[i] * masterVolume * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = sample.toShort()
            waveformData[NUM_CHANNELS][i] = mixBuffer[i] * masterVolume
        }
    }
    
    // Channel control methods
    fun setChannelFrequency(channel: Int, frequency: Float) {
        if (channel in 0 until NUM_CHANNELS) {
            channels[channel].frequency = frequency.coerceIn(20f, 20000f)
        }
    }
    
    fun getChannelFrequency(channel: Int): Float {
        return if (channel in 0 until NUM_CHANNELS) channels[channel].frequency else 0f
    }
    
    fun setChannelVolume(channel: Int, volume: Float) {
        if (channel in 0 until NUM_CHANNELS) {
            channels[channel].volume = volume.coerceIn(0f, 1f)
        }
    }
    
    fun getChannelVolume(channel: Int): Float {
        return if (channel in 0 until NUM_CHANNELS) channels[channel].volume else 0f
    }
    
    fun setChannelEnabled(channel: Int, enabled: Boolean) {
        if (channel in 0 until NUM_CHANNELS) {
            channels[channel].enabled = enabled
        }
    }
    
    fun isChannelEnabled(channel: Int): Boolean {
        return if (channel in 0 until NUM_CHANNELS) channels[channel].enabled else false
    }
    
    fun setChannelLfoRate(channel: Int, rate: Float) {
        if (channel in 0 until NUM_CHANNELS) {
            channels[channel].lfoRate = rate.coerceIn(0f, 20f)
        }
    }
    
    fun getChannelLfoRate(channel: Int): Float {
        return if (channel in 0 until NUM_CHANNELS) channels[channel].lfoRate else 0f
    }
    
    fun setChannelLfoDepth(channel: Int, depth: Float) {
        if (channel in 0 until NUM_CHANNELS) {
            channels[channel].lfoDepth = depth.coerceIn(0f, 1f)
        }
    }
    
    fun getChannelLfoDepth(channel: Int): Float {
        return if (channel in 0 until NUM_CHANNELS) channels[channel].lfoDepth else 0f
    }
    
    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
    }
    
    fun getMasterVolume(): Float = masterVolume
    
    fun isPlaying(): Boolean = isPlaying
    
    // BGM control methods
    fun loadBgm(uri: Uri) {
        bgmUri = uri
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(context, uri)
            isLooping = bgmLooping
            setVolume(bgmVolume, bgmVolume)
            prepare()
        }
    }
    
    fun playBgm() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                if (bgmFadeIn > 0) {
                    player.setVolume(0f, 0f)
                    player.start()
                    fadeInBgm()
                } else {
                    player.setVolume(bgmVolume, bgmVolume)
                    player.start()
                }
                isBgmPlaying = true
            }
        }
    }
    
    fun pauseBgm() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                if (bgmFadeOut > 0) {
                    fadeOutBgm {
                        player.pause()
                        isBgmPlaying = false
                    }
                } else {
                    player.pause()
                    isBgmPlaying = false
                }
            }
        }
    }
    
    fun stopBgm() {
        mediaPlayer?.let { player ->
            player.stop()
            player.prepare()
            isBgmPlaying = false
        }
    }
    
    private fun fadeInBgm() {
        Thread {
            val steps = (bgmFadeIn * 50).toInt()
            val stepDelay = (bgmFadeIn * 1000 / steps).toLong()
            for (i in 0..steps) {
                val vol = (i.toFloat() / steps) * bgmVolume
                mediaPlayer?.setVolume(vol, vol)
                Thread.sleep(stepDelay)
            }
        }.start()
    }
    
    private fun fadeOutBgm(onComplete: () -> Unit) {
        Thread {
            val steps = (bgmFadeOut * 50).toInt()
            val stepDelay = (bgmFadeOut * 1000 / steps).toLong()
            for (i in steps downTo 0) {
                val vol = (i.toFloat() / steps) * bgmVolume
                mediaPlayer?.setVolume(vol, vol)
                Thread.sleep(stepDelay)
            }
            onComplete()
        }.start()
    }
    
    fun setBgmVolume(volume: Float) {
        bgmVolume = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(bgmVolume, bgmVolume)
    }
    
    fun getBgmVolume(): Float = bgmVolume
    
    fun setBgmLooping(looping: Boolean) {
        bgmLooping = looping
        mediaPlayer?.isLooping = looping
    }
    
    fun isBgmLooping(): Boolean = bgmLooping
    
    fun setBgmFadeIn(seconds: Float) {
        bgmFadeIn = seconds.coerceIn(0f, 10f)
    }
    
    fun getBgmFadeIn(): Float = bgmFadeIn
    
    fun setBgmFadeOut(seconds: Float) {
        bgmFadeOut = seconds.coerceIn(0f, 10f)
    }
    
    fun getBgmFadeOut(): Float = bgmFadeOut
    
    fun isBgmPlaying(): Boolean = isBgmPlaying
    
    fun isBgmLoaded(): Boolean = bgmUri != null
    
    fun release() {
        stop()
        audioTrack?.release()
        audioTrack = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
