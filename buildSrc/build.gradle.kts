plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

val kotlinVersion = kotlin.coreLibrariesVersion
//val kotlinxCoroutinesVersion = project.property("kotlinx_coroutines.version") as String
//val kotlinxSerializationVersion = project.property("kotlinx_serialization.version") as String
//val staticCssVersion = project.property("static_css.version") as String
//val binomAtomicVersion = project.property("binom_atomic.version") as String
//val composeVersion = project.property("compose.version") as String
//val binomUrlVersion = project.property("binom_url.version") as String
//val binomBitArrayVersion = project.property("binom_bitarray.version") as String
val binomVersion = project.property("binom.version") as String

buildConfig {
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_VERSION", "\"$kotlinVersion\"")
//    buildConfigField("String", "KOTLINX_COROUTINES_VERSION", "\"$kotlinxCoroutinesVersion\"")
//    buildConfigField("String", "KOTLINX_SERIALIZATION_VERSION", "\"$kotlinxSerializationVersion\"")
//    buildConfigField("String", "STATIC_CSS_VERSION", "\"$staticCssVersion\"")
//    buildConfigField("String", "COMPOSE_VERSION", "\"$composeVersion\"")
//    buildConfigField("String", "BINOM_URL_VERSION", "\"$binomUrlVersion\"")
//    buildConfigField("String", "ATOMIC_VERSION", "\"$binomAtomicVersion\"")
//    buildConfigField("String", "BITARRAY_VERSION", "\"$binomBitArrayVersion\"")
    buildConfigField("String", "BINOM_VERSION", "\"$binomVersion\"")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://repo.binom.pw")
    maven(url = "https://plugins.gradle.org/m2/")
    maven(url = "https://maven.google.com")
    gradlePluginPortal()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
//    api("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
//    api("org.jetbrains.compose:compose-gradle-plugin:$composeVersion")
//    api("pw.binom.static-css:plugin:$staticCssVersion")
    api("pw.binom:binom-publish:0.1.23")
//    api("com.bmuschko:gradle-docker-plugin:6.4.0")
//    api("org.hidetake:core:2.11.2")
//    api("org.hidetake:groovy-ssh:2.11.2")
//    api("com.jcraft:jsch:0.1.55")
//    api("com.jakewharton.cite:cite-gradle-plugin:0.2.0")
}
