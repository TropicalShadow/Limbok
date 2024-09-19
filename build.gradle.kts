plugins {
    id("java")
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

tasks.test {
    useJUnitPlatform()
}