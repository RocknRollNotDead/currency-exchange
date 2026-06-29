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

## Как запускал

### 1. Заход в Ubuntu

- Арендовал vps сервер с Ubuntu (самый дешёвый) на одном из российских провайдеров - Beget Cloud, Timeweb Cloud, Selectel и др. 
- Получил данные для входа в виде ssh login@000.000.000.000 и password, где вместо login - выданный логин, вместо 0.0.0.0 выданный ip адрес, а вместо password - выданный пароль
- Открыл командную строку БЕЗ имени администратора и ввёл 'ssh login@000.000.000.000' * Enter * и потом password: 'mypassword'

### 2. Настройка Java и Tomcat Manager

**2.1. Установил JDK**
```bash
apt update && apt upgrade -y
apt install -y openjdk-21-jre-headless
```

**2.2. Установил Tomcat**

В `apt` уже есть Tomcat 9, а нужен Tomcat 10. Проверил версию на https://tomcat.apache.org/download-10.cgi и задал такие команды на установку Tomcat: (на 29.06.2026 самая новая версия - 10.1.56)

```bash
cd /opt
wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.56/bin/apache-tomcat-10.1.56.tar.gz
tar -xzf apache-tomcat-10.1.56.tar.gz
mv apache-tomcat-10.1.56 tomcat
```

**2.3. Создал пользователя Tomcat**

```bash
useradd -m -d /opt/tomcat -U -s /bin/false tomcat
chown -R tomcat:tomcat /opt/tomcat
```

**2.4. Директория**

Создал директорию, куда мой проект создаст файл database.db

```bash
mkdir -p /var/lib/currency-exchange
chown tomcat:tomcat /var/lib/currency-exchange
```

**2.5. Добавил админа для Tomcat Manager**

Чтобы не загружать файлы через Linux и командную строку, можно воспользоваться веб интерфейсом Tomcat, для возможности этого нужно создать пользователя, набрав `/opt/tomcat/conf/tomcat-users.xml` (этим открывается файл):

В нём вместо этого:
```xml
....
</tomcat users>
```

Делаем это
```xml
....
<role rolename="manager-gui"/>
<user username="admin" password="mypassword" roles="manager-gui"/>
</tomcat users>
```
(вместо mypassword вставляем свой придуманный пароль)

> В Tomcat в `webapps/manager/META-INF/context.xml` может быть открыто (не закомментировано) значение `RemoteAddrValve`/`RemoteCIDRValve` которое ограничивает доступ к Manager, и его можно открыть только в убунту по адресу `localhost`, я проверил и закомментировал, если он был не закомментирован.

**2.6. Задал путь к базе данных через env**

```bash
cat > /opt/tomcat/bin/setenv.sh << 'EOF'
export DB_PATH=/var/lib/currency-exchange/database.db
EOF
chmod +x /opt/tomcat/bin/setenv.sh
chown -R tomcat:tomcat /opt/tomcat
```

`setenv.sh` ставит переменные окружения в Tomcat

**2.7. Открыл порт 8080**

```bash
ufw allow 8080/tcp
```

**2.8. Запустить Tomcat от пользователя tomcat**

```bash
su -s /bin/bash tomcat -c /opt/tomcat/bin/startup.sh
```

В конце присланного сообщения увидел `Tomcat started.`
Но на всякий случай лучше проверить:
```bash
ps aux | grep tomcat              # процесс запущен
ss -tlnp | grep 8080               # порт слушается
```

### 3. Загрузка `.war` в Manager

По ссылке `http://000.000.000.000:8080/manager/html` открывается Manager Tomcat, и вводим логин и пароль из `<user username="admin" password="mypassword" roles="manager-gui"/>` (из того файла `/opt/tomcat/conf/tomcat-users.xml`).


Нашёл раздел **"WAR file to deploy"** и кнопку "Выбрать файл".
После этого в app.js поменял ссылку
```javascript
const host = "http://localhost:8080/rates"
 ```
на
```javascript
`"http://000.000.000.000:8080/currency-exchange"
```
и после этого собрал .war - `mvn clean package` в IntelliJ IDEA

выбрать собранный `.war` из папки target, переименовал из currency-exchange-1.0-SNAPSHOT в currency-exchange для нормального названия в строке браузера → нажал **"Развернуть"**.

После этого приложение должно появиться в списке в начале страницы со статусом `running` (`true`), а также развернуться по адресу:
```
http://000.000.000.000:8080/currency-exchange/
```

**3.1. Поправить адрес бэкенда в app.js**

Если открылось, но в F12 на вкладке Network ошибка ERR_CONNECTION_REFUSED, значит что-то не так со ссылкой `const host = "http://localhost:8080/rates"`

Чтобы обновить её на рабочую ссылку прямо на сервере, нужно открыть файл прямо в командной строке:
```bash
/opt/tomcat/webapps/currency-exchange/js/app.js
```
и попровить вручную на `const host = "http://000.000.000.000:8080/currency-exchange"`.

## О коммитах

Если зайти в историю коммитов — там используется особый паттерн [«garbage](https://habr.com/ru/companies/softonit/articles/892386/)[ dump»](https://matklad.github.io/2021/05/12/design-pattern-dumping-ground.html). Мой пет-проект стал лучше благодаря этому прекрасному паттерну. С этим паттерном мои навыки в гит значительно увеличились.

