import org.jetbrains.gradle.ext.packagePrefix
import org.jetbrains.gradle.ext.settings
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.collections.minBy

plugins {
    kotlin("jvm") version "2.3.21"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.4.1"
    id("io.kotest").version("6.1.11")
}

group = "dev.timray.kotomata"
version = "1.0-SNAPSHOT"

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        jvmToolchain(25)
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}


repositories {
    mavenCentral()
}

idea {
    module {
        settings {
            packagePrefix["src/main/kotlin"] = "dev.timray.kotomata"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core:6.1.11")
    testImplementation("io.kotest:kotest-runner-junit5:6.1.11")

}

tasks.test {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-XXLanguage:+ContextParameters"))
}