pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BreakupChatbot"
include(":app")
include(":core:common")
include(":core:ui")
include(":core:domain")
include(":core:data")
include(":feature:chat")
include(":feature:settings")
include(":feature:onboarding")