# План новой структуры поста

Принцип структуры:

- `##` — конкретная зависимость или стек зависимостей проекта.
- `###` — условие в формате «что может пойти не так, если у вас есть эта зависимость».
- Внутри каждого `###`-раздела держать один и тот же шаблон:
  - симптомы;
  - почему проблема возникает и как её исправить;
  - код для Spring Boot 3, на котором проблема проявляется;
  - код для Spring Boot 4, который уже работает корректно.

## Kotlin (`org.jetbrains.kotlin:*`)

### Если у вас есть Kotlin-код поверх Spring API, но нет явных nullability-ограничений в generic-обёртках

- `WebTestClient.ResponseSpec.responseBody()`
- `JsonbToObjectReader<T>`
- фабрики DTO и generic utility-функции с `T : Any`

### Если у вас есть Kotlin, но код раньше неявно полагался на platform types Spring Framework

- неоднозначные перегрузки `ResponseEntity(...)`
- неоднозначные перегрузки `queryParam(...)`
- `URI?` vs `URI`
- места с вынужденным `!!`, которые после JSpecify приходится расставлять осознанно

## Spring Data JDBC (`org.springframework.data:spring-data-jdbc`, `org.springframework.boot:spring-boot-starter-data-jdbc`)

### Если у вас есть Spring Data JDBC, но ваши кастомные конфигурации всё ещё живут на старом `Dialect`

- `Dialect -> JdbcDialect`
- `JdbcPostgresDialect.INSTANCE`
- сигнатуры `DataAccessStrategy` и смежных бинов

### Если у вас есть Spring Data JDBC, но вы вручную собираете `DefaultDataAccessStrategy` по старому конструктору

- новый аргумент `DefaultQueryMappingConfiguration`
- ручная инфраструктура вокруг `SqlGeneratorSource`, `InsertStrategyFactory`, `SqlParametersFactory`

### Если у вас есть Spring Data JDBC, но вы опирались на старые исключения и встроенные конвертеры

- исчезновение `DbActionExecutionException`
- возврат собственного конвертера `Timestamp -> Temporal`
- регистрация конвертера в `JdbcCustomConversions`

### Если у вас есть Spring Data JDBC, но вы ожидаете, что аудитинг и callback-и будут вести себя как раньше

- `@CreatedDate`
- `@LastModifiedDate`
- `modifyOnCreate = false`
- ручная сборка `JdbcAggregateOperations`
- переход на `JdbcAggregateTemplate`
- фикстуры с историческими датами

## Spring Security (`org.springframework.security:spring-security-config`, `org.springframework.boot:spring-boot-starter-security`)

### Если у вас есть тестовая security-конфигурация, но `DaoAuthenticationProvider` всё ещё собирается по старому API

- конструктор теперь принимает `UserDetailsService`
- `PasswordEncoder` задаётся через `setPasswordEncoder(...)`

## Jackson (`tools.jackson:*`, `org.springframework.boot:spring-boot-starter-json`)

### Если у вас есть Jackson, но код и зависимости всё ещё живут в namespace `com.fasterxml.jackson.*`

- переход на `tools.jackson.*`
- чистка `compileClasspath` и `testCompileClasspath`
- временный карантин с `runtimeOnly` для Jackson 2

### Если у вас есть Jackson, но ваши кастомные десериализаторы и модули написаны под Jackson 2 SPI

- `JsonDeserializer -> ValueDeserializer`
- `parser.codec.readTree(...) -> parser.readValueAsTree<JsonNode>()`
- ручная регистрация `SimpleModule`
- удаление лишней ручной регистрации `JavaTimeModule`

### Если у вас есть Jackson, но ваши DTO держатся на legacy JSON-контрактах и неявной десериализации

- пропавшие дефолты примитивов
- дубли имён свойств
- более жёсткий приоритет `@JsonFormat`
- сериализация boolean-полей с префиксом `is`
- `@JsonProperty("isDeleted")`
- secondary constructor для обратной совместимости
- `@JsonCreator`-фабрики для старых форматов JSON

## Logback Encoder (`net.logstash.logback:logstash-logback-encoder`)

### Если у вас есть logstash-logback-encoder, но версия encoder-а осталась на линии Jackson 2

- `8.x -> 9.0`
- несовместимость со стеком Jackson 3

## Spring Test (`org.springframework:spring-test`, `org.springframework.boot:spring-boot-starter-test`)

### Если у вас есть Spring Test bean overrides, но тесты всё ещё используют старый `@SpyBean`

