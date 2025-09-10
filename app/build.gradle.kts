plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")

    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.safar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.safar"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation ("com.google.accompanist:accompanist-permissions:0.36.0")


    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.5")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.5")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // For StateFlow collection in Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    //Maplibre
    implementation("org.maplibre.gl:android-sdk:11.8.0")

    // Supabase
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.4.0")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.4.0")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.4.0")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.4.0")

// Location Services
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Kotlinx Serialization and Datetime
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")


    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    implementation("io.ktor:ktor-client-okhttp:2.3.11")

}