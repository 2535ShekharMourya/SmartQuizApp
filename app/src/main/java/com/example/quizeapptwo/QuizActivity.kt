package com.example.quizeapptwo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo.animateShrink
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.util.Locale


class QuizActivity : AppCompatActivity(),TextToSpeech.OnInitListener {
    lateinit var tts: TextToSpeech
    var value: String? = null
    var QuetionsCoun: Int = 1
    private lateinit var questions: List<Quetions>
    //var timeleft = Utility.TOTAL_EXAM_TIME
    private var currentQuestionIndex = 0
    private var marks: Int = 0
    lateinit var questionTextView: TextView
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var option1RadioButton: RadioButton
    private lateinit var option2RadioButton: RadioButton
    private lateinit var option3RadioButton: RadioButton
    private lateinit var option4RadioButton: RadioButton
    lateinit var progress_bar: ProgressBar
    lateinit var progress_text: TextView
    lateinit var submit: Button
    lateinit var next: Button
    lateinit var prev: Button
    private var timerTextView: TextView? = null
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 60000 // 1 minute (60,000 milliseconds)
    lateinit var circularProgress: CircularProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        tts = TextToSpeech(this, this)
        value = intent.getStringExtra("name")

        questionTextView = findViewById<TextView>(R.id.questionTextView)
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup)
        option1RadioButton = findViewById(R.id.option1RadioButton)
        option2RadioButton = findViewById(R.id.option2RadioButton)
        option3RadioButton = findViewById(R.id.option3RadioButton)
        option4RadioButton = findViewById(R.id.option4RadioButton)
        progress_bar = findViewById(R.id.progress_bar)
        progress_text = findViewById(R.id.progress_text)
        submit = findViewById(R.id.submitButton)
        next = findViewById<Button>(R.id.btn_next)
        prev = findViewById<Button>(R.id.btn_pre)
        timerTextView = findViewById(R.id.timer)
        val submit2=findViewById<Button>(R.id.submitBtn)
        submit2.setOnClickListener {
            showResults()
        }
        // circularProgress=findViewById<CircularProgressBar>(R.id.circularProgressBar)

        startTimer()


        val jsonData = """
             [
    {
      "question": "What is the capital of France?",
      "options": [
        "A) Madrid",
        "B) Berlin",
        "C) Paris",
        "D) Rome"
      ],
      "answer": "C) Paris"
    },
    {
      "question": "Which planet is known as the Red Planet?",
      "options": [
        "A) Earth",
        "B) Mars",
        "C) Jupiter",
        "D) Saturn"
      ],
      "answer": "B) Mars"
    },
    {
      "question": "What is the chemical symbol for water?",
      "options": [
        "A) H2O",
        "B) CO2",
        "C) O2",
        "D) NaCl"
      ],
      "answer": "A) H2O"
    },
    {
      "question": "Who wrote 'To Kill a Mockingbird'?",
      "options": [
        "A) Harper Lee",
        "B) Mark Twain",
        "C) F. Scott Fitzgerald",
        "D) Jane Austen"
      ],
      "answer": "A) Harper Lee"
    },
    {
      "question": "What is the largest mammal in the world?",
      "options": [
        "A) Elephant",
        "B) Blue Whale",
        "C) Giraffe",
        "D) Great White Shark"
      ],
      "answer": "B) Blue Whale"
    },
    {
      "question": "Which element has the atomic number 1?",
      "options": [
        "A) Helium",
        "B) Oxygen",
        "C) Hydrogen",
        "D) Carbon"
      ],
      "answer": "C) Hydrogen"
    },
    {
      "question": "What is the hardest natural substance on Earth?",
      "options": [
        "A) Gold",
        "B) Iron",
        "C) Diamond",
        "D) Silver"
      ],
      "answer": "C) Diamond"
    },
    {
      "question": "Who painted the Mona Lisa?",
      "options": [
        "A) Vincent van Gogh",
        "B) Pablo Picasso",
        "C) Leonardo da Vinci",
        "D) Michelangelo"
      ],
      "answer": "C) Leonardo da Vinci"
    },
    {
      "question": "What is the smallest unit of life?",
      "options": [
        "A) Atom",
        "B) Cell",
        "C) Molecule",
        "D) Organ"
      ],
      "answer": "B) Cell"
    },
    {
      "question": "What is the main ingredient in guacamole?",
      "options": [
        "A) Tomato",
        "B) Avocado",
        "C) Onion",
        "D) Lemon"
      ],
      "answer": "B) Avocado"
    }
  ]
        """
        val gson = Gson()
        val questionListType = object : TypeToken<List<Quetions>>() {}.type
        questions = gson.fromJson(jsonData, questionListType)

        // Load the last seen question index
        currentQuestionIndex = loadCurrentQuestionIndex(this)

        // Display the current question
        displayQuestion(currentQuestionIndex)
        next.setOnClickListener {
            goToNextQuestion()
        }
        prev.setOnClickListener {
            goToPreviousQuestion()
        }
        submit.setOnClickListener {
            speakOut("Checking Answer right or wrong")
            checkAnswer()
        }
        updateButtonStates()
    }

    private fun displayQuestion(index: Int) {
        val question = questions[index]
        // Update UI with the question and options
        questionTextView.text = question.question
        option1RadioButton.text = question.options[0]
        option2RadioButton.text = question.options[1]
        option3RadioButton.text = question.options[2]
        option4RadioButton.text = question.options[3]
        var temp = index
        progress_bar.progress = temp
        if (temp == 10) {
            progress_bar.progress = 1
        }
        progress_bar.max = questions!!.size - 1
        progress_text.text = "${temp + 1}" + "/" + "${questions!!.size}"
        optionsRadioGroup.clearCheck()
        resetOptionColors()
    }

    //Save the current question index
    fun saveCurrentQuestionIndex(context: Context, index: Int) {
        val sharedPreferences = context.getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("CurrentQuestionIndex", index)
        editor.apply()
    }

    //Load the current question index
    fun loadCurrentQuestionIndex(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("QuizPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("CurrentQuestionIndex", 0) // Default to 0 if not found
    }

    private fun goToNextQuestion() {
        QuetionsCoun++
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            displayQuestion(currentQuestionIndex)
            saveCurrentQuestionIndex(this, currentQuestionIndex)
            updateButtonStates()
        } else {
            // Quiz finished
            questionTextView.text = "Quiz finished!"
            // optionsRadioGroup.visibility = RadioGroup.GONE
            next.isEnabled = true
            prev.isEnabled = true


        }
    }

    private fun goToPreviousQuestion() {
        QuetionsCoun--
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
            displayQuestion(currentQuestionIndex)
            saveCurrentQuestionIndex(this, currentQuestionIndex)
            updateButtonStates()
        }
    }

    private fun updateButtonStates() {
        prev.isEnabled = currentQuestionIndex > 0
        next.isEnabled = currentQuestionIndex < questions.size - 1
    }

    override fun onPause() {
        super.onPause()
        // Save the current question index when the activity is paused
        saveCurrentQuestionIndex(this, currentQuestionIndex)
        countDownTimer?.cancel()
    }

    private fun checkAnswer() {
        val selectedOptionId = optionsRadioGroup.checkedRadioButtonId
        if (selectedOptionId != -1) {
            val selectedRadioButton = findViewById<RadioButton>(selectedOptionId)
            val selectedAnswer = selectedRadioButton.text.toString()
            val correctAnswer = questions[currentQuestionIndex].answer

            if (selectedAnswer == correctAnswer) {
                marks++
                selectedRadioButton.setBackgroundColor(Color.GREEN)
            } else {
                selectedRadioButton.setBackgroundColor(Color.RED)
            }
        }
    }

    private fun resetOptionColors() {
        option1RadioButton.setBackgroundColor(Color.TRANSPARENT)
        option2RadioButton.setBackgroundColor(Color.TRANSPARENT)
        option3RadioButton.setBackgroundColor(Color.TRANSPARENT)
        option4RadioButton.setBackgroundColor(Color.TRANSPARENT)

    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
                /*var min: Long =timeleft/60
                var sec: Long =timeleft%60
                timerTextView?.setText("min $min  sec $sec")
                timeleft--
                circularProgress.apply {
                   progress=(timeleft/Utility.TOTAL_EXAM_TIME*100).toFloat()
                }*/

                // timeLeftInMillis = millisUntilFinished
                //  updateTimer()
            }

            override fun onFinish() {
                // Timer finished, handle the end of the quiz
                speakOut("Time's up" )
                timerTextView?.text = "Time's up!"
                Handler().postDelayed({
                    showResults()
                }, 2000)

                // Implement your logic here (e.g., end the quiz, show results, etc.)
            }
        }.start()
    }

    private fun updateTimer() {
        val minutes = (timeLeftInMillis / 1000 / 60).toInt()
        val seconds = (timeLeftInMillis / 1000 % 60).toInt()

        val timeFormatted = String.format("%02d:%02d", minutes, seconds)

        timerTextView?.text = timeFormatted
    }

    override fun onDestroy() {
        // Shutdown TTS when activity is destroyed
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onDestroy()
        countDownTimer?.cancel()
    }

    override fun onResume() {
        super.onResume()
        startTimer()
    }

    private fun showResults() {
        val intent = Intent(this, ResultActivity::class.java)
        val score = marks // Replace this with the actual score calculation
        intent.putExtra("SCORE", score)
        intent.putExtra("name", value)

        startActivity(intent)
        finish() // Optional: Call this if you don't want to allow the user to go back to the quiz
        animateShrink(this)
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
            }
        } else {
            // Initialization failed
        }


    }

}