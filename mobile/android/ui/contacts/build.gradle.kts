plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("android")
}

group = "cc.cryptopunks.astral"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":client"))
    api(project(":mobile:android:ui:theme"))

    api(compose.runtime)
    api(compose.foundation)
    implementation(compose.uiTooling)

    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    api("androidx.activity:activity-compose:1.7.0")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    api("com.google.accompanist:accompanist-swiperefresh:0.23.1")
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "cc.cryptopunks.astral.ui.contacts"
}
