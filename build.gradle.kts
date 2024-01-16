plugins {
    kotlin("jvm") version "1.9.21"
    id("java")
    id("application")
}

group = "com.xaverianteamrobotics"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains:markdown:0.5.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
    applicationDistribution.from("./") {
        include("doc-style.css")
        include("unzipAPK.sh")
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}