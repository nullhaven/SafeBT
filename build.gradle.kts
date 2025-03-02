plugins {
    id("java")
}

group = "one.nullhaven"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

repositories {
    mavenCentral()
}

dependencies {
    // You'll have to provide that yourself, sorry.
    compileOnly(files("lib/Bukkit.jar"))
    testImplementation(files("lib/Bukkit.jar"))

    implementation("org.jetbrains:annotations:26.0.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.javadoc {
    isFailOnError = false
    options.memberLevel = JavadocMemberLevel.PUBLIC
    options.encoding = "UTF-8"
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

tasks.test {
    useJUnitPlatform()
}
