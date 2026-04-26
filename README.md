# Skreenup — Professional Screenshot Framing Made Simple

<p align="center">
  <img src="https://github.com/user-attachments/assets/c6f07488-7694-4fff-b70e-dc0b8bd50a5d" width="128" height="128" alt="Skreenup Logo" />
</p>

<p align="center">
  A modern, professional-looking Android application built with Jetpack Compose that allows users to create stunning screenshot mockups with realistic device frames and beautiful backgrounds.
</p>

<p align="center">
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

---

## 🚀 Features

### 🖼️ Device Framing
* **Multiple Device Frames:** Choose from a variety of Android, iOS, browser, and desktop frames across categories (Phone, Tablet, Laptop, Browser, etc.).
* **Realistic Render:** High-quality device frames with reflections, shadows, and aspect-ratio awareness.
* **Advanced Adjustments:** Fine-tune shadow intensity, softness, and toggle screen reflections.
* **Frame Rotation:** Rotate the device frame with snapping support to create dynamic, tilted mockups.
* **Auto Screen Color:** Automatically detects and applies the dominant color from your screenshot to the device screen bezel.

### 🎨 Background Customization
* **Solid Colors & Gradients:** Apply vibrant solid colors or beautiful linear gradients with a custom gradient builder.
* **Image Blurs:** Use a blurred version of image as a background with adjustable blur levels.
* **Import Gallery Image:** Use any image from your gallery as a custom background.
* **Add Image via URL:** Fetch and use any image directly from a URL.
* **Unsplash Backgrounds:** Browse and apply Unsplash images as backgrounds, visible live in the home preview.
* **Background Presets:** Quickly apply curated background presets for fast results.

### ✍️ Text & Overlays
* **Multi-Text Support:** Add multiple independent text overlays on a single canvas.
* **Heavy Customisation:** Control font, size, color, alignment, background, and more per text element.
* **Edit in Preview:** Tap any text directly in the canvas to edit it inline.
* **Remove Individually:** Dismiss any text overlay with a dedicated × button.
* **Text Background:** Apply a background fill behind text for contrast and legibility.
* **Smart Watermarks:** Toggle a brand watermark that automatically adapts its color (Black/White) based on background luminance.
* **Layering Control:** Move text in front of or behind the device frame.
* **Drag, Snap & Rotate:** Move text overlays freely with snapping to center and alignment guidelines. Rotate with a two-finger gesture.

### 💾 Templates & Presets
* **Save Templates:** Save your designs as reusable templates from the home screen.
* **Preset Gallery:** Pre-built layout and background presets to get started quickly.
* **Full Reset:** Reset individual tabs or the entire canvas with a global reset button.

### 📤 Export & Sharing
* **Pixel-Accurate Export:** Export logic tuned for output that matches the canvas precisely.
* **Multiple Aspect Ratios:** Square, Portrait, and Story formats optimized for social media.
* **Share Flyout:** Multiple share options accessible via a flyout menu.

### 🕓 History
* **Project History:** All designs auto-saved locally via Room Database.
* **Delete Entries:** Remove individual history items.

### ⚙️ Settings & Onboarding
* **Theme Setting:** Switch between Light, Dark, and System theme.
* **First Launch Guide:** Step-by-step onboarding shown on first launch.
* **In-App Release Notes:** Update dialog shows what's new on each version upgrade.

---

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

---

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
│   │   └── tabs/        # Editor tab screens (Background, Frame, Text, etc.)
│   └── theme/           # App's Design System (Colors, Typography, Shapes)
└── MainActivity.kt      # Main entry point of the application
```

---

## ⚙️ Getting Started

### Prerequisites
* Android Studio Ladybug (2024.2.1) or later
* Android SDK 35 (Target 37)
* JDK 11+

### Build & Run
1. Clone the repo: `git clone https://github.com/Pankaj-Meharchandani/Skreenup.git`
2. Open in Android Studio.
3. Sync Gradle and run on a device/emulator with API 30+.

---

## 🛡 Permissions
| Permission | Usage |
|------------|-------|
| `INTERNET` | Required for fetching Unsplash images and remote backgrounds. |

---

## 📄 License
This project is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for details.
