// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.intellij.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask
import org.jetbrains.intellij.*
import org.jetbrains.intellij.IntelliJPluginConstants.PLUGIN_GROUP_NAME
import org.jetbrains.intellij.utils.LatestVersionResolver

@UntrackedTask(because = "Should always be run to initialize the plugin")
abstract class InitializeIntelliJPluginTask : DefaultTask() {

    @get:Internal
    abstract val offline: Property<Boolean>

    @get:Internal
    abstract val selfUpdateCheck: Property<Boolean>

    init {
        group = PLUGIN_GROUP_NAME
        description = "Initializes the Gradle IntelliJ Plugin"
    }

    private val context = logCategory()

    @TaskAction
    fun initialize() {
        checkPluginVersion()
    }

    private fun checkPluginVersion() {
        if (!selfUpdateCheck.get() || offline.get()) {
            return
        }

        try {
            val version = getCurrentPluginVersion()
                ?.let(Version::parse)
                .or { Version() }
            val latestVersion = LatestVersionResolver.fromGitHub(IntelliJPluginConstants.PLUGIN_NAME, IntelliJPluginConstants.GITHUB_REPOSITORY)
            if (version < Version.parse(latestVersion)) {
                warn(context, "${IntelliJPluginConstants.PLUGIN_NAME} is outdated: $version. Update `${IntelliJPluginConstants.PLUGIN_ID}` to: $latestVersion")
            }
        } catch (e: Exception) {
            error(context, e.message.orEmpty(), e)
        }
    }
}
