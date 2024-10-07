plugins {
    id("java")
    id("idea")
    id("net.neoforged.moddev") version "1.0.20"
//    id("net.minecraftforge.gradle") version "[6.0,6.2)"
}

val MINECRAFT_VERSION: String by rootProject.extra
val PARCHMENT_VERSION: String? by rootProject.extra
val NEOFORGE_VERSION: String by rootProject.extra
val MOD_VERSION: String by rootProject.extra

base {
    archivesName = "vegan-delight-neoforge"
}

tasks.jar {
    val main = project.project(":common").sourceSets.getByName("main")
    from(main.output.classesDirs)
    from(main.output.resourcesDir)

    from(rootDir.resolve("LICENSE.md"))

    filesMatching("neoforge.mods.toml") {
        expand(mapOf("mod_version" to MOD_VERSION))
    }
}

tasks.jar.get().destinationDirectory = rootDir.resolve("build").resolve("libs")

neoForge {
    // Specify the version of NeoForge to use.
    version = NEOFORGE_VERSION

    parchment {
        minecraftVersion = MINECRAFT_VERSION
        mappingsVersion = PARCHMENT_VERSION
    }

    runs {
        create("client") {
            client()
        }
    }

    mods {
        create("no_escape") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":common").sourceSets.main.get())
        }
    }
}

tasks.named("compileTestJava").configure {
    enabled = false
}

dependencies {
    implementation(project.project(":common").sourceSets.main.get().output)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)