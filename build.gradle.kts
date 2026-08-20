plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.0.1"
}

group = "io.github.md5sha256"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven("https://repo.nexomc.com/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.+")
    implementation("org.incendo:cloud-paper:2.0.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.11.5")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("com.nexomc:nexo:1.10.0")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(25))

tasks {
    compileJava {
        options.release.set(25)
    }
    processResources {
        val version = project.version
        filesMatching("paper-plugin.yml") {
            expand("version" to version)
        }
    }
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin"s jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.1.2")
    }
    shadowJar {
        val base = "io.github.md5sha256.recipeviewer.libraries"
        relocate("org.incendo", "${base}.org.incendo")
        relocate("com.github.stefvanschie", "${base}.com.github.stefvanschie")
        relocate("org.spongepowered.configurate", "${base}.org.spongepowered.configurate")
        relocate("org.yaml.snakeyaml", "${base}.org.yaml.snakeyaml")
        relocate("io.leangen.geantyref", "${base}.io.leangen.geantyref")
    }
}