import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    `maven-publish`
    signing
    `java-library`
}

group = "com.github.mckernant1.lol.blitzcrank"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")

    implementation(platform("software.amazon.awssdk:bom:2.17.204"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}



publishing {
    repositories {
        maven {
            url = uri("s3://mvn.mckernant1.com/release")
            authentication {
                register("awsIm", AwsImAuthentication::class.java)
            }
        }
    }

    publications {
        create<MavenPublication>("default") {
            artifactId = "lol-predictions-bot-models"
            from(components["kotlin"])
            val sourcesJar by tasks.creating(Jar::class) {
                val sourceSets: SourceSetContainer by project
                from(sourceSets["main"].allSource)
                archiveClassifier.set("sources")
            }
            artifact(sourcesJar)
            pom {
                name.set("lol-predictions-bot-models")
                description.set("Models to store lol predictions models")
                url.set("https://github.com/mckernant1/lol-predictions-bot-models")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("mckernant1")
                        name.set("Tom McKernan")
                        email.set("tmeaglei@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/mckernant1/lol-predictions-bot-models.git")
                    developerConnection.set("scm:git:ssh://github.com/mckernant1/lol-predictions-bot-models.git")
                    url.set("https://github.com/mckernant1/lol-predictions-bot-models")
                }
            }
        }
    }
}
