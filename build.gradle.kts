plugins {
	java
	id("org.springframework.boot") version "2.2.2.RELEASE" apply false
	//id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	//id("io.spring.dependency-management") version "1.0.15.RELEASE"
}

group = "ru.kafka-example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_11
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.postgresql:postgresql:42.6.0")
	implementation("org.liquibase:liquibase-core:4.20.0")
	//implementation("org.apache.kafka:kafka-clients:2.4.1")
	compileOnly("org.projectlombok:lombok:1.18.22")
	annotationProcessor("org.projectlombok:lombok:1.18.22")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	//testImplementation("org.springframework.kafka:spring-kafka-test")

	testImplementation("org.testcontainers:kafka:1.16.3")
	testImplementation("org.testcontainers:junit-jupiter:1.16.3")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

dependencyManagement {
	imports {
		mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
	}
}
