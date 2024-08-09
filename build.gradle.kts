plugins {
    `java-library`
}

group = "eu.phaf"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    tasks.named<Test>("test") {
        useJUnitPlatform()
    }

    tasks.jar {
        enabled = true
        // Remove `plain` postfix from jar file name
        archiveClassifier.set("")
    }
}

configurations {
    all {
        exclude(module = "spring-boot-starter-logging")
    }
}