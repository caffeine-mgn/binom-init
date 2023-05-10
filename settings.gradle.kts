pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://repo.binom.pw")
        gradlePluginPortal()
    }
}
rootProject.name = "BinomInit"
include("init")
