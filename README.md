# 📸 SKImageSlider

A lightweight and customizable **Image Slider** library for Android written in Kotlin.  
Supports images from **drawable, Uri, file path, URL, or Drawable object** with optional **title, description, auto-scroll, swipe enable/disable, and error fallback image**.  

---

## 🚀 Features
- ✅ Supports multiple image sources: `drawable`, `Uri`, `file path`, `URL`
- ✅ Optional **title** and **description** per slide
- ✅ Auto-scroll with configurable time interval
- ✅ Swipe enable/disable
- ✅ Error placeholder image if loading fails
- ✅ Lightweight (no 3rd-party libraries)

---

## 📦 Installation

Add [JitPack](https://jitpack.io/) to your **root `settings.gradle`**:

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
}


Then add the dependency in your app-level build.gradle:

dependencies {
    implementation 'com.github.shricky:ImageSlider:1.0.1'
}


🛠 Usage
1. Add to XML
<com.sk.skimageslider.ImageSliderView
    android:id="@+id/imageSlider"
    android:layout_width="match_parent"
    android:layout_height="200dp"/>

2. Setup in Activity/Fragment
val slider = findViewById<ImageSliderView>(R.id.imageSlider)

val items = listOf(
    SliderItem(R.drawable.sample1, "Title 1", "Description 1"),
    SliderItem("file:///android_asset/sample2.png", "Title 2", "Description 2"),
    SliderItem("https://example.com/sample3.jpg", "Title 3", "Description 3")
)

// Set items with 3-second auto scroll
slider.setItems(items, autoScrollInterval = 3000)

// Enable/disable swipe
slider.enableSwipe(true)

3. Error Placeholder (Optional)

If an image fails to load, you can show a default placeholder by updating your adapter with:

holder.binding.imageView.setImageResource(R.drawable.placeholder)

🙌 Contribute

Pull requests are welcome!

📄 License
MIT License

Copyright (c) 2025 Shishir Kumar