- `@SpyBean -> @MockitoSpyBean`
- перенос spy прямо в тест вместо отдельного `@TestConfiguration`

## Tomcat (`org.springframework.boot:spring-boot-starter-tomcat`)

### Если у вас есть кастомизация embedded Tomcat, но код завязан на старый пакет `ConfigurableTomcatWebServerFactory`

- переезд класса в `org.springframework.boot.tomcat.*`
- случаи, где одного изменения импорта недостаточно

## Logback Access (`ch.qos.logback.access:*`)

### Если у вас есть logback-access-интеграция, но кастомизация Tomcat завязана на старый Tomcat-specific API

- `WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory>`
- fallback на `WebServerFactoryCustomizer<WebServerFactory>`
- вызов `addEngineValves(...)` через reflection

## Spring Web MVC (`org.springframework:spring-web`, `org.springframework.boot:spring-boot-starter-web`)

### Если у вас есть Spring Web MVC, но код опирается на старые удобные перегрузки Web API

- `ResponseEntity(...)`
- `UriBuilder.queryParam(...)`
- compile-time неоднозначности после уточнения nullability

### Если у вас есть Spring Web MVC, но `RestTemplate`/HTTP-клиент на Apache HttpClient 5 настроен по старому API

- `HttpComponentsClientHttpRequestFactory`
- `setConnectTimeout(...) -> setConnectionRequestTimeout(...)`

## Spring OAuth2 Client (`org.springframework.boot:spring-boot-starter-oauth2-client`)

### Если у вас есть Spring OAuth2 Client, но вы рассчитываете, что oversized multipart по-прежнему вернётся как аккуратный `413`

- `OAuth2AuthorizationCodeGrantFilter`
- `MaxUploadSizeExceededException`
- `SizeLimitExceededException`
- ранний фильтр, который вручную возвращает `413 Payload Too Large`

## Spring Retry (`org.springframework.retry:spring-retry`, `org.springframework:spring-resilience`)

### Если у вас есть аннотационные ретраи, но код всё ещё собран вокруг `spring-retry`

- `@EnableRetry -> @EnableResilientMethods`
- `org.springframework.retry.annotation.* -> org.springframework.resilience.annotation.*`
- `maxAttemptsExpression -> maxRetriesString`
- `backoff(...) -> delayString`
- `retryFor = [...] -> @Retryable(Exception::class)`

## Bucket4j (`com.giffing.bucket4j.spring.boot.starter:bucket4j-spring-boot-starter`)

### Если у вас есть Bucket4j starter, но его версия не совместима со Spring Boot 4

- `0.12.8 -> 0.14.0`
- падение автоконфигурации фильтров при `bucket4j.enabled=true`

## Testcontainers (`org.testcontainers:*`, `org.springframework.boot:spring-boot-testcontainers`)

### Если у вас есть Testcontainers, но зависимости всё ещё подключены по старым артефактам

- `junit-jupiter -> testcontainers-junit-jupiter`
- `postgresql -> testcontainers-postgresql`
- обновление импортов и точек подключения контейнеров

## Selenium (`org.testcontainers:testcontainers-selenium`)

### Если у вас есть Selenium/e2e-тесты на Testcontainers, но модуль браузерного контейнера остался на старой линии артефактов

- `selenium -> testcontainers-selenium`
- `org.testcontainers.containers.BrowserWebDriverContainer -> org.testcontainers.selenium.BrowserWebDriverContainer`
- `NoClassDefFoundError` на shaded-классах

## RestAssured (`io.rest-assured:rest-assured`)

### Если у вас есть RestAssured, но версия всё ещё тащит проблемный Groovy-слой

- `5.x -> 6.0.0`
- `NullPointerException` внутри Groovy meta-class при выполнении запроса

## WireMock (`org.wiremock:wiremock-jetty12`)

### Если у вас есть WireMock, но версии Jetty в тестовом classpath расходятся с теми, что притаскивает Spring Boot 4

- `NoSuchMethodError` на Jetty API
- явная фиксация `jetty-bom` и `jetty-ee10-bom` на совместимую ветку

## Spring Rest Docs (`org.springframework.restdocs:spring-restdocs-*`)

### Если у вас есть Spring Rest Docs, но рядом со Spring Boot 4 всё ещё остаётся линия 3.x

- `IncompatibleClassChangeError` на HTTP-типах
- `4.0.0`
- затем отказ от ручного version pinning в пользу Boot BOM
