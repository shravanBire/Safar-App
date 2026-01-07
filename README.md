# 🚴‍♂️ Safar — Smart Bike Tracking & Journey Analytics System

Safar is an end-to-end **smart bike tracking system** that combines an **Android application** with an **ESP32-based IoT device** to record, store, and visualize bike journeys in real time.

The project is designed as a **full-stack system** involving:
- Embedded systems (ESP32 + GPS + GSM)
- Cloud backend (Supabase REST API)
- Modern Android app (Jetpack Compose + MVVM)

## 📌 Project Overview

Safar helps users:
- Track bike movement using GPS
- Store live location data on the cloud
- View trips, distance, duration, and routes in an Android app
- Analyze ride history in a clean and modern UI

This project was built with **scalability, reliability, and real-world constraints** in mind (network drops, power saving, movement detection).

## 🗂️ Repository Structure
Safar-App/
│
├── android-app/
│ └── Android application (Jetpack Compose, MVVM)
│
└── safar-Tracker/
└── ESP32_tracker.ino # ESP32 firmware

## 📱 Android Application (`android-app/`)

### 🔧 Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **Design:** Custom light/dark theme
- **State Handling:** ViewModel + State

### ✨ Features
- Home screen with:
  - Map view
  - Circular speedometer
- Trips screen:
  - Date-wise trip selection
  - Distance, duration, and route visualization
- Clean UI built from Figma designs
- Scalable architecture (RoomDB / Retrofit ready)

### 📐 Architecture (MVVM)
UI (Compose Screens)
↓
ViewModel
↓
Repository
↓
Data Source (API / DB)

## 🔌 ESP32 Tracker (`esp32-tracker/`)

### 🔧 Hardware Used
- ESP32
- NEO-6M GPS Module
- SIM800L GSM/GPRS Module
- External power from bike battery (planned)

### 📚 Libraries
- TinyGPS++
- HardwareSerial (ESP32)

### ⚙️ Key Features
- Real-time GPS tracking
- Movement detection using distance and speed
- Adaptive data upload intervals:
  - Every 3 seconds when moving
  - Progressive intervals when stationary  
    (5 min → 15 min → 30 min → 1 hour)
- Robust GPRS handling with auto reconnect
- HTTP POST data upload to Supabase

### 📦 Data Format (Sent to Backend)
```json
{
  "device_id": "bike_001",
  "latitude": 18.520430,
  "longitude": 73.856744,
  "speed": 24.5
}

### ☁️ Backend (Supabase)
REST-based API
PostgreSQL database
Stores GPS location and trip data
Serves data to the Android app

### 🚀 Future Enhancements
Live bike tracking on map
Trip analytics and statistics
Power optimization for ESP32
Offline data buffering
PDF trip reports
Real-time updates using MQTT/WebSockets
Thermal printer integration (planned)

### 🤝 Contributors
- 👨‍💻 Shravan Bire
  Android App Development
  System Architecture
  Android ↔ ESP32 data flow design
  GitHub: https://github.com/shravanBire
- 👨‍💻 Parth
  ESP32 Firmware Development
  GPS & SIM800L integration
  Network reliability and optimization
  GitHub: https://github.com/ParthCh300x

### 🧠 Why This Project Matters
Safar demonstrates:
Embedded systems + mobile app integration
Real-world IoT challenges (network, power, movement)
Clean Android architecture (MVVM)
End-to-end system design thinking
