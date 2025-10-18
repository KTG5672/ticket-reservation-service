plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
tasks.named<Jar>("jar") {
    enabled = true
}
dependencies {
    implementation(project(":modules:common"))
    implementation(project(":modules:domain"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
}