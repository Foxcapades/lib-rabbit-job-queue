import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-library`
  `maven-publish`
  kotlin("jvm") version "1.6.10"
  id("org.jetbrains.dokka") version "1.6.10"
}

group = "org.veupathdb.lib"
version = "1.0.1"

repositories {
  mavenCentral()
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

  // Jackson and modules (gotta catch em all)
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-json-org:2.13.1")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.13.1")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
  implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.13.1")

  // Logging
  implementation("org.apache.logging.log4j:log4j-core:2.17.0")
  implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.17.0")

  implementation("org.veupathdb.lib:hash-id:1.0.2")
  implementation("com.rabbitmq:amqp-client:5.14.0")
}

kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
  }
}

java {
  withJavadocJar()
  withSourcesJar()
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "17"
    freeCompilerArgs = listOf("-Xjvm-default=all")
  }
}

tasks.register<Copy>("getDeps") {
  from(sourceSets["main"].runtimeClasspath)
  into("runtime/")
}

tasks.withType<DokkaTask>().configureEach {


  dokkaSourceSets.configureEach {
    includeNonPublic.set(false)
    jdkVersion.set(17)
  }
}

publishing {
  repositories {
    maven {
      name = "GitHub"
      url = uri("https://maven.pkg.github.com/VEuPathDB/lib-rabbit-job-queue")
      credentials {
        username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
      }
    }
  }

  publications {
    create<MavenPublication>("gpr") {
      from(components["java"])

      pom {
        name.set("RabbitMQ Job Queue Library")
        description.set("Provides a worker and dispatcher for submitting jobs to a RabbitMQ queue.")
        url.set("https://github.com/VEuPathDB/lib-rabbit-job-queue")
        developers {
          developer {
            id.set("epharper")
            name.set("Elizabeth Paige Harper")
            email.set("epharper@upenn.edu")
            url.set("https://github.com/foxcapades")
            organization.set("VEuPathDB")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/VEuPathDB/lib-rabbit-job-queue.git")
          developerConnection.set("scm:git:ssh://github.com/VEuPathDB/lib-rabbit-job-queue.git")
          url.set("https://github.com/VEuPathDB/lib-rabbit-job-queue")
        }
      }
    }
  }
}
