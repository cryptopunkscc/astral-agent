pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        kotlin("android").version(extra["kotlin.version"] as String)
        id("com.android.application").version(extra["agp.version"] as String)
        id("com.android.library").version(extra["agp.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}

rootProject.name = "Astral Agent"

include(
    ":android",
//    ":desktop",
    ":common",
    ":client",

    ":mobile:android:mod",
    ":mobile:android:mod:notify",
    ":mobile:android:mod:content",
    ":mobile:android:intent",
//    ":mobile:android:node",
    ":mobile:android:wrapper",
    ":mobile:android:service",
    ":mobile:android:ui:theme",
    ":mobile:android:ui:main",
    ":mobile:android:ui:log",
    ":mobile:android:ui:permissions",
    ":mobile:android:ui:contacts",
    ":mobile:android:app",
    ":mobile:android:app:deps",
)
