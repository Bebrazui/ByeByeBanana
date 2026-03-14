# ByeByeBanana (Android)

English | [Русский](README.md) | [Türkçe](README-tr.md)

Local Android client for ByeDPI. The app runs a local proxy and routes traffic through it. It is **not** a remote VPN server and does not send traffic anywhere.

## Features
- Auto‑enable for whitelist apps (Accessibility, strict mode, keep‑alive)
- In‑app logs viewer
- Dynamic theme (Material You) + light/dark
- Per‑app tunneling
- Import/export settings
- VPN and Proxy modes

## Notes
- Approve the VPN permission once via the main button.
- Some OEMs require allowing autostart/background for stability.

## Build
```bash
./gradlew assembleRelease
```
APK: `app/build/outputs/apk/release/`

## Dependencies
- ByeDPI
- hev‑socks5‑tunnel
