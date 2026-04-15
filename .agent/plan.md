# Project Plan

Update Skreenup to v2.1 with:
- Separate About screen and dynamic version fetching.
- Positioning sliders (Up/Down, Left/Right) for both frame and screenshot independently.
- Replace Share icon with Save icon.
- Hex color code input for backgrounds (solid/gradient).
- New device models: Full laptop chassis, iPhone X with notch.
- Refined cutout placements (higher up), larger Samsung dot, and 56pt iPhone radius.
- Scrollable Adjust screen for better accessibility.
- Professional Material 3 aesthetic.

## Project Brief

# Project Brief: Skreenup v2.1

Skreenup v2.1 is an advanced screenshot framing tool for Android, offering professional-grade controls for creating high-fidelity device mockups. This version introduces granular positioning, expanded hardware models, and precise color customization.

### Features

- **Independent Positioning & Scaling**: Dedicated sliders in the Adjust tab allow for independent X/Y movement and scaling of both the device frame and the screenshot inside, ensuring pixel-perfect alignment.
- **Expanded Hardware Library**: Features a diverse range of frames, including full laptop chassis, the classic iPhone X notch style, and refined modern models with accurate 56pt corner radii and camera cutout placements.
- **Precision Color Customization**: Professional background tools including a custom gradient builder and Hex color code text input for both solid and gradient backgrounds.
- **High-Fidelity Export**: A streamlined workflow with a dedicated 'Save' action that exports high-resolution mockups, accurately preserving frame details, transparency, and camera cutouts.

### High-Level Tech Stack
- **Kotlin**: The core language for expressive and safe application logic.
- **Jetpack Compose**: For a modern, declarative UI following Material 3 aesthetics.
- **Jetpack Navigation 3**: A state-driven navigation architecture to manage primary tabs and the separate About screen.
- **Compose Adaptive Material (CAMAL)**: Strictly utilized for all layouts to ensure a responsive and optimized experience across mobile, tablet, and foldable devices.
- **Coroutines & Flow**: For efficient handling of high-resolution image rendering and real-time UI state updates.
- **Coil**: For high-performance loading and processing of user-imported images and backgrounds.

## Implementation Steps

### Task_1_Setup_Navigation: Establish the project foundation with a vibrant Material 3 theme, Edge-to-Edge display, and Navigation 3 architecture featuring the bottom navigation bar with four tabs: Frame, Background, Adjust, and About.
- **Status:** COMPLETED
- **Acceptance Criteria:**
  - Material 3 theme with energetic color scheme implemented
  - Edge-to-Edge support enabled
  - Navigation 3 with 4-tab Bottom Navigation functional

### Task_2_Frame_Selection_Import: Implement the 'Frame' tab featuring a horizontal list of high-fidelity device models (iPhone 17 Pro/Air, Galaxy S26 series, Laptops) and the screenshot import logic via PhotoPicker to render the image within the selected frame.
- **Status:** COMPLETED
- **Acceptance Criteria:**
  - Horizontal device selection list implemented
  - Screenshot import via PhotoPicker works and displays inside the frame
  - The implemented UI must match the design provided in C:/Users/DELL/AndroidStudioProjects/Skreenup/input_images/image_0.png

### Task_3_Background_Adjust_Controls: Develop the 'Background' tab (supporting Solid colors, Gradients, and User Images) and the 'Adjust' tab (providing scaling controls and export ratio selection).
- **Status:** COMPLETED
- **Acceptance Criteria:**
  - Background customization (Solid/Gradient/Image) applied to preview
  - Frame scaling logic implemented
  - Export ratio selection (1:1, 16:9) updates preview canvas
  - The implemented UI must match the design provided in C:/Users/DELL/AndroidStudioProjects/Skreenup/input_images/image_0.png

### Task_4_v2_Rendering_Refinement: Refine Skreenup v2 rendering: Implement system-based theme, update device models (iPhone 17 Pro Max res: 2868x1320, Dynamic Island 15x5mm, Samsung dot cutouts), and fix the rendering engine (Background behind frame, proportional scaling, 'fit' image logic).
- **Status:** COMPLETED
- **Acceptance Criteria:**
  - System-based light/dark theme active (manual toggle removed)
  - Device models updated (iPhone 17 Pro Max, correct Dynamic Island/dot cutouts)
  - Rendering engine fixed: Background is behind the frame
  - Images 'fit' inside display area without stretching
  - Proportional frame scaling preserves corner radii and cutouts
  - The implemented UI must match the design provided in C:/Users/DELL/AndroidStudioProjects/Skreenup/input_images/image_0.png

### Task_5_v2_About_Export_Final: Finalize v2: Implement the detailed About screen, high-resolution export, and adaptive app icon. Conduct final verification of app stability and UI fidelity.
- **Status:** COMPLETED
- **Acceptance Criteria:**
  - About screen includes Logo, Name, Developer (Pankaj-Meharchandani), GitHub link, and Version
  - High-resolution export functional and preserves camera cutouts
  - Adaptive app icon integrated
  - Project builds successfully, app does not crash, and all existing tests pass
  - The implemented UI must match the design provided in C:/Users/DELL/AndroidStudioProjects/Skreenup/input_images/image_0.png

### Task_6_v2_1_Hardware_Controls: Implement v2.1 precision controls and hardware: Add independent X/Y positioning sliders for frame and screenshot, Hex color input for backgrounds, and new device models (Full Laptop chassis, iPhone X with notch) with refined 56pt radii and cutout placements.
- **Status:** COMPLETED
- **Updates:** Implemented v2.1 precision controls and hardware. Added independent X/Y sliders for frame and screenshot. Added Hex color input for backgrounds. Refined iPhone radii to 56pts. Moved cutouts higher up and made Samsung dot larger. Added iPhone X notch and full Laptop chassis models. Made Adjust screen scrollable.
- **Acceptance Criteria:**
  - Independent X/Y sliders for frame and screenshot functional
  - Hex color code input working for solid/gradient backgrounds
  - Laptop and iPhone X (notch) frames implemented
  - iPhone radius refined to 56pt and cutouts positioned accurately

### Task_7_v2_1_UI_Final_Verify: Final v2.1 polish: Separate the About screen from the bottom navigation (separate page), implement dynamic version fetching, replace the Share icon with a Save icon, and perform final stability and UI verification.
- **Status:** COMPLETED
- **Updates:** Final v2.1 polish completed.
- Overhauled navigation to separate the About screen (not a tab, but a separate page).
- Implemented dynamic version fetching from PackageInfo in the About screen.
- Replaced the Share icon with a Save icon in the TopAppBar.
- Verified all v2.1 features (positioning sliders, hex input, new models).
- Confirmed the Adjust screen is fully scrollable and stable.
- Performed final UI and stability checks.
- **Acceptance Criteria:**
  - About screen is a separate page (not a tab)
  - App version is fetched dynamically in the About screen
  - Share icon replaced with Save icon in TopAppBar
  - Final UI verification and stability checks
- **Duration:** N/A

