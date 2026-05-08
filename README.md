# AniMex — Android Anime Streaming App

Monochrome + Red Accents | Light & Dark Mode | Live Comments

## Features
- **Home** — Latest anime dari Otakudesu
- **Ongoing** — Anime tayang dengan paginasi
- **Completed** — Anime selesai dengan paginasi
- **Search** — Pencarian real-time
- **Genre** — Browse berdasarkan genre
- **Streaming** — WebView dengan streaming_url
- **Live Comments** — Real-time via Supabase (polling 8 detik)
- **Dark/Light Mode** — Toggle di toolbar
- **Dan masih ada lainnya lagi

## Setup

### 1. Buka di Android Studio
File > Open > pilih folder `AniMex/`

### 2. Setup Supabase Database
Buka Firebase Console > Database > Realtime Database > Rules > Beri true untuk read and write pada collection `comments`

### 3. Build & Run
- Min SDK: 23 (Android 6.0)
- Target SDK: 34 (Android 14)
- Jalankan di emulator/device

## Tech Stack
- **Retrofit2** — API calls ke apinime.tineo.my.id
- **OkHttp** — Supabase REST API
- **Glide** — Image loading
- **WebView** — Video streaming
- **Material Components** — UI design
- **Supabase** — Live comments database


### Copyright (c) 2026 Rabbani. All Rights Reserved.
[MIT](/LICENSE)
