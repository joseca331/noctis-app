package com.example.noctis_app
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class PantallaAdaptacionActivity : AppCompatActivity() {

    private val TAG = "NOCTIS_CICLO"
    private lateinit var tvTemporizador: TextView
    private lateinit var btnIniciarPausar: Button
    private lateinit var etMinutos: EditText

    private var timer: CountDownTimer? = null
    private var tiempoRestanteMs: Long = 20 * 60 * 1000L
    private var enMarcha: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        // Enlazamos con tu archivo pantalla_adaptacion.xml
        setContentView(R.layout.pantalla_adaptacion)

        tvTemporizador = findViewById(R.id.tvTemporizador)
        btnIniciarPausar = findViewById(R.id.btnIniciarPausar)
        etMinutos = findViewById(R.id.etMinutos)
        val btnAplicar = findViewById<Button>(R.id.btnAplicar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        if (savedInstanceState != null) {
            tiempoRestanteMs = savedInstanceState.getLong("TIEMPO_RESTANTE", tiempoRestanteMs)
            enMarcha = savedInstanceState.getBoolean("EN_MARCHA", false)
        } else {
            val minutosRecibidos = intent.getIntExtra("DURACION_MIN", 20)
            tiempoRestanteMs = minutosRecibidos * 60 * 1000L
        }

        actualizarTexto()
        if (enMarcha) iniciarTemporizador()

        // Configura los minutos escritos en el EditText
        btnAplicar.setOnClickListener {
            val texto = etMinutos.text.toString()
            if (!enMarcha && texto.isNotEmpty()) {
                val mins = texto.toInt()
                if (mins > 0) {
                    tiempoRestanteMs = mins * 60 * 1000L
                    actualizarTexto()
                }
            }
        }

        btnIniciarPausar.setOnClickListener {
            if (enMarcha) pausarTemporizador() else iniciarTemporizador()
        }

        btnCancelar.setOnClickListener {
            timer?.cancel()
            val data = Intent().putExtra("COMPLETADA", false)
            setResult(Activity.RESULT_CANCELED, data)
            finish()
        }
    }

    private fun iniciarTemporizador() {
        timer = object : CountDownTimer(tiempoRestanteMs, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestanteMs = millisUntilFinished
                actualizarTexto()
            }

            override fun onFinish() {
                enMarcha = false
                val data = Intent().putExtra("COMPLETADA", true)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }.start()

        enMarcha = true
        btnIniciarPausar.text = getString(R.string.btn_pausar)
    }

    private fun pausarTemporizador() {
        timer?.cancel()
        enMarcha = false
        btnIniciarPausar.text = getString(R.string.btn_iniciar)
    }

    private fun actualizarTexto() {
        val minutos = (tiempoRestanteMs / 1000) / 60
        val segundos = (tiempoRestanteMs / 1000) % 60
        tvTemporizador.text = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
        outState.putLong("TIEMPO_RESTANTE", tiempoRestanteMs)
        outState.putBoolean("EN_MARCHA", enMarcha)
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        Log.d(TAG, "onDestroy")
    }
}