import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm") version "1.8.10"
    id("org.jetbrains.dokka") version "1.7.20"
    `maven-publish`
}

repositories {
    mavenCentral()

    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    val spigotVersion: String by project
    val junitVersion: String by project

    api(kotlin("stdlib-jdk8"))
    api("org.spigotmc:spigot-api:$spigotVersion")

    implementation(kotlin("reflect"))

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}

kotlin {
    val jvmTarget: String by project
    jvmToolchain(jvmTarget.toInt())
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val dokkaHtml by tasks.getting(DokkaTask::class) {
    dokkaSourceSets {
        configureEach {
            externalDocumentationLink(
                "https://hub.spigotmc.org/javadocs/spigot/",
                "https://hub.spigotmc.org/javadocs/spigot/element-list"
            )
        }
    }
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

publishing {
    repositories {
        maven {
            name = "md5lukasReposilite"

            url = uri(
                "https://repo.md5lukas.de/${
                    if (version.toString().endsWith("-SNAPSHOT")) {
                        "snapshots"
                    } else {
                        "releases"
                    }
                }"
            )

            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            artifact(sourcesJar)
            artifact(javadocJar)
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}