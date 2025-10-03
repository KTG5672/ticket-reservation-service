plugins {
    id("java")
    id("org.springframework.boot") version "3.3.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

subprojects {
    group = "io.github.ktg.ticketing"
    version = "0.0.1-SNAPSHOT"
    description = "티켓 예매 서비스"

    // Ensure every subproject has Java plugin (compileOnly/testCompileOnly configs exist)
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    // compileOnly extends from annotationProcessor for Lombok
    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {
        // Lombok (explicit version so non-Boot modules also resolve)
        compileOnly("org.projectlombok:lombok:1.18.34")
        annotationProcessor("org.projectlombok:lombok:1.18.34")
        testCompileOnly("org.projectlombok:lombok:1.18.34")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

        // JUnit Jupiter
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
        testImplementation("org.assertj:assertj-core:3.27.3")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

