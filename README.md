# ByeByeBanana (Android)

Русский | [English](README-en.md) | [Türkçe](README-tr.md)

Локальный Android‑клиент для ByeDPI. Приложение поднимает локальный прокси и перенаправляет трафик через него. Это **не** удалённый VPN‑сервер и трафик никуда не отправляется.

## Медиа
![Логотип](docs/images/logo.png)

**Иконка приложения**
![Иконка](docs/images/icon.png)

**Кнопки**
![Включить](docs/images/buttons/btn_on.png)
![Выключить](docs/images/buttons/btn_off.png)

**Загрузка**
[![Скачать APK](docs/images/buttons/btn_download.png)](https://github.com/Bebrazui/ByeByeBanana/releases)

## Возможности
- Автовключение для белого списка приложений (Accessibility, стабильный режим, keep‑alive)
- Встроенный журнал (логи) внутри приложения
- Динамическая тема (Material You) + светлая/тёмная
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

## Зависимости
- ByeDPI
- hev‑socks5‑tunnel
