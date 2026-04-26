# Skreenup — Professional Screenshot Framing Made Simple

<p align="center">
  <img src="https://github.com/user-attachments/assets/c6f07488-7694-4fff-b70e-dc0b8bd50a5d" width="128" height="128" alt="Skreenup Logo" />
</p>

<p align="center">
  A modern, professional-looking Android application built with Jetpack Compose that allows users to create stunning screenshot mockups with realistic device frames and beautiful backgrounds.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.2.1-blue?style=for-the-badge" alt="Version" />
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen?logo=android&logoColor=white&style=for-the-badge" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-orange?logo=kotlin&logoColor=white&style=for-the-badge" alt="Language" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-blue?logo=jetpackcompose&logoColor=white&style=for-the-badge" alt="UI" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-purple?style=for-the-badge" alt="Architecture" />
  <img src="https://img.shields.io/badge/License-GNU-grey?style=for-the-badge" alt="License" />
</p>

---

## 📸 Screenshots
<p align="center">
    <img src="https://github.com/user-attachments/assets/93a75138-fe93-443f-98fa-ba49022eed06"/> <br>
  <img src="https://github.com/user-attachments/assets/87745be3-abf8-46c0-b351-2cd0c4e604bf" width="30%"/> &nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/25285631-dc95-42ee-998b-fa7980d874ce" width="30%"/>
  </p>

## 🚀 Features

### 🖼️ Device Framing
* **Multiple Device Frames:** Choose from a variety of Android and iOS device frames across different categories (Phone, Tablet, Laptop, etc.).
* **Realistic Render:** High-quality device frames that adapt to your screenshot's aspect ratio.
* **Advanced Adjustments:** Fine-tune shadow intensity, softness, and toggle screen reflections.
* **Frame Rotation:** Rotate the device frame to create dynamic, tilted mockups.

### 🎨 Background Customization
* **Solid Colors & Gradients:** Apply vibrant solid colors or beautiful linear gradients with a custom gradient builder.
* **Image Blurs:** Use a blurred version of your own screenshot or a custom image as a background with adjustable blur levels.
* **Import Gallery Image:** Use any image from your gallery as a custom background.

### ✍️ Personalization & Branding
* **Text Overlays:** Add headings and subheadings with various fonts, sizes, and alignments.
* **Smart Watermarks:** Toggle a brand watermark that automatically adapts its color (Black/White) based on the background luminance for perfect visibility.
* **Flexible Layouts:** Adjust the scale and position of both the device frame and text overlays independently.
* **Layering Control:** Change the Z-Index of text to place it in front of or behind the device frame.
* **Aspect Ratios:** Export in various formats optimized for social media (Square, Portrait, Story).

### 💾 Persistence
* **Project History:** All your previous designs are automatically saved locally using Room Database, so you can resume editing later.

## 🛠 Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin (100%) |
| UI Framework | Jetpack Compose (Material 3) |
| Architecture | MVVM (Model-View-ViewModel) |
| Navigation | Compose Navigation 3 |
| Persistence | Room Database |
| Image Loading | Coil |
| Background Ops | Kotlin Coroutines & Flow |

## 📂 Project Structure
```text
app/src/main/java/com/example/skreenup/
│
├── data/                # Room Database, DAOs, and Entities (Project, Preset)
├── navigation/          # Navigation logic and screen definitions
├── ui/
│   ├── components/      # Reusable UI components (Buttons, Tabs, etc.)
│   ├── models/          # UI-specific data models
│   ├── screens/         # Feature screens (Home, Editor, About)
│   │   └── tabs/        # Editor tab screens (Background, Frame, etc.)
│   └── theme/           # App's Design System (Colors, Typography, Shapes)
└── MainActivity.kt      # Main entry point of the application
```

## ⚙️ Getting Started

### Prerequisites
* Android Studio Ladybug (2024.2.1) or later
* Android SDK 35 (Target 37)
* JDK 11+

### Build & Run
1. Clone the repo: `git clone https://github.com/Pankaj-Meharchandani/Skreenup.git`
2. Open in Android Studio.
3. Sync Gradle and run on a device/emulator with API 30+.

## 🛡 Permissions
| Permission | Usage |
|------------|-------|
| `INTERNET` | Required for fetching images or updates (if applicable). |

## 📄 License
This project is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for details.
