package pw.binom.init

import pw.binom.url.toURL

object BinomLibraries {
    val repository = Repository.UrlRepository("https://repo.binom.pw".toURL())
    private const val group = "pw.binom.io"
    private const val version = "1.0.0-SNAPSHOT"
    private fun lib(name: String) = Library(group = group, version = version, artifact = name, repository = repository)
    private val atomic = lib("atomic")
    private val binomCoroutines = lib("binom-coroutines")
    private val charset = lib("charset")
    private val cleaner = lib("cleaner")
    private val collections = lib("collections")
    private val compression = lib("compression")
    private val concurrency = lib("concurrency")
    private val crc = lib("crc")
    private val console = lib("console")
    private val core = lib("core")
    private val date = lib("date")
    private val db = lib("db")
    private val dbSerialization = lib("db-serialization")
    private val dbSerializationAnnotations = lib("db-serialization-annotations")
    private val dbKmigrator = lib("kmigrator")
    private val dbPostgresqlAsync = lib("postgresql-async")
    private val dbRedis = lib("redis")
    private val dbSqlite = lib("sqlite")
    private val dbTarantoolClient = lib("tarantool-client")
    private val dns = lib("dns")
    private val env = lib("env")
    private val file = lib("file")
    private val fileWatch = lib("file-watch")
    private val flux = lib("flux")
    private val http = lib("http")
    private val httpClient = lib("httpClient")
    private val httpServer = lib("httpServer")
    private val io = lib("io")
    private val logger = lib("logger")
    private val metric = lib("metric")
    private val mqNats = lib("nats")
    private val network = lib("network")
    private val pipe = lib("pipe")
    private val pool = lib("pool")
    private val process = lib("process")
    private val s3 = lib("s3")
    private val scram = lib("scram")
    private val signal = lib("signal")
    private val smtp = lib("smtp")
    private val socket = lib("socket")
    private val ssl = lib("ssl")
    private val strong = lib("strong")
    private val thread = lib("thread")
    private val url = lib("url")
    private val uuid = lib("uuid")
    private val uuidSerialization = lib("uuid-serialization")
    private val webdav = lib("webdav")
    private val xml = lib("xml")
    private val xmlSerialization = lib("xml-serialization")
    private val xmlXsd = lib("xsd")

    private val libs = setOf(
        CompositLibrary(name = "Колекции", library = collections, dependencies = listOf(atomic)),
        CompositLibrary(name = "Сжатие", library = compression, dependencies = listOf(core, crc)),
        CompositLibrary(
            name = "Сеть",
            library = network,
            dependencies = listOf(core, env, concurrency, thread, collections, socket),
        ),
        CompositLibrary(
            name = "Клиент Postgres",
            library = dbPostgresqlAsync,
            dependencies = listOf(db, date, network, ssl, scram),
        ),
        CompositLibrary(name = "Клиент Redis", library = dbRedis, dependencies = listOf(core, db, date, network)),
        CompositLibrary(
            name = "Клиент Tarantool",
            library = dbTarantoolClient,
            dependencies = listOf(core, db, date, network, ssl),
        ),
        CompositLibrary(
            name = "SQLite",
            library = dbPostgresqlAsync,
            dependencies = listOf(core, db, file, concurrency, thread),
        ),
        CompositLibrary(name = "Работа с файлами", library = file, dependencies = listOf(core, charset)),
        CompositLibrary(
            name = "HTTP Сервер",
            library = httpServer,
            dependencies = listOf(core, network, ssl, http, compression, httpClient, binomCoroutines),
        ),
        CompositLibrary(
            name = "HTTP Клиент",
            library = httpServer,
            dependencies = listOf(core, network, ssl, compression, http),
        ),
        CompositLibrary(name = "Логирование", library = httpServer, dependencies = listOf(core, date)),
        CompositLibrary(
            name = "S3 клиент",
            library = s3,
            dependencies = listOf(core, httpServer, xml, date, collections, xmlSerialization),
        ),
    )
}
