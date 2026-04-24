import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    java
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.vaadin") version "25.1.1"
}

group = "com.eduinsight"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.vaadin.com/vaadin-addons") }
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:25.1.1")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.vaadin:vaadin-spring-boot-starter")
    developmentOnly("com.vaadin:vaadin-dev")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
    }
}

vaadin {
    productionMode = false
}
