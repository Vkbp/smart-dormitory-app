import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp) // Add this line
    alias(libs.plugins.kotlin.parcelize)
    id("com.google.dagger.hilt.android")  // Thêm dòng này
}
android {
    namespace = "com.ktx.dormitory"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ktx.dormitory"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "com.ktx.dormitory.HiltTestRunner"
        
        // Cấu hình BASE_URL linh hoạt: local.properties > gradle.properties > Default
        val props = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            props.load(localPropertiesFile.inputStream())
        }
        
        val baseUrlFromProps = (props.getProperty("BASE_URL") ?: project.findProperty("BASE_URL")?.toString())
            ?.trim()?.removeSurrounding("\"")

        // IP 192.168.1.11 dành cho điện thoại thật (theo Wi-Fi adapter).
        // IP 10.0.2.2 dành cho máy ảo Android Studio.
        val baseUrl: String = baseUrlFromProps ?: "https://172.21.5.6:8080/api/"
        
        // Đảm bảo URL luôn có dấu / ở cuối để Retrofit không crash
        val formattedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        buildConfigField("String", "BASE_URL", "\"$formattedBaseUrl\"")

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Retrofit & OkHttp (Để gọi API)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-compiler:2.52") // Đảm bảo dòng này dùng ksp, không phải kapt
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation("javax.inject:javax.inject:1")

    // WorkManager
    implementation(libs.androidx.work.runtime)

    // Biometric
    implementation("androidx.biometric:biometric:1.1.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    implementation("com.google.zxing:core:3.5.1") // Thư viện giải mã QR

    // ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    // ML Kit Face Detection (Mới cho AI Module)
    implementation("com.google.mlkit:face-detection:16.1.6")

    // TensorFlow Lite (Sửa lỗi UnsatisfiedLinkError trên một số thiết bị/giả lập)
    implementation("org.tensorflow:tensorflow-lite:2.17.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.5.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.17.0")

    // Lifecycle (for collectAsStateWithLifecycle)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Paging 3
    val paging_version = "3.3.2"
    implementation("androidx.paging:paging-runtime-ktx:$paging_version")
    implementation("androidx.paging:paging-compose:$paging_version")

    // Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    implementation(libs.sqlcipher)
    implementation(libs.sqlite.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.okhttp.mockwebserver)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.truth)
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.52")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.52")
    androidTestImplementation("io.mockk:mockk-android:1.13.10")
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation("androidx.navigation:navigation-testing:2.8.3")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}