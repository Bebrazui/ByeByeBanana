# ByeByeBanana (Android)

Русский | [English](README-en.md) | [Türkçe](README-tr.md)

Локальный Android‑клиент для ByeDPI. Приложение поднимает локальный прокси и перенаправляет трафик через него. Это **не** удалённый VPN‑сервер и трафик никуда не отправляется.
Форк оригинального проекта ByeByeDPI Android.

<p align="center">
  <img src="docs/images/logo_round.png" alt="ByeByeBanana icon" width="160" />
</p>

<p align="center">
  <a href="https://github.com/Bebrazui/ByeByeBanana/releases">
    <img src="docs/images/buttons/btn_download.png" alt="Скачать APK" width="220" />
  </a>
</p>

## Возможности
- Автовключение для белого списка приложений (Accessibility, стабильный режим, keep‑alive)
- Встроенный журнал (логи) внутри приложения
- Динамическая тема
- Раздельное туннелирование по списку приложений
- Импорт/экспорт настроек
- Режимы: VPN и Proxy

## Важно
- Один раз вручную подтвердите VPN‑разрешение (кнопка запуска в приложении).
- На некоторых прошивках нужно разрешить автозапуск и фоновые процессы для стабильной работы.

## Сборка
```bash
./gradlew assembleRelease
```
APK: `app/build/outputs/apk/release/`

## Подпись
Для релизной подписи нужен keystore. Пример:
```bash
keytool -genkeypair -v -keystore release.jks -alias byebd -keyalg RSA -keysize 2048 -validity 10000
```
Далее укажите параметры в `local.properties` или через `GRADLE_OPTS`, например:
```properties
KEYSTORE_FILE=release.jks
KEYSTORE_PASSWORD=your_password
KEY_ALIAS=byebd
KEY_PASSWORD=your_password
```
SHA‑256 сертификата можно получить так:
```bash
keytool -list -v -keystore release.jks -alias byebd | rg -n \"SHA256\"
```
SHA‑256: 98:F0:A2:B8:90:B7:C3:E1:78:5D:18:24:7D:E1:52:B4:44:C8:C6:EE:42:6C:BB:7B:1E:61:4E:47:9F:40:E3:20

## Зависимости
- ByeDPI
- hev‑socks5‑tunnel
