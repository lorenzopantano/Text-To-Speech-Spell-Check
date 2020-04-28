package it.lorenzopantano.texttospeech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TextToSpeechActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static TextToSpeech textToSpeech;
    String[] languages;
    private static final String TAG = "mTTS";
    private static final int CLANGACT = 101;
    private static final int CVOICEACT = 102;

    //Lingue
    ArrayList<Locale> languagesList = new ArrayList<Locale>();
    Set<Locale> languagesSet = new HashSet<>();

    //Voci
    List<Voice> voiceList = new ArrayList<>();
    Set<Voice> voiceSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tts_layout);
        languages = getResources().getStringArray(R.array.languages);
        Holder holder = new Holder();

        //Inizializza il tts, primo this è il context, il secondo l'onInitListener
        textToSpeech  = new TextToSpeech(this, this);

        //Imposta nel menu a scelta delle lingue, tutte quelle disponibili

    }

    /*Inizializza il tts:
     * status può essere:
     * TextToSpeech.SUCCESS oppure TextToSpeech.ERROR
     * https://developer.android.com/reference/android/speech/tts/TextToSpeech.OnInitListener
     */

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "onInit: INITIALIZATION SUCCESS");
            int res2 = textToSpeech.isLanguageAvailable(Locale.ITALY);
            Log.d(TAG, "onInit: ITALIAN AVAILABLE");

            int result = textToSpeech.setLanguage(Locale.ITALIAN);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "onInit: LANGUAGE NOT AVAILABLE");
            }
        } else {
            Log.e(TAG, "onInit: INITIALIZATION FAILED");
        }

        ttsTh.start();

        //Vedi classe sotto UtteranceProgList()
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgList());
    }

    /*
    * Le lingue e le voci disponibili vengono salvate in due array
    * diversi, in un thread diverso da quello prinicipale
    *  */
    Thread ttsTh = new Thread(new Runnable() {
        @Override
        public void run() {
            //Tutte le lingue disponibili
            languagesSet = textToSpeech.getAvailableLanguages();
            for (Locale lang : languagesSet) {
                languagesList.add(lang);
            }
            Log.d(TAG, "run: LANGUAGES OKAY");

            //Tutte le voci disponibili
            voiceSet = textToSpeech.getVoices();
            voiceList.addAll(voiceSet);
            Log.d(TAG, "run: VOICES OKAY");

            Log.d(TAG, "run: INTERRUPTING THREAD");
            ttsTh.interrupt();
            Log.d(TAG, "run: THREAD STOPPED");
        }
    });


    /*
    * Alla chiusura (o rotazione dell'app ???) distruggere l'engine del tts
    * il metodo stop() ferma se stava parlando,
    * il metodo shutdown() distrugge l'oggetto TextToSpeechEngine rendendolo inutilizzabile.
    * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /*
    * Serve a gestire quello che succede metre il tts parla
    * */
    class UtteranceProgList extends UtteranceProgressListener {

        @Override
        public void onStart(String utteranceId) {
            Log.d(TAG, "onStart: UTTERANCE PROGRESS LISTENER START: "+ utteranceId);
        }

        @Override
        public void onDone(String utteranceId) {
            Log.d(TAG, "onDone: UTTERANCE PROGRESS LISTENER DONE: "+ utteranceId);
        }

        @Override
        public void onError(String utteranceId) {
            Log.d(TAG, "onError: UTTERANCE PROGRESS LISTENER ERROR: "+ utteranceId);
        }
    }

    /*
    * Holder per UI
    * */

    class Holder implements View.OnClickListener{

        private EditText etInputText;
        private SeekBar seekBarSpeed, seekBarPitch;
        private ImageButton imgbtnPlay, imgbtnStop;
        private Button btnLanguage, btnVoice;

        Holder () {

            //Altre view
            etInputText = findViewById(R.id.etInputText);
            seekBarPitch = findViewById(R.id.seekBarPitch);
            seekBarSpeed = findViewById(R.id.seekBarSpeed);
            imgbtnPlay = findViewById(R.id.imgbtnPlay);
            imgbtnStop = findViewById(R.id.imgbtStop);
            btnLanguage = findViewById(R.id.btnLanguage);
            btnVoice = findViewById(R.id.btnVoice);

            btnLanguage.setOnClickListener(this);
            btnVoice.setOnClickListener(this);
            imgbtnPlay.setOnClickListener(this);
            imgbtnStop.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //Play button
            if (v.getId() == R.id.imgbtnPlay) {
                /*
                * Speed e pitch "normali" sono pari a 1.0
                * 2.0 significa il doppio e 0.5 la metà
                * 0.0 non esiste, dobbiamo fare in modo che non venga selezionato.
                * Dividiamo per 50 perché all'inizio il progress delle due seekBar
                * è impostato a 50 per cui il valore iniziale è 1.0
                * Se arriviamo al massimo cioè 100 abbiamo 2.0
                * */

                float speed = (float) seekBarSpeed.getProgress() / 50;
                float pitch = (float) seekBarPitch.getProgress() / 50;
                if (speed < 0.1) speed = 0.1f;
                if (pitch < 0.1) pitch = 0.1f;

                textToSpeech.setPitch(pitch);
                textToSpeech.setSpeechRate(speed);

                /*
                * Il metodo speak prende 4 parametri:
                * 1- Il testo da riprodurre
                * 2- Con che modalità riprodurlo: QUEUE_FLUSH significa che nella coda delle 'cose da riprodurre' viene cancellato tutto
                *    e viene riprodotto il testo corrente; QUEUE_ADD significa che il testo viene aggiunto alla coda
                * 3- Parametri aggiuntivi che specificano ad esempio il volume, può essere null
                * 4- utteranceId che è l'id univoco della richiesta, verrà passato all'UtteranceProgressListener
                * */

                /*
                * Possiamo ad esempio impostare il volume al minimo creando un Bundle per i parametri,
                * e metterci dentro la costante relativa al volume (ce ne sono diverse di costanti per il TTS)
                * KEY_PARAM_VOLUME che va da 0.0 a 1.0 dove 0.0 è silenzio e 1.0 è il massimo (default)
                * Bundle params = new Bundle();
                * params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 0.1f);
                * E poi passare params come terzo parametro al metodo speak()
                * */


                textToSpeech.speak(etInputText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, "textToSpeechUtteranceID");
            }

            //Stop button
            else if (v.getId() == R.id.imgbtStop) {
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
            }

            //Change Language button
            else if (v.getId() == R.id.btnLanguage) {
                if (textToSpeech.isSpeaking()) textToSpeech.stop();
                Intent langIntent = new Intent(TextToSpeechActivity.this, ChangeLanguageActivity.class);
                langIntent.putExtra("avLanguages", languagesList);
                startActivityForResult(langIntent, CLANGACT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ON ACTIVITY RESULT "+ languagesList.get(data.getIntExtra("selectedLang", -1)));
        if (requestCode == CLANGACT) {
            //TODO: cambia la lingua
            textToSpeech.setLanguage(languagesList.get(data.getIntExtra("selectedLang", -1)));
            Toast.makeText(this, "LANGUAGE SET TO : "+ languagesList.get(data.getIntExtra("selectedLang", -1)).getDisplayLanguage(), Toast.LENGTH_SHORT).show();
        }
    }
}
