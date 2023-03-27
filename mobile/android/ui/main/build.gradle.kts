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
    api(project(":mobile:android:intent"))
    api(project(":mobile:android:ui:theme"))
    api(project(":mobile:android:ui:log"))

    implementation(compose.uiTooling)

    api("androidx.activity:activity-compose:1.7.0")

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
    namespace = "cc.cryptopunks.astral.ui.main"
}
