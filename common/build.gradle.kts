plugins {
    id("java")
    id("idea")
    id("fabric-loom") version "1.7.3"
}

repositories {
    maven("https://maven.parchmentmc.org/")
}

val MINECRAFT_VERSION: String by rootProject.extra
val PARCHMENT_VERSION: String? by rootProject.extra
val FABRIC_LOADER_VERSION: String by rootProject.extra
val FABRIC_API_VERSION: String by rootProject.extra

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = MINECRAFT_VERSION)

    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${MINECRAFT_VERSION}:${PARCHMENT_VERSION}@zip")
    })

    //modImplementation("net.fabricmc:fabric-loader:$FABRIC_LOADER_VERSION")
}

tasks {
    jar {
        from(rootDir.resolve("LICENSE.md"))
    }
}

tasks.configureEach {
    group = null
}