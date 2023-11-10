import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  java
  kotlin("jvm")
  id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "org.veupathdb.lib.test"
version = "1.1.0"

repositories {
  mavenCentral()
  gradlePluginPortal()
  maven {
    name = "GitHubPackages"
    url  = uri("https://maven.pkg.github.com/veupathdb/packages")
    credentials {
      username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
      password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
    }
  }
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("stdlib-jdk8"))
  implementation(rootProject)
  implementation("org.veupathdb.lib:hash-id:1.1.0")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
}

kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks.withType<ShadowJar> {
  archiveFileName.set("service.jar")
  from(project.configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
  manifest {
    attributes(
      "Main-Class" to "Main"
    )
  }
}
