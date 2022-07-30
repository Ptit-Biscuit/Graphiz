import org.gradle.internal.os.OperatingSystem.*

plugins {
    kotlin("multiplatform") version "1.7.10"
    `maven-publish`
}

group = "com.github.ptitbiscuit"
version = "1.0.0"

val openrndrVersion = "0.3.58"
val openrndrOs = when (current()) {
    WINDOWS -> "windows"
    MAC_OS -> "macos"
    LINUX -> "linux-x64"
    else -> throw IllegalArgumentException("os not supported")
}

repositories {
    mavenCentral()

    dependencies {
        maven(url = "https://dl.bintray.com/openrndr/openrndr")
    }
}

fun openrndr(module: String): Any {
    return "org.openrndr:openrndr-$module:$openrndrVersion"
}

fun openrndrNatives(module: String): Any {
    return "org.openrndr:openrndr-$module-natives-$openrndrOs:$openrndrVersion"
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(openrndr("core"))
                implementation(openrndr("gl3"))
                implementation(openrndrNatives("gl3"))
                implementation(openrndr("extensions"))

                implementation("org.slf4j:slf4j-nop:1.7.25")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_18
    sourceCompatibility = JavaVersion.VERSION_18
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("graphiz") {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = "graphiz"
            version = "v${project.version}"
        }
    }
}
