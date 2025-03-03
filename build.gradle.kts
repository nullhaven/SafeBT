plugins {
    id("java")
    id("maven-publish")
}

group = "one.nullhaven"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // You'll have to provide that yourself, sorry.
    implementation(files("lib/Bukkit.jar"))
    testImplementation(files("lib/Bukkit.jar"))

    implementation("org.jetbrains:annotations:26.0.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "repo"
            url = uri("https://maven.pkg.github.com/nullhaven/safebt")
            credentials {
                username = System.getenv("GHCR_USERNAME")
                password = System.getenv("GHCR_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("release") {
            from(components["java"])
        }
    }
}
