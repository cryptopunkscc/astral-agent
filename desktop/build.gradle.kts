import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "cc.cryptopunks.astral"
version = "1.0-SNAPSHOT"


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "16"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)

                // https://github.com/dorkbox/SystemTray/issues/181
                // implementation("com.dorkbox:SystemTray:4.2.1")
                implementation("com.dorkbox:SystemTray:4.1")

                implementation("org.slf4j:slf4j-simple:2.0.6")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "Astral Agent"
        nativeDistributions {
            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Rpm,
                TargetFormat.Dmg,
                TargetFormat.Msi,
            )
            packageName = mainClass
            packageVersion = "1.0.0"

            appResourcesRootDir.set(project.layout.projectDirectory.dir("build/go/bin/"))
            linux {
                iconFile.set(project.file("src/jvmMain/resources/ic_astral_launcher.png"))
            }
        }
    }
}

val buildAstrald by tasks.registering {
    group = "build"
    doLast {
        exec {
            workingDir = projectDir.resolve("build/go/bin/linux-x64").apply { mkdirs() }
            commandLine = listOf("go", "build", "-o", "astrald", "github.com/cryptopunkscc/astrald/cmd/astrald")
        }
    }
}

tasks["jvmProcessResources"].dependsOn(buildAstrald)
