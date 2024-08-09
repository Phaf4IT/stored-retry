plugins {
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
}

dependencies {
    implementation(project(":stored-retry:core"))
    compileOnly(project(":stored-retry:postgres"))
    compileOnly(project(":stored-retry:jobrunr"))
    // spring specific stuff
    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    compileOnly("io.projectreactor:reactor-core")
    // aspect aop
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework:spring-web")
    compileOnly("jakarta.annotation:jakarta.annotation-api")
    // get annotated methods
    implementation("org.apache.commons:commons-lang3:3.14.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.1")) //import bom
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")

    testImplementation("io.projectreactor:reactor-core")

    // TODO let's test with and without
    testImplementation(project(":stored-retry:postgres"))
//    testImplementation(project(":stored-retry:jobrunr"))
}


tasks.bootJar {
    enabled = false
}

