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
    implementation(project(":mobile:android:service"))
    api(project(":mobile:android:mod:notify"))
    api(project(":mobile:android:mod:content"))
    api(project(":mobile:android:ui:main"))
    api(project(":mobile:android:ui:permissions"))
    api(project(":mobile:android:ui:contacts"))
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
    namespace = "cc.cryptopunks.astral.app.deps"
}
