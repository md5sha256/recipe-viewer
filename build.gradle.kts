plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

group = "io.github.md5sha256"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin"s jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.4")
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

tasks {
    compileJava {
        options.release.set(21)
    }
    processResources {
        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }
    }

}