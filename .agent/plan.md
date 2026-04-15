# Project Plan

Update Skreenup to v2 with:
- System-based light/dark theme (remove manual toggle).
- About Screen with App Logo, App Name, Developer Name (Pankaj-Meharchandani), GitHub link, Version.
- Rendering Fixes: Background BEHIND frame, proportional mockup scaling (including radius/cutouts), image 'fit' (not stretch), and independent image scaling.
- Device Updates: iPhone Dynamic Island (15mm x 5mm), Samsung dot cutouts, rename 'iPhone 17 Air' to 'iPhone 17', replace 'iPhone 17 Pro' with 'iPhone 17 Pro Max' (2868x1320 res, 163.4x78mm).
- Professional Material 3 aesthetic.

## Project Brief

# Project Brief: Skreenup v2

Skreenup v2 is a professional-grade screenshot framing utility for Android, refined for precision and high-fidelity rendering. It allows users to create marketing-ready mockups with accurate device dimensions and sophisticated background layers.

### Features
- **Precision Device Models**: High-fidelity frames for the iPhone 17 Pro Max, iPhone 17, and Galaxy S26 series, featuring accurate physical dimensions and realistic camera cutouts (Dynamic Island and single-dot implementations).
- **Advanced Rendering Engine**: A layered rendering system that ensures background customizations (solid colors, custom gradients, or user-uploaded images) are correctly displayed behind the device frame.
- **Intelligent Mockup Scaling**: A proportional scaling system that preserves critical frame details—such as corner radii and camera islands—while providing independent controls to scale and 'fit' the screenshot within the display area.
- **Adaptive Material 3 Experience**: A fully responsive interface that automatically adapts to system light/dark themes, featuring a streamlined bottom navigation and a detailed About section for developer attribution.

### High-Level Tech Stack
- **Kotlin**: The core language for expressive and safe application logic.
- **Jetpack Compose**: For a modern, declarative UI following the latest Material 3 guidelines.
- **Jetpack Navigation 3**: A state-driven navigation strategy to manage the Frame, Background, Adjust, and About destinations.
- **Compose Adaptive Material (CAMAL)**: The primary library for ensuring a responsive, adaptive layout across different Android screen sizes and orientations.
- **Coroutines & Flow**: For managing high-resolution image processing and UI state updates asynchronously.
- **Coil**: For efficient loading and caching of screenshots and user-provided backgrounds.

## Implementation Steps

### Task_1_Setup_Navigation: Establish the project foundation with a vibrant Material 3 theme, Edge-to-Edge display, and Navigation 3 architecture featuring the bottom navigation bar with four tabs: Frame, Background, Adjust, and About.
- **Status:** COMPLETED
- **Updates:** Established the project foundation for Skreenup's new UI.
- **Acceptance Criteria:**
  - Material 3 theme with energetic color scheme implemented
  - Edge-to-Edge support enabled
  - Navigation 3 with 4-tab Bottom Navigation functional

### Task_2_Frame_Selection_Import: Implement the 'Frame' tab featuring a horizontal list of high-fidelity device models (iPhone 17 Pro/Air, Galaxy S26 series, Laptops) and the screenshot import logic via PhotoPicker to render the image within the selected frame.
- **Status:** COMPLETED
- **Updates:** Implemented the 'Frame' tab for Skreenup.
- **Acceptance Criteria:**
  - Horizontal device selection list implemented
  - Screenshot import via PhotoPicker works and displays inside the frame
  - The implemented UI must match the design provided in C:/Users/DELL/AndroidStudioProjects/Skreenup/input_images/image_0.png

### Task_3_Background_Adjust_Controls: Develop the 'Background' tab (supporting Solid colors, Gradients, and User Images) and the 'Adjust' tab (providing scaling controls and export ratio selection).
- **Status:** COMPLETED
- **Updates:** Developed the 'Background' and 'Adjust' tabs for Skreenup.
- **Acceptance Criteria:**
  - Background customization (Solid/Gradient/Image) applied to preview
  - Frame scaling logic implemented
  - Export ratio selection (1:1, 16:9) updates preview canvas
  - The implemented UI must match the design provided in C:/Users/DELL/AndroidStudioProjects/Skreenup/input_images/image_0.png

### Task_4_v2_Rendering_Refinement: Refine Skreenup v2 rendering: Implement system-based theme, update device models (iPhone 17 Pro Max res: 2868x1320, Dynamic Island 15x5mm, Samsung dot cutouts), and fix the rendering engine (Background behind frame, proportional scaling, 'fit' image logic).
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - System-based light/dark theme active (manual toggle removed)
  - Device models updated (iPhone 17 Pro Max, correct Dynamic Island/dot cutouts)
  - Rendering engine fixed: Background is behind the frame
  - Images 'fit' inside display area without stretching
  - Proportional frame scaling preserves corner radii and cutouts
  - The implemented UI must match the design provided in C:/Users/DELL/AndroidStudioProjects/Skreenup/input_images/image_0.png
- **StartTime:** 2026-04-15 16:15:28 IST

### Task_5_v2_About_Export_Final: Finalize v2: Implement the detailed About screen, high-resolution export, and adaptive app icon. Conduct final verification of app stability and UI fidelity.
- **Status:** PENDING
- **Acceptance Criteria:**
  - About screen includes Logo, Name, Developer (Pankaj-Meharchandani), GitHub link, and Version
  - High-resolution export functional and preserves camera cutouts
  - Adaptive app icon integrated
  - Project builds successfully, app does not crash, and all existing tests pass
  - The implemented UI must match the design provided in C:/Users/DELL/AndroidStudioProjects/Skreenup/input_images/image_0.png

