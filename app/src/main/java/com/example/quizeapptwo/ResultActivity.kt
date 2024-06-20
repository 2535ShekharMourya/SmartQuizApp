package com.example.quizeapptwo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.*
import com.blogspot.atifsoftwares.animatoolib.Animatoo.animateShrink
import java.util.Locale
import kotlin.properties.Delegates

class ResultActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    lateinit var tts: TextToSpeech
    var value: String? = null
    var score = 0
    private var ResultTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        tts = TextToSpeech(this, this)
        val con = findViewById<TextView>(R.id.congo)
        val finish = findViewById<Button>(R.id.Finish)
        score = intent.getIntExtra("SCORE", 0) // Default value is 0 if SCORE is not found
        val resultTextView: TextView = findViewById(R.id.Score)

        val value = intent.getStringExtra("name")
        con.text = "Congratulations ${value} !!"


        resultTextView.text = "Your score: $score"

        finish.setOnClickListener {
            speakOut("you can Restart Quiz")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            animateShrink(this)
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
                speakOut("Congratulations Shekhar Your Score is $score")

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