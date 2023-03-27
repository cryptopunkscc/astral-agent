plugins {
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("android")
}

group = "cc.cryptopunks.astral"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    flatDir {
        dirs("../../build")
    }
}

dependencies {
    api(project(":mobile:android:wrapper"))
    api(project(":mobile:android:ui:theme"))

    api(compose.runtime)
    api(compose.foundation)
    api(compose.material)

    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "cc.cryptopunks.astral.ui.log"
}
