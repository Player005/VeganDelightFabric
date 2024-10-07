@file:Suppress("PropertyName")

plugins {
    id("java")
    id("fabric-loom") version "1.7.3" apply false
}

val MINECRAFT_VERSION by extra { "1.20.4" }
val NEOFORGE_VERSION by extra { "20.4.237" }
val FABRIC_LOADER_VERSION by extra { "0.16.5" }
val FABRIC_API_VERSION by extra { "0.91.6+1.20.2" }

val PARCHMENT_VERSION by extra { "2024.04.14" }

val MOD_VERSION by extra { "1.1.1" }


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jar {
    enabled = false
}

subprojects {
    apply(plugin = "java")

    java.toolchain.languageVersion = JavaLanguageVersion.of(21)

    fun createVersionString(): String {
        val builder = StringBuilder()

        val isReleaseBuild = project.hasProperty("build.release")
        val buildId = System.getenv("GITHUB_RUN_NUMBER")

        if (isReleaseBuild) {
            builder.append(MOD_VERSION)
        } else {
            builder.append(MOD_VERSION.substringBefore('-'))
            builder.append("-snapshot")
        }

        builder.append("+mc").append(MINECRAFT_VERSION)

        if (!isReleaseBuild) {
            if (buildId != null) {
                builder.append("-build.${buildId}")
            } else {
                builder.append("-local")
            }
        }

        return builder.toString()
    }

    tasks.processResources {
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mapOf("mod_version" to createVersionString()))
        }
        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to createVersionString(),
                "loader_version" to FABRIC_LOADER_VERSION,
                "minecraft_version" to MINECRAFT_VERSION))
        }
    }

    version = createVersionString()
    group = "net.player005.noescape"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

}