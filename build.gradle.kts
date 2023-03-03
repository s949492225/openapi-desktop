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
    implementation(platform("com.aallam.openai:openai-client-bom:2.1.3"))
    implementation("com.aallam.openai:openai-client")
    implementation("io.ktor:ktor-client-okhttp")
}

compose.desktop {

    compose.desktop {
        application {
            mainClass = "MainKt"
            nativeDistributions {
                targetFormats(Dmg, Msi, Deb)
                packageName = "ComposeTest"
                packageVersion = "1.0.0"
            }
        }
    }
}