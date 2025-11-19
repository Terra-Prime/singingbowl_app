package com.singingbowl.app

import android.app.Application
import com.singingbowl.app.audio.AudioEngine

class SingingBowlApp : Application() {
    
    lateinit var audioEngine: AudioEngine
        private set
    
    override fun onCreate() {
        super.onCreate()
        audioEngine = AudioEngine(this)
    }
    
    override fun onTerminate() {
        super.onTerminate()
        audioEngine.release()
    }
}
