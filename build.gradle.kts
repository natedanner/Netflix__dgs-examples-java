/*
 * Copyright 2021 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.*
import org.gradle.api.tasks.testing.logging.TestLogEvent.*


plugins {
    id("java")
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.netflix.dgs.codegen") version "6.1.4"
}
apply(plugin = "com.netflix.dgs.codegen")

group = "com.example"
version = "0.0.1-SNAPSHOT"


repositories {
    mavenCentral()
    // ----
    // Before we release the DGS Framework our CI Pipeline tests this project against the current snapshot.
    // To support that we need to have `mavenLocal` support.
    mavenLocal()
    // ----
}


dependencyManagement {
    imports {
        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:8.5.6")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenLocal()
}

// Set Kotlin version to 1.9.20 to avoid the issue described here:
// https://youtrack.jetbrains.com/issue/KT-58021
// TODO: after updating to Spring Boot 3.2.x, this workaround can be removed
extra["kotlin.version"] = "1.9.20"

dependencies {
    implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:8.5.6"))
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("name.nkonev.multipart-spring-graphql:multipart-spring-graphql:1.1.4")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("net.datafaker:datafaker:2.+")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-micrometer")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.netflix.graphql.dgs:graphql-dgs-client")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
    generateClientv2 = true
    packageName = "com.example.demo.generated"
}




tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(FAILED, STANDARD_ERROR, SKIPPED)
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
