plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "club.tesseract.limbo"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("net.minestom:minestom-snapshots:9fbff439e7")
    implementation("dev.hollowcube:schem:1.2.0")

    // logging (logback)
    implementation("ch.qos.logback:logback-classic:1.5.8")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "$group.EntryPoint"
        }
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
    }

    test {
        useJUnitPlatform()
    }
}