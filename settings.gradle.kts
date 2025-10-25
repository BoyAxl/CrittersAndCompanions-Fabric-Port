pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

plugins {
    id("com.possible-triangle.helper") version ("1.1")
}

include("common", "fabric", "neoforge")
