# ByeByeBanana (Android)

English | [Русский](README.md) | [Türkçe](README-tr.md)

Local Android client for ByeDPI. The app runs a local proxy and routes traffic through it. It is **not** a remote VPN server and does not send traffic anywhere.

## Media
![Logo](docs/images/logo.png)

**App icon**
![Icon](docs/images/icon.png)

**Buttons**
![Start](docs/images/buttons/btn_on.png)
![Stop](docs/images/buttons/btn_off.png)

**Download**
[![Download APK](docs/images/buttons/btn_download.png)](https://github.com/Bebrazui/ByeByeBanana/releases)

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

## Signing
Release signing requires a keystore. Example:
```bash
keytool -genkeypair -v -keystore release.jks -alias byebd -keyalg RSA -keysize 2048 -validity 10000
```
Then set parameters in `local.properties` or via `GRADLE_OPTS`, e.g.:
```properties
KEYSTORE_FILE=release.jks
KEYSTORE_PASSWORD=your_password
KEY_ALIAS=byebd
KEY_PASSWORD=your_password
```
To get the certificate SHA‑256:
```bash
keytool -list -v -keystore release.jks -alias byebd | rg -n \"SHA256\"
```

## Dependencies
- ByeDPI
- hev‑socks5‑tunnel
