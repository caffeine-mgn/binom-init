package pw.binom.init.libs

import pw.binom.init.*

object BinomLibraries {

    private val internalLibs = ArrayList<Library.Define>()
    val libs: List<Library.Define>
        get() = internalLibs
    private const val group = "pw.binom.io"
    private val binomPublicationVersion = Version("binomPublication", "0.1.23")
    private val version = Version(family = "BINOM", version = "1.0.0-SNAPSHOT")
    val binomPublicationPlugin = Plugin.IdPlugin(
        id = "pw.binom.publish",
        version = binomPublicationVersion,
    )

    private fun lib(
        name: String,
        plugins: List<Plugin> = emptyList(),
        deps: List<Library> = emptyList(),
        category: Category = Category.OTHER,
    ) =
        Library.Define(
            group = group,
            version = version,
            artifact = name,
            repository = Repository.BINOM_REPOSITORY,
            plugins = plugins,
            dependencies = deps,
            category = category,
        ).also {
            internalLibs += it
        }

    private val binomCoroutines = lib("coroutines")
    private val cleaner = lib("cleaner")
    private val collections = lib("collections")
    private val compression = lib("compression")
    private val concurrency = lib("concurrency")
    private val crc = lib("crc")
    private val console = lib("console")
    private val core = lib("core")
    private val charset = lib("charset")
    private val date = lib("date")
    private val db = lib("db", category = Category.DB)
    private val dbSerialization = lib("db-serialization")
    private val dbSerializationAnnotations = lib("db-serialization-annotations")
    private val dbKmigrator = lib("kmigrator", category = Category.DB)
    private val dbPostgresqlAsync = lib("postgresql-async", category = Category.DB)
    private val dbRedis = lib("redis", category = Category.DB)
    private val dbSqlite = lib("sqlite", category = Category.DB)
    private val dbTarantoolClient = lib("tarantool-client", category = Category.DB)
    private val dns = lib("dns")
    private val env = lib("env")
    private val file = lib("file")

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
    private val validate = lib("validate")
    private val process = lib("process")
    private val s3 = lib("s3")
    private val scram = lib("scram")
    private val signal = lib("signal")
    private val smtp = lib("smtp")
    private val socket = lib("socket")
    private val ssl = lib("ssl")
    private val strong = lib("strong")
    private val strongProperties = lib("strong-properties")
    private val strongPropertiesIni = lib("strong-properties-ini")
    private val strongPropertiesYaml = lib("strong-properties-yaml")
    private val strongWebServer = lib("strong-web-server")
    private val thread = lib("thread")
    private val url = lib("url")

    //    private val uuid = lib("uuid")
//    private val uuidSerialization = lib("uuid-serialization")
    private val webdav = lib("webdav")
    private val xml = lib("xml")
    private val xmlSerialization = lib("xml-serialization", category = Category.SERIALIZATION)
    private val xmlXsd = lib("xsd")
    /*
        val libs = setOf(
    //        CompositLibrary(name = "Консоль", library = console, dependencies = listOf(console)),
            CompositLibrary(name = "Коллекции", library = collections, dependencies = listOf()),
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
        */
}
