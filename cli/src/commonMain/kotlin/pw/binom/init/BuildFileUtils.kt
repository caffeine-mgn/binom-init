package pw.binom.init

object BuildFileUtils {
    private fun plugins(
        output: Writer,
        plugins: List<Plugin>,
        apply: Boolean,
        withVersion: Boolean,
    ) {
        val classpathPlugins = plugins.filterIsInstance<Plugin.Classpath>()
        if (withVersion) {
            classpathPlugins.takeIf { it.isNotEmpty() }
                ?.let {
                    output {
                        "buildscript" {
                            it.mapNotNull { it.repository }.distinct()
                                .takeIf { it.isNotEmpty() }
                                ?.let {
                                    "repositories" {
                                        it.forEach {
                                            it.write(this)
                                        }
                                    }
                                }
                            "dependencies" {
                                it.forEach {
                                    it.writeClasspath(this)
                                }
                            }
                        }
                    }
                }
        }
        if (apply) {
            classpathPlugins.forEach {
                it.writeApply(output)
            }
        }
        plugins
            .filterIsInstance<Plugin.PluginSection>()
            .takeIf { it.isNotEmpty() }
            ?.let { pluginList ->
                output {
                    "plugins" {
                        pluginList.forEach { plugin ->
                            plugin.write(
                                output = this,
                                withVersion = withVersion,
                                apply = apply,
                            )
                        }
                    }
                }
            }
    }

    private fun buildRepositories(output: Writer, list: List<Repository>) {
        if (list.isNotEmpty()) {
            output {
                "repositories" {
                    list.forEach {
                        it.write(this)
                    }
                }
            }
        }
    }

    fun buildRoot(output: Writer, m: MultiProject) {
        plugins(
            output = output,
            plugins = m.allPlugins,
            apply = false,
            withVersion = true,
        )
        output {
            "allprojects" {
                +"group=\"${m.group}\""
                buildRepositories(this, m.dependenciesRepositories)
            }
        }
    }

    fun buildSettings(output: Writer, m: Project) {
        val pluginRepositories = m.pluginRepositories
        output {
            +"rootProject.name = \"${m.name}\""
        }
        if (pluginRepositories.isNotEmpty()) {
            output {
                "pluginManagement" {
                    buildRepositories(this, pluginRepositories)
                }
            }
        }
    }

    fun buildSettings(output: Writer, m: MultiProject) {
        val pluginRepositories = m.pluginRepositories
        output {
            +"rootProject.name = \"${m.name}\""
        }
        if (pluginRepositories.isNotEmpty()) {
            output {
                "pluginManagement" {
                    buildRepositories(this, pluginRepositories)
                }
            }
        }
        output {
            m.projects.forEach {
                +"include(\":${it.name}\")"
            }
        }
    }

    fun buildProperties(output: Writer, lib: List<Version>) {
        val versions = lib.distinct()
        if (versions.isNotEmpty()) {
            output {
                versions.forEach { version ->
                    +"${version.propertyName}=${version.version}"
                }
            }
        }
    }

    fun build(output: Writer, project: Project, isSubProject: Boolean) {
        plugins(
            output = output,
            plugins = project.allPlugins,
            apply = true,
            withVersion = !isSubProject,
        )
        val entryPoint = if (project.packageName.isEmpty()) {
            "main"
        } else {
            "${project.packageName}.main"
        }
        val mainClass = if (project.packageName.isEmpty()) {
            "MainKt"
        } else {
            "${project.packageName}.MainKt"
        }
        val versions = project.libs.filterIsInstance<Library.Define>().map { it.version }.distinct()
        output {
            versions.forEach { version ->
                output {
                    +"val ${version.variableName} = project.property(\"${version.propertyName}\")"
                }
            }
            if (project.kind == Kind.APPLICATION) {
                +"val nativeEntryPoint = \"$entryPoint\""
            }
            "kotlin" {
                when (project.kind) {
                    Kind.LIBRARY -> project.targets.forEach { target ->
                        +"${target.target}()"
                    }

                    Kind.APPLICATION -> {
                        project.targets.forEach { target ->
                            when (target) {
                                Target.JVM -> {
                                    "jvm" {
                                        "compilations.all" {
                                            "mainRun" {
                                                +"mainClass=\"$mainClass\""
                                            }
                                        }
                                    }
                                }

                                else -> target.target {
                                    "binaries" {
                                        "executable" {
                                            +"entryPoint = nativeEntryPoint"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (project.libs.isNotEmpty()) {
                    "sourceSets" {
                        "commonMain.dependencies" {
                            project.libs.forEach { lib ->
                                when (lib) {
                                    is Library.Kotlin -> lib.write(this)
                                    is Library.Define -> lib.write(this, versionInline = false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}