dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://repo.spring.io/snapshot") }
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/snapshot") }
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
        mavenCentral()
    }
}

include("application", "domain")
rootProject.name = "yaba-server"
