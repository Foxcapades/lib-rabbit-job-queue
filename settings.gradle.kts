rootProject.name = "rabbit-job-queue"

include("test:client")
findProject(":test:client")?.name = "client"

include("test:server")
findProject(":test:server")?.name = "server"

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}