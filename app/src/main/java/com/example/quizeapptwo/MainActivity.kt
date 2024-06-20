package com.example.quizeapptwo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.blogspot.atifsoftwares.animatoolib.Animatoo.animateShrink
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    lateinit var tts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tts = TextToSpeech(this, this)
        // Initialize TextToSpeech

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val button = findViewById<Button>(R.id.button)
        val input = findViewById<TextView>(R.id.input)

        button.setOnClickListener {
            if (input.text.toString().isEmpty()) {
                speakOut( "Please Enter Your Name First")
                Toast.makeText(this, "Enter Your Name First", Toast.LENGTH_SHORT).show()
            } else {
                speakOut( "Start Quiz")

                var intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("name", input.text.toString())
                startActivity(intent)
                finish()
                  animateShrink(this)
            }
        }

    }

    fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set the language
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle the error
            } else {
                // TTS is ready to use
                speakOut("Welcome to QuizApp")
            }
        } else {
            // Initialization failed
        }


    }
    override fun onDestroy() {
        // Shutdown TTS when activity is destroyed
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onDestroy()
    }
}
