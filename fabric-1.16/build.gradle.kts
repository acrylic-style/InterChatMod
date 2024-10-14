plugins {
    id("fabric-loom") version "1.8-SNAPSHOT"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

val minecraftVersion = "1.16.5"
val yarnMappings = "1.16.5+build.10"
val loaderVersion = "0.14.24"
val fabricVersion = "0.42.0+1.16"
val archivesBaseName = "InterChatMod-${project.name}"
val adventureVersion by project.properties

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    maven { url = uri("https://maven.terraformersmc.com/releases/") }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    modImplementation("com.terraformersmc:modmenu:1.16.23")

    // Uncomment the following line to enable the deprecated Fabric API modules.
    // These are included in the Fabric API production distribution and allow you to update your mod to the latest modules at a later more convenient time.

    // modImplementation "net.fabricmc.fabric-api:fabric-api-deprecated:${project.fabric_version}"
    implementation(project(":common"))
    include(project(":common"))
    include("net.kyori:adventure-api:$adventureVersion")
    include("net.kyori:adventure-key:$adventureVersion")
    include("net.kyori:examination-api:1.3.0")
    include("net.kyori:examination-string:1.3.0")
    include("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    include("net.kyori:adventure-text-serializer-json:$adventureVersion")
    include("net.kyori:adventure-text-serializer-gson:$adventureVersion")
    include("org.java-websocket:Java-WebSocket:1.5.4")
    include("org.slf4j:slf4j-api:2.0.6")
    include("org.slf4j:slf4j-nop:2.0.6")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    remapJar {
        archiveFileName.set("$archivesBaseName-${project.version}.jar")
        from("LICENSE") {
            rename { "${it}_${archivesBaseName}"}
        }
    }

    remapSourcesJar {
        archiveFileName.set("$archivesBaseName-${project.version}-sources.jar")
        from("LICENSE") {
            rename { "${it}_${archivesBaseName}"}
        }
    }

    shadowJar {
        archiveBaseName.set("$archivesBaseName-DO-NOT-USE")
    }
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}
