import com.diffplug.spotless.LineEnding

plugins {
    java
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.spotless)
    `maven-publish`
    idea
}

group = providers.gradleProperty("mod.group").get()
version = providers.gradleProperty("mod.version").get() + "+" + providers.gradleProperty("minecraft.version").get()
base.archivesName = "hostility-staff-fabric"
description = providers.gradleProperty("mod.description").get()

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

loom {
    splitEnvironmentSourceSets()
    accessWidenerPath = file("src/main/resources/hostility_staff.classtweaker")

    mods {
        create("hostility_staff") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
    include(implementation(fabricApi.module("fabric-api-base", libs.versions.fabric.api.get())) as Dependency)
    include(implementation(fabricApi.module("fabric-creative-tab-api-v1", libs.versions.fabric.api.get())) as Dependency)
    include(implementation(fabricApi.module("fabric-networking-api-v1", libs.versions.fabric.api.get())) as Dependency)
    include(implementation(fabricApi.module("fabric-rendering-v1", libs.versions.fabric.api.get())) as Dependency)
    include(implementation(fabricApi.module("fabric-resource-loader-v1", libs.versions.fabric.api.get())) as Dependency)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 25
    }

    processResources {
        val replacement = mapOf(
            "namespace" to providers.gradleProperty("mod.namespace").get(),
            "name" to providers.gradleProperty("mod.name").get(),
            "license" to providers.gradleProperty("mod.license").get(),
            "version" to providers.gradleProperty("mod.version").get(),
            "authors" to providers.gradleProperty("mod.authors").get(),
            "description" to providers.gradleProperty("mod.description").get(),
            "issue" to providers.gradleProperty("mod.issue").get(),
            "homepage" to providers.gradleProperty("mod.homepage").get(),
            "credits" to providers.gradleProperty("mod.credits").get(),
            "java" to providers.gradleProperty("java.version").get(),
            "minecraft" to providers.gradleProperty("minecraft.version").get(),
            "loader" to libs.versions.fabric.loader.get()
        )

        inputs.properties(replacement)
        filesMatching("fabric.mod.json") {
            expand(replacement)
        }
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${project.name}" }
        }
    }
}

spotless {
    java {
        target("src/**/*.java")
        lineEndings = LineEnding.UNIX
        licenseHeaderFile(file("spotless/license-header.txt"))
        encoding(Charsets.UTF_8)
        endWithNewline()
        removeUnusedImports()
    }
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies,
// so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
