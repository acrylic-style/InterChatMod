import net.blueberrymc.blueberryfarm.blueberry

plugins {
    id("net.blueberrymc.blueberryfarm") version("2.3.0") // https://github.com/BlueberryMC/BlueberryFarm
}

tasks.withType<JavaExec>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

blueberry {
    minecraftVersion.set("1.20.1")
    apiVersion.set("2.0.0-SNAPSHOT")
}

repositories {
    // mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.azisaba.net/repository/maven-public/") }
    maven { url = uri("https://libraries.minecraft.net/") }
    maven { url = uri("https://repo.blueberry.net/repository/maven-public/") }
}

dependencies {
    blueberry()
    implementation("org.java-websocket:Java-WebSocket:1.5.4")
}

tasks {
    shadowJar {
        relocate("org.java_websocket", "net.azisaba.interchatmod.lib.org.java_websocket")
    }
}
