plugins {
    kotlin("jvm")
    application
}

group = "com.krishnakandula"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Core"))
}

application {
    mainClass.set("com.krishnakandula.ironengine.sample.MainKt")
}
