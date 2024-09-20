import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `maven-publish`
  kotlin("jvm") version "2.0.20"
  id("org.jetbrains.dokka") version "1.9.20"
}

group = "org.veupathdb.lib"
version = "2.0.1"

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

  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("com.rabbitmq:amqp-client:5.20.0")

  api("org.veupathdb.lib:jackson-singleton:3.2.0")
  api("org.veupathdb.lib:hash-id:1.1.0")
}

kotlin {
  jvmToolchain {
    languageVersion = JavaLanguageVersion.of(21)
    vendor = JvmVendorSpec.AMAZON
  }
}

java {
  withJavadocJar()
  withSourcesJar()
}

tasks.register<Copy>("getDeps") {
  from(sourceSets["main"].runtimeClasspath)
  into("runtime/")
}

tasks.withType<DokkaTask>().configureEach {
  dokkaSourceSets.configureEach {
    includeNonPublic.set(false)
    jdkVersion.set(21)
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
