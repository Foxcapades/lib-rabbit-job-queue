import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-library`
  kotlin("jvm") version "1.6.10"
}

group = "org.veupathdb.lib"
version = "1.0.0"

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