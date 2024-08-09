plugins {
    `java-library`
}

group = "io.github.phaf4it"
version = "0.0.1-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    project.version = rootProject.version
    project.group = rootProject.group

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }

    tasks.jar {
        enabled = true
        // Remove `plain` postfix from jar file name
        archiveClassifier.set("")
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>(project.name) {
                from(components["java"])
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/phaf4it/stored-retry")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
            // just here to validate :)
            maven {
                name = "MyLocalRepo"
                url = uri(layout.buildDirectory.dir("repo"))
            }
        }
    }
}

configurations {
    all {
        exclude(module = "spring-boot-starter-logging")
    }
}