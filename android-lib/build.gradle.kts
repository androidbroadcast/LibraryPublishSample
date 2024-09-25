import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    `maven-publish`
    alias(libs.plugins.vanniktechMavenPublish)
}

group = "dev.androidbroadcast.library"
version = "1.0.0"

android {
    namespace = "dev.androidbroadcast.library"
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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar() // Обязательно надо для удобства использования

            // Javadoc отдельно публикуется только если нету исходников
            // withJavadocJar()
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

publishing {
    publications {
        create<MavenPublication>("release") {

            // Добавляем компоненты в публикацию
            afterEvaluate {
                from(components["release"])
            }

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                name = "android-lib"
                description = "Sample Android library publication"

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
