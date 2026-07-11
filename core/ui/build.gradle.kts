plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.eallora.breakupchatbot.ui"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.0"
    }
}

dependencies {
    api(project(":core:common"))
    
    // Compose
    api("androidx.compose.ui:ui:1.6.8")
    api("androidx.compose.ui:ui-tooling-preview:1.6.8")
    api("androidx.compose.material3:material3:1.2.1")
    
    // Lifecycle for collectAsStateWithLifecycle
    api("androidx.lifecycle:lifecycle-runtime-compose:2.8.1")
    
    // Graphics
    api("androidx.compose.material:material-icons-extended:1.6.8")
}