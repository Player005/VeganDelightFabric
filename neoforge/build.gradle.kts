plugins {
    id("java-library")
    id("idea")
    id("net.neoforged.gradle.userdev") version "7.0.165"
}

val MOD_VERSION: String by rootProject.extra
val MOD_ID: String by rootProject.extra
val MOD_NAME: String by rootProject.extra

val MINECRAFT_VERSION: String by rootProject.extra
val MINECRAFT_VERSION_RANGE: String by rootProject.extra
val PARCHMENT_VERSION: String by rootProject.extra
val PARCHMENT_MC_VERSION: String by rootProject.extra

val NEOFORGE_VERSION: String by rootProject.extra
val NEOFORGE_VERSION_RANGE: String by rootProject.extra

val FD_NEO_VERSION: String by rootProject.extra

version = MOD_VERSION
group = "net.player005.vegandelightfabric"

repositories {
    mavenLocal()

    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven") {
                name = "Modrinth"
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

base {
    archivesName = MOD_ID
}

subsystems {
    parchment {
        minecraftVersion = PARCHMENT_MC_VERSION
        mappingsVersion = PARCHMENT_VERSION
    }
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


runs {
    configureEach {
        modSource(project.sourceSets.main.get())
    }
    create("client") {

    }

    create("data") {
        arguments.addAll("--mod", "vegandelight", "--all", "--output", file("src/generated/resources/").getAbsolutePath(), "--existing", file("src/main/resources/").getAbsolutePath())
    }
}

//configurations {
//    runtimeClasspath.extendsFrom(localRuntime)
//}

tasks.named("compileTestJava").configure {
    enabled = false
}

dependencies {
    implementation("net.neoforged:neoforge:${NEOFORGE_VERSION}")

    implementation(project.project(":common").sourceSets.getByName("main").output)

    implementation("maven.modrinth:farmers-delight:$FD_NEO_VERSION")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// NeoGradle compiles the game, but we don't want to add our common code to the game's code
val notNeoTask: (Task) -> Boolean = { it: Task -> !it.name.startsWith("neo") && !it.name.startsWith("compileService") }

tasks.withType<JavaCompile>().matching(notNeoTask).configureEach {
    source(project(":common").sourceSets.main.get().allSource)
}

tasks.withType<Javadoc>().matching(notNeoTask).configureEach {
    source(project(":common").sourceSets.main.get().allJava)
}

tasks.withType<ProcessResources>().matching(notNeoTask).configureEach {
    from(project(":common").sourceSets.main.get().resources)

    val replaceProperties = mapOf(
        "minecraft_version"      to MINECRAFT_VERSION,
        "minecraft_version_range" to MINECRAFT_VERSION_RANGE,
        "neo_version"            to NEOFORGE_VERSION,
        "neo_version_range"      to NEOFORGE_VERSION_RANGE,
        "loader_version_range"   to "*",
        "mod_id"                 to MOD_ID,
        "mod_name"               to MOD_NAME,
        "mod_license"            to "LGPL 3.0 or later",
        "mod_version"            to MOD_VERSION,
        "mod_authors"            to "Player005, SayWhatSayMon",
        "mod_description"        to ""
    )
    inputs.properties(replaceProperties)

    filesMatching("META-INF/mods.toml") {
        expand(replaceProperties)
    }
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)