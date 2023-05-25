package pw.binom

import org.gradle.api.internal.DocumentationRegistry
import org.gradle.api.internal.GradleInternal
import org.gradle.api.internal.artifacts.mvnsettings.MavenSettingsProvider
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.buildinit.plugins.internal.ProjectLayoutSetupRegistry
import org.gradle.buildinit.plugins.internal.services.ProjectLayoutSetupRegistryFactory
import org.gradle.configuration.project.BuiltInCommand
import org.gradle.internal.service.ServiceRegistration
import org.gradle.internal.service.scopes.AbstractPluginServiceRegistry
import org.gradle.workers.WorkerExecutor

open class BinomBuiltInCommand : BuiltInCommand {
    override fun asDefaultTask() = emptyList<String>()

    override fun commandLineMatches(taskNames: MutableList<String>?): Boolean {
        return taskNames!!.size > 0 && taskNames[0] == "binom-init"
    }

    init {
        println("BinomBuiltInCommand init")
    }
}

open class BinomInitServices : AbstractPluginServiceRegistry() {
    init {
        println("BinomInitServices init")
    }

    override fun registerGlobalServices(registration: ServiceRegistration) {
        registration.add(BinomBuiltInCommand::class.java)
    }

    override fun registerProjectServices(registration: ServiceRegistration) {
        registration.addProvider(ProjectScopeBuildInitServices())
    }

    private class ProjectScopeBuildInitServices {
        @Suppress("unused")
        fun createProjectLayoutSetupRegistry(
            mavenSettingsProvider: MavenSettingsProvider?,
            documentationRegistry: DocumentationRegistry?,
            fileCollectionFactory: FileCollectionFactory?,
            workerExecutor: WorkerExecutor?,
            gradle: GradleInternal,
        ): ProjectLayoutSetupRegistry {
            val fileResolver = gradle.rootProject.fileResolver
            return ProjectLayoutSetupRegistryFactory(
                mavenSettingsProvider,
                documentationRegistry,
                workerExecutor,
            ).createProjectLayoutSetupRegistry()
        }
    }
}
