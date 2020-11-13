package com.example.speechtotext

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.speechtotext.databinding.ActivitySubBinding

class SubActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubBinding
    private var speechRecognizer : SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        speechRecognizer?.setRecognitionListener(createRecognitionListenerStringStream { binding.textView.text = it })
        binding.voiceBtn.setOnClickListener {
            speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
        }
        binding.stopBtn.setOnClickListener { speechRecognizer?.stopListening() }
        binding.button.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
    }

    private fun createRecognitionListenerStringStream(onResult : (String)-> Unit) : RecognitionListener {
        return object : RecognitionListener {
            // The sound level in the audio stream has changed.
            override fun onRmsChanged(rmsdB: Float) {}

            // Called when the endpointer is ready for the user to start speaking.
            override fun onReadyForSpeech(params: Bundle) {
                onResult("onReadyForSpeech")
            }

            // More sound has been received.
            override fun onBufferReceived(buffer: ByteArray) {
                onResult("onBufferReceived")
            }

            // Called when partial recognition results are available.
            override fun onPartialResults(partialResults: Bundle) {
                onResult("onPartialResults")
            }

            // Reserved for adding future events.
            override fun onEvent(eventType: Int, params: Bundle) {
                onResult("onEvent")
            }

            // The user has started to speak.
            override fun onBeginningOfSpeech() {
                onResult("onBeginningOfSpeech")
            }

            // Called after the user stops speaking.
            override fun onEndOfSpeech() {
                onResult("onEndOfSpeech")
            }

            // A network or recognition error occurred.
            override fun onError(error: Int) {
                onResult("onError")
            }

            // Called when recognition results are ready.
            override fun onResults(results: Bundle) {
                val stringArray = results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
                onResult("onResults " + stringArray.toString())
            }
        }
    }

    companion object {
        private const val PERMISSIONS_RECORD_AUDIO = 1000
    }
}