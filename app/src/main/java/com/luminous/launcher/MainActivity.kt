package com.luminous.launcher

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView
import android.content.Intent
import org.json.JSONObject

class MainActivity : Activity() {

    private val background = Color.rgb(12, 14, 30)
    private val purple = Color.rgb(145, 90, 255)
    private val white = Color.WHITE

    private lateinit var profileText: TextView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER_HORIZONTAL
        root.setPadding(32, 45, 32, 32)
        root.setBackgroundColor(background)

        val scroll = ScrollView(this)
        scroll.addView(root)

        val title = TextView(this)
        title.text = "✦ LUMINOUS"
        title.textSize = 32f
        title.gravity = Gravity.CENTER
        title.setTextColor(white)

        root.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "MINECRAFT JAVA LAUNCHER"
        subtitle.textSize = 12f
        subtitle.gravity = Gravity.CENTER
        subtitle.setTextColor(purple)

        root.addView(subtitle)

        profileText = TextView(this)
        profileText.text = "No profile imported"
        profileText.textSize = 16f
        profileText.setTextColor(white)
        profileText.setPadding(0, 40, 0, 30)

        root.addView(profileText)

        val importButton = Button(this)
        importButton.text = "IMPORT CONFIGURATION"

        importButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "application/json"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, 100)
        }

        root.addView(importButton)

        val launchButton = Button(this)
        launchButton.text = "LAUNCH MINECRAFT"

        launchButton.setOnClickListener {
            statusText.text =
                "Minecraft runtime is not integrated yet. " +
                "A compatible runtime is required to launch the game."
        }

        root.addView(launchButton)

        statusText = TextView(this)
        statusText.text = "Welcome to LUMINOUS!"
        statusText.textSize = 14f
        statusText.setTextColor(white)
        statusText.setPadding(0, 30, 0, 0)

        root.addView(statusText)

        setContentView(scroll)
    }

    @Deprecated("Uses activity result API")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data ?: return

            try {
                val json = contentResolver.openInputStream(uri)
                    ?.bufferedReader()
                    ?.use { it.readText() } ?: return

                val config = JSONObject(json)

                val name = config.optString("name", "Minecraft")
                val versionId = config.optString("versionId", "Unknown")

                val loader = when {
                    versionId.contains("forge", true) -> "Forge"
                    versionId.contains("fabric", true) -> "Fabric"
                    else -> "Unknown / Vanilla"
                }

                profileText.text =
                    "Profile: $name\n" +
                    "Version ID: $versionId\n" +
                    "Mod Loader: $loader"

                statusText.text =
                    "Configuration imported successfully!"

            } catch (e: Exception) {
                statusText.text =
                    "Unable to read configuration: ${e.message}"
            }
        }
    }
}
