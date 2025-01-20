plugins {
    java
    application
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

application.mainClass = "top.modpotato.AmputationPlugin"

group = "top.modpotato"
version = "1.7"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
    
    // Include the LICENSE file in the JAR
    from(rootDir) {
        include("LICENSE")
    }
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }
}