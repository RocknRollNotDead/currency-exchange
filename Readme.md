# Currency Exchange

Третий учебный проект из [роадмапа Сергея Жукова](https://zhukovsd.github.io/java-backend-learning-course/) — «Обмен валют».
[ТЗ проекта](https://zhukovsd.github.io/java-backend-learning-course/projects/currency-exchange/).

## Стек и структура

**Backend** — REST API на чистых HTTP-сервлетах (Jakarta EE, без Spring), в MVC-стиле:
Servlet → Service → DAO, с отдельными слоями моделей, DTO, мапперов (MapStruct) и кастомных исключений.

- Java 17+, Jakarta Servlet API 6.0
- SQLite (через `sqlite-jdbc`) + пул соединений HikariCP
- Gson для сериализации JSON
- MapStruct для маппинга модель ↔ DTO
- Maven, упаковка в `.war`

**Frontend** — взят из репозитория [zhukovsd/currency-exchange-frontend](https://github.com/zhukovsd/currency-exchange-frontend), автор не я, использую как готовый клиент к своему API.

7 явных пакетов - db, DTO, exceptions, mapper, models, services, servlets. Без всякого мусора по типу validators.
Сразу понятна ориентация в коде, можно быстро найти то, что надо и посмотреть как это реализовано. Мне кажется, что больше 10 пакетов - перебор. Человеку сложно ориентироваться в таком количестве пакетов. Это неудобно.


## Функциональность

- CRUD по справочнику валют (`/currencies`, `/currency/{code}`)
- CRUD по курсам обмена (`/exchangeRates`, `/exchangeRate/{codePair}`)
- Расчёт обмена между двумя валютами (`/exchange?from=...&to=...&amount=...`) с тремя сценариями расчёта курса: прямой курс, обратный курс, расчёт через курс к USD
- Единая обработка ошибок через `Filter` → понятные JSON-ответы с корректными HTTP-статусами (400/404/409/500)

## Как запустить

### 1. Подготовка

- JDK 17+
- Maven
- Apache Tomcat 10+ (нужна поддержка Jakarta Servlet API, на Tomcat 9 и ниже проект не соберётся/не запустится)

### 2. Клонировать репозиторий

```bash
git clone https://github.com/RocknRollNotDead/currency-exchange.git
cd currency-exchange
```

### 3. Указать путь к файлу базы данных

Приложение само создаёт схему (`currencies`, `exchange_rates`) при первом старте, если файла БД ещё нет — отдельно накатывать миграции не нужно. Нужно только указать, где именно SQLite-файл должен лежать. Любым из двух способов:

**Через переменную окружения** (приоритетный способ, удобен для хостинга/CI):

```bash
export DB_PATH=/absolute/path/to/database.db
```

**Или через `context-param` в `src/main/webapp/WEB-INF/web.xml`**, если переменная окружения не задана:

```xml
<context-param>
    <param-name>db.path</param-name>
    <param-value>/absolute/path/to/database.db</param-value>
</context-param>
```

### 4. Собрать `.war`

```bash
mvn clean package
```

Артефакт появится в `target/currency-exchange.war`.

### 5. Задеплоить в Tomcat

Скопировать `.war` в `webapps/` вашего Tomcat (или задеплоить через панель управления хостинга, если она поддерживает загрузку `.war`):

```bash
cp target/currency-exchange.war $TOMCAT_HOME/webapps/
```

Tomcat развернёт его автоматически. Приложение будет доступно по адресу `http://<host>:<port>/currency-exchange/`.

### Локальный запуск через IntelliJ IDEA

В репозитории есть конфигурация плагина SmartTomcat (`.smarttomcat/`) — если в IDEA установлен этот плагин, можно запускать проект прямо из IDE без отдельной сборки `.war`, предварительно задав `DB_PATH` в конфигурации запуска.

## О коммитах

Если зайдёте в историю коммитов — там используется особый паттерн [«garbage](https://habr.com/ru/companies/softonit/articles/892386/)[ dump»](https://matklad.github.io/2021/05/12/design-pattern-dumping-ground.html). Мой пет-проект стал лучше благодаря этому прекрасному паттерну. С этим паттерном мои навыки в гит значительно увеличились.

