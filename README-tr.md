# ByeByeBanana (Android)

Türkçe | [Русский](README.md) | [English](README-en.md)

ByeDPI için yerel Android istemcisi. Uygulama yerel bir proxy çalıştırır ve trafiği buradan geçirir. **Uzak VPN sunucusu değildir** ve trafik başka bir yere gönderilmez.
Orijinal ByeByeDPI Android projesinin forkudur.

<p align="center">
  <img src="docs/images/logo_round.png" alt="ByeByeBanana icon" width="160" />
</p>

<p align="center">
  <a href="https://github.com/Bebrazui/ByeByeBanana/releases">
    <img src="docs/images/buttons/btn_download.png" alt="APK İndir" width="220" />
  </a>
</p>

## Özellikler
- Beyaz liste için otomatik etkinleştirme (Accessibility, sıkı mod, keep‑alive)
- Uygulama içi günlükler
- Dinamik tema (Material You) + açık/koyu
- Uygulama bazlı tünelleme
- Ayarları içe/dışa aktarma
- VPN ve Proxy modları

## Notlar
- VPN iznini bir kez ana düğmeden onaylayın.
- Bazı cihazlarda arka plan/otomatik başlatma izinleri gerekir.

## Derleme
```bash
./gradlew assembleRelease
```
APK: `app/build/outputs/apk/release/`

## İmzalama
Release imzalama için keystore gerekir. Örnek:
```bash
keytool -genkeypair -v -keystore release.jks -alias byebd -keyalg RSA -keysize 2048 -validity 10000
```
Sonra `local.properties` içinde veya `GRADLE_OPTS` ile ayarlayın:
```properties
KEYSTORE_FILE=release.jks
KEYSTORE_PASSWORD=your_password
KEY_ALIAS=byebd
KEY_PASSWORD=your_password
```
Sertifika SHA‑256 için:
```bash
keytool -list -v -keystore release.jks -alias byebd | rg -n \"SHA256\"
```
SHA‑256: 98:F0:A2:B8:90:B7:C3:E1:78:5D:18:24:7D:E1:52:B4:44:C8:C6:EE:42:6C:BB:7B:1E:61:4E:47:9F:40:E3:20

## Bağımlılıklar
- ByeDPI
- hev‑socks5‑tunnel
