rootProject.name = "stored-retry"
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}
include(":stored-retry-core")
project(":stored-retry-core").projectDir = file("libraries/core")
include(":stored-retry-jobrunr")
project(":stored-retry-jobrunr").projectDir = file("libraries/jobrunr")
include(":stored-retry-postgres")
project(":stored-retry-postgres").projectDir = file("libraries/postgres")
include(":stored-retry-spring-boot-autoconfigure")
project(":stored-retry-spring-boot-autoconfigure").projectDir = file("libraries/spring-boot-autoconfigure")
