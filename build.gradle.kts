import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.compose") version "1.3.0"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(platform("com.aallam.openai:openai-client-bom:3.0.0"))
    implementation("com.aallam.openai:openai-client")
    implementation("io.ktor:ktor-client-okhttp")
    implementation("com.alialbaali.kamel:kamel-image:0.4.0")
    implementation("com.mikepenz:multiplatform-markdown-renderer-jvm:0.6.1")
}

compose.desktop {

    compose.desktop {
        application {
            mainClass = "MainKt"
            nativeDistributions {
                targetFormats(Dmg, Msi, Deb)
                macOS {
                    iconFile.set(project.file("launcher/icon.icns"))
                }
                packageName = "ChatGpt"
                packageVersion = "1.0.0"
            }
        }
    }
}