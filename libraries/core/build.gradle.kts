dependencies{
    compileOnly("org.slf4j:slf4j-api:2.0.13")
    // get annotated methods
    implementation(libs.apacheCommonsLang3)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")
}