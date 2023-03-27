plugins {
    id("com.android.library")
    kotlin("android")
}

group = "cc.cryptopunks.astral"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    flatDir {
        dirs("../build")
    }
}

dependencies {
    api(project(":client"))
    implementation(name = "astral", group = "", ext = "aar")
    implementation(project(":mobile:android:mod"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("androidx.core:core-ktx:1.8.0")
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
    lint {
        abortOnError = false
    }
    namespace = "cc.cryptopunks.astral.node"
}

val buildGo by tasks.registering {
    group = "build"
    val astral = file("../build/astral")
    if (astral.exists()) inputs.file(astral)
    outputs.file("../build/astral.aar")
    doLast {
        exec {
            commandLine("go", "build", "-o", "../build/astral", "./")
        }
    }
}

val buildGoMobile by tasks.registering {
    group = "build"
    dependsOn(buildGo)
    inputs.file("../build/astral")
    outputs.upToDateWhen {
        file("../build/astral.aar").exists()
    }
    doLast {
        exec {
            workingDir = file("../")
            commandLine("gomobile", "bind", "-v", "-o", "./build/astral.aar", "-target=android", "./node/")
        }
    }
}

tasks["preBuild"].dependsOn(buildGoMobile)
