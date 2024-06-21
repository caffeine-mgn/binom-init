package pw.binom.init.libs

import pw.binom.init.*

object BinomLibraries {


    private const val group = "pw.binom.io"
    private val version = Version(family = "BINOM", version = "1.0.0-SNAPSHOT")
    private fun lib(name: String, plugins: List<Plugin> = emptyList(), deps: List<Library> = emptyList()) =
        Library.Define(
            group = group,
            version = version,
            artifact = name,
            repository = Repository.BINOM_REPOSITORY,
            plugins = plugins,
            dependencies = deps,
        )

    private val binomCoroutines = lib("coroutines")

    private val cleaner = lib("cleaner")
    private val collections = lib("collections")
    private val compression = lib("compression")
    private val concurrency = lib("concurrency")
    private val crc = lib("crc")
    private val console = lib("console")
    private val core = lib("core")
    private val charset = lib("charset", deps = listOf(core, collections))
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

    //    private val fileWatch = lib("file-watch")
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

    val libs = setOf(
        CompositLibrary(name = "Консоль", library = console, dependencies = listOf(console)),
        CompositLibrary(name = "Колекции", library = collections, dependencies = listOf()),
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
            library = dbSqlite,
            dependencies = listOf(core, db, file, concurrency, thread),
        ),
        CompositLibrary(
            name = "Работа с файлами",
            library = file,
            dependencies = listOf(core, charset),
        ),
        CompositLibrary(
            name = "HTTP Сервер",
            library = httpServer,
            dependencies = listOf(core, network, ssl, http, compression, httpClient, binomCoroutines),
        ),
        CompositLibrary(
            name = "HTTP Клиент",
            library = httpClient,
            dependencies = listOf(core, network, ssl, compression, http),
        ),
        CompositLibrary(
            name = "Логирование",
            library = logger,
            dependencies = listOf(core, date),
        ),
        CompositLibrary(
            name = "S3 клиент",
            library = s3,
            dependencies = listOf(core, httpClient, xml, date, collections, xmlSerialization),
        ),
    )
}
