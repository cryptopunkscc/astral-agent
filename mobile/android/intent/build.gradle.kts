plugins {
    id("com.android.library")
    kotlin("android")
}

group = "cc.cryptopunks.astral"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":client"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
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
    namespace = "cc.cryptopunks.astral.intent"
}
