import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.4.21"
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "13"
        }
    }
}
