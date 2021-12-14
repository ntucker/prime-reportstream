plugins {
    kotlin("jvm") version "1.6.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.ajalt.clikt:clikt:3.3.0")
    implementation("com.helger.as2:as2-lib:4.8.0")

    implementation("org.apache.logging.log4j:log4j-api:[2.15.0,)")
    implementation("org.apache.logging.log4j:log4j-core:[2.15.0,)")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:[2.15.0,)")

    implementation("org.slf4j:slf4j-api:[1.7.29,)")
}
