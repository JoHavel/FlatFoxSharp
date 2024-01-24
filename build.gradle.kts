plugins {
    kotlin("multiplatform") version "1.9.22"
}

group = "cz.moznabude"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.687")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.687")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.11.1-pre.688")
//                implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:5.3.1-pre.240-kotlin-1.5.30")
            }
        }
    }
}