# ByeByeBanana (Android)

Türkçe | [Русский](README.md) | [English](README-en.md)

ByeDPI için yerel Android istemcisi. Uygulama yerel bir proxy çalıştırır ve trafiği buradan geçirir. **Uzak VPN sunucusu değildir** ve trafik başka bir yere gönderilmez.

## Medya
![Logo](docs/images/logo.png)

**Uygulama ikonu**
![Ikon](docs/images/icon.png)

**Butonlar**
![Başlat](docs/images/buttons/btn_on.png)
![Durdur](docs/images/buttons/btn_off.png)

**İndirme**
[![APK İndir](docs/images/buttons/btn_download.png)](https://github.com/Bebrazui/ByeByeBanana/releases)

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

## Bağımlılıklar
- ByeDPI
- hev‑socks5‑tunnel
