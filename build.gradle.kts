allprojects {
    group = "pw.binom.init"
    version = System.getenv("GITHUB_REF_NAME")// ?: propertyOrNull("version")?.takeIf { it != "unspecified" }
            ?: "1.0.0-SNAPSHOT"

    repositories {
        mavenLocal()
        maven(url = "https://repo.binom.pw")
//        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        mavenCentral()
    }
}