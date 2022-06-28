// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.intellij.tasks

import com.jetbrains.plugin.structure.base.utils.deleteQuietly
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Remove `classpath.index` files that are created by the `PathClassLoader`.
 * This loader, due to the implementation bug, ignores the `idea.classpath.index.enabled=false` flag and as a workaround,
 * files have to be removed manually.
 */
open class ClasspathIndexesCleanupTask @Inject constructor(
    objectFactory: ObjectFactory,
) : ConventionTask() {

    @InputFiles
    val classesDirs: ConfigurableFileCollection = objectFactory.fileCollection()

    @TaskAction
    fun classPathIndexCleanup() {
        classesDirs.forEach {
            it.toPath().resolve("classpath.index").deleteQuietly()
        }
    }
}