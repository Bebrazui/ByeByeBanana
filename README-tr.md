# ByeByeBanana (Android)

Türkçe | [Русский](README.md) | [English](README-en.md)

ByeDPI için yerel Android istemcisi. Uygulama yerel bir proxy çalıştırır ve trafiği buradan geçirir. **Uzak VPN sunucusu değildir** ve trafik başka bir yere gönderilmez.

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

## Bağımlılıklar
- ByeDPI
- hev‑socks5‑tunnel
