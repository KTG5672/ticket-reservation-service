plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = rootProject.group

dependencies {
    implementation(project(":modules:common"))
    implementation(project(":modules:domain"))
    implementation(project(":modules:persistence"))

    // Web & Validation
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}


tasks.withType<Test> {
    useJUnitPlatform()
}