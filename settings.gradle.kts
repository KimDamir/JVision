rootProject.name = "JVision"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("lc.kra.system")
                includeGroupAndSubgroups("com.google.code.gson")
                includeGroupAndSubgroups("org.jetbrains")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("lc.kra.system")
                includeGroupAndSubgroups("com.google.code.gson")
                includeGroupAndSubgroups("org.jetbrains")
            }
        }
    }
}
include(":composeApp")
