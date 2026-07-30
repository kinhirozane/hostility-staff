import java.util.*

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        val builder = maybeCreate("libs")
        val properties = Properties()
        layout.rootDirectory.file("gradle.properties").asFile.bufferedReader().use { reader ->
            properties.load(reader)
        }

        properties.filter { (key, _) -> key.toString().endsWith(".version") }
            .map { (key, value) ->
                key.toString().removeSuffix(".version").replace(".", "-") to value.toString()
            }
            .forEach { (alias, version) -> builder.version(alias, version) }
    }
}

if (JavaVersion.current() < JavaVersion.VERSION_25) throw IllegalStateException("Please use Java 25+!")

rootProject.name = "hostility_staff"
