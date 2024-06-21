package pw.binom.init

class MultiProject(
    val projects: List<Project>,
    val name: String,
    val group: String,
) {
    val allPlugins
        get() = projects.flatMap { it.allPlugins }.distinct()

    val pluginRepositories
        get() = allPlugins.mapNotNull { it.repository }.distinct()

    val versions
        get() = projects.flatMap { it.versions }.distinct()

    val dependenciesRepositories
        get() = projects.flatMap { it.libs }.map { it.repository }.distinct()
}
