import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
    alias(libs.plugins.vanniktechMavenPublish)
}

group = "dev.androidbroadcast.kmplibrary"
version = "1.0.0"

kotlin {
    jvm("desktop")
    androidTarget {
        publishLibraryVariants("release")
        withSourcesJar(publish = true)
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        tvosArm64(),
        tvosX64(),
        tvosSimulatorArm64(),
        watchosArm32(),
        watchosArm64(),
        watchosX64(),
        macosX64(),
        macosArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "KmpLibrary"
            isStatic = true
        }
    }

    linuxX64()
    linuxArm64()

    mingwX64()
}

android {
    namespace = "dev.androidbroadcast.kmplibrary"
    compileSdk = 34

    defaultConfig {
        minSdk = 28
    }

    buildTypes {
        debug {}
        release {}
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Раздел publishing переходит в настройку kotlin.androidTarget
}

// Добавляем информацию в публикации, которые создаст Kotlin Multiplatform Gradle плагин
publishing.publications
    .withType<MavenPublication>()
    .configureEach {
        groupId = project.group.toString()
        // artifactId = project.name // Не меняем имя артефакта
        version = project.version.toString()

        pom {
            name = "kmp-lib"
            description = "Sample KMP library publication"

            scm {
                connection = "scm:git:git://github.com/androidbroadcast/LibraryPublishSample.git"
                developerConnection = "scm:git:ssh://github.com/androidbroadcast/LibraryPublishSample.git"
                url = "https://github.com/androidbroadcast/LibraryPublishSample"
            }

            ciManagement {
                system = "GitHub Actions"
                url = "https://github.com/androidbroadcast/LibraryPublishSample/actions"
            }

            issueManagement {
                system = "GitHub"
                url = "https://github.com/androidbroadcast/LibraryPublishSample/issues"
            }

            developers {
                developer {
                    id = "kirich1409"
                    name = "Kirill Rozov"
                    email = "kirill@androidbroadcast.dev"
                }
            }
        }
    }

publishing {
    publications {
        // Список репозиториев куда публикуются артефакты
        repositories {
            // mavenCentral() // Публикация в Maven Central делается через REST API с помошью отдельного плагина
            mavenLocal() // Ищете файлы в директории ~/.m2/repository

            // Репозиторий в build папке корня проекта
            maven(url = uri(rootProject.layout.buildDirectory.file("maven-repo"))) {
                name = "BuildDir"
            }

            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/androidbroadcast/LibraryPublishSample")
                credentials {
                    username = System.getenv("GITHUB_USENAME")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

// Подробности как публиковать
// https://vanniktech.github.io/gradle-maven-publish-plugin/central
mavenPublishing {
    // Публикация в https://central.sonatype.com/
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

