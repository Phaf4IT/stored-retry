dependencies{
    implementation(project(":stored-retry-core"))
    compileOnly("org.slf4j:slf4j-api:2.0.13")
    // database
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    // database migration
    implementation("org.flywaydb:flyway-core:10.17.0")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.17.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")

    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.1")) //import bom
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
    constraints {
        testImplementation(libs.apacheCommonsCompress){
            because("Apache Commons Compress: Denial of service caused by an infinite loop for a corrupted DUMP file")
        }
    }
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.0.0")
    constraints{
        testImplementation(libs.jsonPath){
            because("json-path Out-of-bounds Write vulnerability")
        }
    }
}
