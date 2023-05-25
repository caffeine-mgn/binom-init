package pw.binom.init

abstract class AbstractProject : RootProject {
    protected val tab = "    "
    protected abstract val config: GlobalConfig
    protected fun baseSettingsPart(out: Appendable) {
        out.write {
            "pluginManagement" {
                "plugins" {
                }
                "repositories" {
                    if (config.useLocalRepository) {
                        +"mavenLocal()"
                    }
                    +"mavenCentral()"
                    +"maven(url = \"https://plugins.gradle.org/m2/\")"
                    +"gradlePluginPortal()"
                    if (config.useBinomRepository) {
                        +"maven(url = \"https://repo.binom.pw\")"
                    }
                }
            }
            +"""rootProject.name="${config.rootName}""""
        }
//        out.appendLine("pluginManagement {")
//            .appendLine("${tab}plugins {")
//            .appendLine("$tab}")
//            .appendLine("${tab}repositories {")
//        if (config.useLocalRepository) {
//            out.appendLine("${tab}${tab}mavenLocal()")
//        }
//        out.appendLine("${tab}${tab}mavenCentral()")
//            .appendLine("""${tab}${tab}maven(url = "https://plugins.gradle.org/m2/")""")
//            .appendLine("${tab}${tab}gradlePluginPortal()")
//        if (config.useBinomRepository) {
//            out.appendLine("""${tab}${tab}maven(url = "https://repo.binom.pw")""")
//        }
//        out.appendLine("$tab}")
//        out.appendLine("}")
//        out.appendLine("rootProject.name = \"${config.rootName}\"")
    }
}
