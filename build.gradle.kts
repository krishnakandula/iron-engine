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
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "13"
        }
    }
}
