plugins {
    id("com.android.library")
    id("maven-publish")
}

android {
    namespace = "androidx.extensions"
    compileSdk = 37

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation("androidx.startup:startup-runtime:1.2.0")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "androidx.extensions"
            artifactId = "extensions"
            version = "1.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}