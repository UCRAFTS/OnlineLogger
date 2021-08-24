OnlineLogger
=

Velocity Плагин отдающий записывающий онлайн серверов в MySQL и Redis. Необходим при использовании нескольких прокси-серверов. 

## Зависимости:
* MySQL 8+
* Redis


## Конфигурация
Идентификатор | Значение
---|---
`db.host` | Адрес базы данных
`db.port` | Порт базы данных
`db.user` | Пользователь базы данных
`db.pass` | Пароль базы данных
`db.base` | Наименование базы данных
`db.poolSize` | Размер пула подключений к БД
`db.tablesPrefix` | Префикс таблиц в БД
`redis.host` | Адрес Redis
`redis.port` | Порт Redis
`redis.timeout` | Максимальное время ожидания Redis
`redis.pass` | Пароль от Redis, при наличии
`redis.serversOnline` | Индекс БД в Redis
`redis.poolSize` | Размер пула подключений к Redis
`settings.period` | Время периода в минутах записи онлайна
`settings.proxyName` | Наименование текущего прокси-сервера