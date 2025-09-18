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

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}


tasks.withType<Test> {
    useJUnitPlatform()
}