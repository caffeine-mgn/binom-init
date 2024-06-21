plugins {
//    kotlin("multiplatform") version "1.9.24" apply false
//    id("com.github.johnrengelman.shadow") version "5.2.0" apply false
}
allprojects {
    group = "pw.binom.init"
    version = System.getenv("GITHUB_REF_NAME")// ?: propertyOrNull("version")?.takeIf { it != "unspecified" }
            ?: "1.0.0-SNAPSHOT"

    repositories {
        mavenLocal()
        maven(url = "https://repo.binom.pw")
        mavenCentral()
    }
}