# Singing Bowl App

A professional Android app for sound meditation with 4 adjustable frequency channels and background music support. Features LFO modulation for authentic singing bowl sound simulation.

## Features

- **4 Independent Frequency Channels** - Each with adjustable Hz (20-20,000 Hz)
- **LFO Modulation** - Rate and depth control for natural "woo woo" singing bowl effect
- **Background Music** - Load any audio file with loop and fade in/out controls
- **Real-time Waveform Visualization** - See all channels' audio output
- **Individual & Master Volume Control** - Fine-tune your sound mix
- **Dark Theme UI** - Easy on the eyes for meditation sessions

## Screenshots

The app features 4 main tabs:
- **Home** - Waveform visualization, play/pause, channel toggles, master volume
- **Sounds** - Frequency input for each channel, BGM file loader, presets
- **FX** - LFO rate/depth, individual volumes, BGM fade controls
- **More** - Reserved for future features

---

## Building the App

### Option 1: GitHub Codespaces (Recommended)

The easiest way to build the app without any local setup.

#### Step 1: Fork and Open in Codespaces

1. Fork this repository to your GitHub account
2. Click the green **Code** button
3. Select **Codespaces** tab
4. Click **Create codespace on main**

#### Step 2: Build the APK

Once the Codespace is ready (wait for the environment to initialize):

```bash
# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK (unsigned)
./gradlew assembleRelease
```

#### Step 3: Download the APK

The built APKs will be in:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

Right-click the file in the Explorer panel and select **Download** to get the APK.

---

### Option 2: Local Development (Android Studio)

#### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest version)
- JDK 17
- Android SDK 34

#### Step 1: Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/singing-bowl-app.git
cd singing-bowl-app
```

#### Step 2: Open in Android Studio

1. Open Android Studio
2. Select **File > Open**
3. Navigate to the cloned repository
4. Wait for Gradle sync to complete

#### Step 3: Build the APK

**From Android Studio:**
- **Menu > Build > Build Bundle(s) / APK(s) > Build APK(s)**

**From Terminal:**
```bash
./gradlew assembleDebug
```

#### Step 4: Run on Device/Emulator

- Connect an Android device with USB debugging enabled, or
- Create an AVD (Android Virtual Device) in Android Studio
- Click the **Run** button (green play icon)

---

### Option 3: Command Line Build

#### Prerequisites

- JDK 17
- Android SDK with build-tools and platform 34

```bash
# Set environment variables
export ANDROID_HOME=/path/to/android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# Clone and build
git clone https://github.com/YOUR_USERNAME/singing-bowl-app.git
cd singing-bowl-app
chmod +x gradlew
./gradlew assembleDebug
```

---

## Deploying the App

### For Personal Use (Direct APK Install)

1. Build the debug APK using any method above
2. Transfer `app-debug.apk` to your Android device
3. Enable **Install from Unknown Sources** in device settings
4. Open the APK file to install

### For Distribution (Signed Release)

#### Step 1: Create a Keystore

```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

#### Step 2: Configure Signing in build.gradle

Add to `app/build.gradle`:

```gradle
android {
    ...
    signingConfigs {
        release {
            storeFile file('my-release-key.jks')
            storePassword 'your-store-password'
            keyAlias 'my-key-alias'
            keyPassword 'your-key-password'
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

#### Step 3: Build Signed Release

```bash
./gradlew assembleRelease
```

The signed APK will be at: `app/build/outputs/apk/release/app-release.apk`

### For Google Play Store

1. Build a signed release as above
2. Create a [Google Play Developer account](https://play.google.com/console) ($25 one-time fee)
3. Create a new app in Play Console
4. Upload the signed APK or AAB (App Bundle)
5. Fill in store listing details
6. Submit for review

To build an App Bundle instead:
```bash
./gradlew bundleRelease
```
Output: `app/build/outputs/bundle/release/app-release.aab`

---

## GitHub Actions CI/CD

This repository includes automated builds via GitHub Actions.

### Automatic Builds

- **On Push/PR to main**: Builds debug and release APKs
- **On Release**: Attaches APK to GitHub Release

### Downloading Built APKs

1. Go to **Actions** tab in your repository
2. Click on the latest workflow run
3. Scroll to **Artifacts** section
4. Download `app-debug` or `app-release-unsigned`

### Creating a Release

1. Go to **Releases** in your repository
2. Click **Draft a new release**
3. Create a new tag (e.g., `v1.0.0`)
4. Publish the release
5. The workflow will automatically attach the APK

---

## App Usage Guide

### Home Tab
- **PLAY/PAUSE**: Start/stop all tone generation
- **CH1-CH4 Toggles**: Enable/disable individual channels
- **BGM Controls**: Play/stop background music, toggle loop
- **Master Volume**: Overall volume control

### Sounds Tab
- **Frequency Inputs**: Enter Hz value for each channel (20-20,000)
- **Presets**: Quick frequency combinations for popular frequencies
- **Load BGM**: Select audio file from device storage

### FX Tab
- **LFO Rate**: Speed of modulation (0-20 Hz)
  - 0 = no modulation (flat tone)
  - 0.5-2 Hz = slow, meditative pulse
  - 5-10 Hz = faster tremolo effect
- **LFO Depth**: Intensity of modulation (0-100%)
  - 0% = no effect
  - 30% = subtle wobble
  - 100% = full amplitude variation
- **Volume**: Individual channel volume
- **BGM Fade In/Out**: Smooth volume transitions (0-10 seconds)

### Recommended Settings for Meditation

**Relaxation:**
- CH1: 432 Hz (Universal harmony)
- CH2: 528 Hz (Healing/DNA repair)
- LFO Rate: 0.3 Hz, Depth: 20%

**Deep Meditation:**
- CH1: 396 Hz (Liberation from fear)
- CH2: 417 Hz (Facilitating change)
- CH3: 639 Hz (Connecting relationships)
- CH4: 741 Hz (Awakening intuition)
- LFO Rate: 0.5 Hz, Depth: 30%

---

## Technical Details

### Audio Engine
- Sample Rate: 44,100 Hz
- Buffer Size: 2,048 samples
- Pure sine wave generation
- Real-time LFO amplitude modulation

### Requirements
- Android 7.0 (API 24) or higher
- Storage permission for loading BGM files

---

## Troubleshooting

### No sound output
1. Check device volume is not muted
2. Ensure at least one channel is enabled (toggle on)
3. Verify master volume is above 0%
4. Press PLAY button

### BGM not loading
1. Grant storage permission when prompted
2. Ensure audio file format is supported (MP3, WAV, OGG, M4A)
3. Try a different audio file

### Build errors
1. Ensure JDK 17 is installed and configured
2. Run `./gradlew clean` before building
3. Check Android SDK is properly installed

---

## License

MIT License - feel free to use, modify, and distribute.

---

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

---

## Support

If you encounter issues:
1. Check the Troubleshooting section above
2. Open an issue on GitHub with:
   - Device model and Android version
   - Steps to reproduce
   - Error messages if any
