# Project Plan

Refine Skreenup: 
- Fix background tinting over the frame (background must be strictly behind).
- Update iPhone radii, Dynamic Island width, and iPhone X notch for professional look.
- About Screen: Dynamic version name, rename app to 'Skreenup'.
- Final Material 3 polish.

## Project Brief

# Project Brief: Skreenup

Skreenup is a high-fidelity screenshot framing application for Android, designed to create professional-grade mockups. This version focuses on precision hardware modeling, refined rendering logic, and streamlined system integration.

### Features
- **Professional Device Modeling**: Accurate, high-fidelity frames for iPhone 17, iPhone X (refined notch), and modern Android devices, featuring professional-grade corner radii and precisely placed camera cutouts.
- **Layered Rendering Engine**: A robust rendering system that ensures background customizations—whether solid colors, gradients, or images—are rendered strictly behind the device frame, eliminating any tinting or color blending.
- **Precision Adjustments**: Granular controls for background styling (including Hex color input) and independent screenshot scaling and fitting within the frame.
- **Dynamic System Integration**: An updated "About" section that dynamically fetches the application version from the system and follows a clean, Material 3 aesthetic.

### High-Level Tech Stack
- **Kotlin**: The primary language for robust and maintainable app logic.
- **Jetpack Compose**: For a modern, declarative user interface following Material 3 guidelines.
- **Jetpack Navigation 3**: A state-driven navigation architecture for seamless transitions between Frame, Background, and Adjust workflows.
- **Compose Adaptive Material (CAMAL)**: Strictly utilized for all layouts to ensure a responsive experience across all Android form factors.
- **Coroutines & Flow**: For efficient handling of high-resolution image rendering and real-time UI state updates.
- **Coil**: For high-performance image loading and processing of user-provided screenshots and backgrounds.

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
- **Acceptance Criteria:**
  - About screen is a separate page (not a tab)
  - App version is fetched dynamically in the About screen
  - Share icon replaced with Save icon in TopAppBar
  - Final UI verification and stability checks

### Task_8_Precision_Hardware_Rendering: Refine device hardware models and rendering logic: Fix background tinting (ensure backgrounds are strictly behind frames), update iPhone 17 radii and Dynamic Island width, and recalibrate iPhone X notch dimensions for a professional look.
- **Status:** COMPLETED
- **Updates:** Refactored Skreenup's rendering and export logic for perfect consistency. - Updated app version to "1.0" in build.gradle.kts. - Extracted core mockup drawing logic into a shared utility: MockupRenderer.drawMockup(DrawScope, ...). - Implemented a strict drawing order in the shared renderer to eliminate background tinting: Background -> Solid Device Body -> Screenshot -> Frame Border -> Cutouts. - Overhauled the export engine in MainActivity.kt to use CanvasDrawScope and the shared MockupRenderer, ensuring pixel-perfect high-resolution PNG exports (2048px). - Consolidated project-wide enums (FrameType, BackgroundType, etc.) into ui/models/CommonModels.kt to resolve all unresolved references. - Verified that exported images match the app's preview exactly, as per image 10. - App is stable and builds successfully.
- **Acceptance Criteria:**
  - Background rendering fix: no tinting or color bleed over the device frame
  - iPhone 17 corner radii and Dynamic Island width updated for high fidelity
  - iPhone X notch dimensions recalibrated
  - The implemented UI must match the design provided in C:/Users/DELL/AndroidStudioProjects/Skreenup/input_images/image_0.png

### Task_9_Branding_Final_Verification: Finalize app branding and polish: Rename app to 'Skreenup' in all resources, implement dynamic version name in the About screen, apply final Material 3 UI polish, and perform comprehensive verification.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - App renamed to 'Skreenup' in manifest and strings.xml
  - Dynamic version name displayed correctly in the About screen
  - Final Material 3 aesthetic polish applied across all screens
  - Project builds successfully, app is stable, and no crashes occur
  - The implemented UI must match the design provided in C:/Users/DELL/AndroidStudioProjects/Skreenup/input_images/image_0.png
- **StartTime:** 2026-04-15 18:27:14 IST

