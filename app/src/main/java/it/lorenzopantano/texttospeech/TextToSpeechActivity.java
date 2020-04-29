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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TextToSpeechActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static TextToSpeech textToSpeech;
    private static final String TAG = "mTTS";
    private static final int CLANGACT = 101;
    private static final int CVOICEACT = 102;
    private static final Locale DEFAULT_LANG = Locale.ITALIAN;
    private EditText etInputText;
    private TextView tvLang, tvVoice;
    private String editTextInput;
    private Locale locale;

    //Lingue
    ArrayList<Locale> languagesList = new ArrayList<Locale>();
    Set<Locale> languagesSet = new HashSet<>();

    //Voci
    ArrayList<Voice> voiceList = new ArrayList<>();
    Set<Voice> voiceSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tts_layout);
        etInputText = findViewById(R.id.etInputText);
        tvLang = findViewById(R.id.tvLang);
        tvVoice = findViewById(R.id.tvVoice);

        new Holder();

        if (savedInstanceState != null) {
            etInputText.setText(savedInstanceState.get("editText").toString());
            locale = Locale.forLanguageTag(savedInstanceState.getString("lang"));
            tvLang.setText("Language: " +locale.getDisplayLanguage().toUpperCase());
        }

        //Inizializza il tts, primo this è il context, il secondo l'onInitListener
        textToSpeech = new TextToSpeech(getApplicationContext(), this);
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
            int result;
            if (locale == null) {
                result = textToSpeech.setLanguage(DEFAULT_LANG); //Default Language is Italian
                tvLang.setText("Language: "+DEFAULT_LANG.getDisplayLanguage());
                tvVoice.setText("Voice: "+textToSpeech.getVoice().getName());
            }
            else {
                result = textToSpeech.setLanguage(locale);
                tvLang.setText("Language: "+locale.getDisplayLanguage());
                tvVoice.setText("Voice: "+textToSpeech.getVoice().getName());
            }
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
            languagesList.addAll(languagesSet);
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

    //Salva la stringa nella EditText
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("editText", etInputText.getText().toString());
        outState.putString("lang", textToSpeech.getVoice().getLocale().toLanguageTag());
    }

    @Override
    protected void onPause() {
        super.onPause();
        editTextInput = etInputText.getText().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (editTextInput != null) etInputText.setText(editTextInput);
    }

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

        private SeekBar seekBarSpeed, seekBarPitch;
        private ImageButton imgbtnPlay, imgbtnStop;
        private Button btnLanguage, btnVoice;


        Holder () {

            //Altre view
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

            ///Change Voice button
            else if (v.getId() == R.id.btnVoice) {
                if (textToSpeech.isSpeaking()) textToSpeech.stop();
                Log.d(TAG, "onClick: CHANGE VOICE BUTTON PREPARING INTENT");
                Intent voiceIntent = new Intent(TextToSpeechActivity.this, ChangeVoiceActivity.class);
                voiceIntent.putExtra("avVoices", voiceList);
                Log.d(TAG, "onClick: CHANGE VOICE BUTTON PUT IN EXTRA: "+ voiceList);
                startActivityForResult(voiceIntent, CVOICEACT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CLANGACT) {


            //TODO: cambia la lingua
            int result = data.getIntExtra("selectedLang", -1);
            if (result == -1) {
                textToSpeech.setLanguage(DEFAULT_LANG);
                tvLang.setText("Language: "+DEFAULT_LANG.getDisplayLanguage());
                tvVoice.setText("Voice: "+textToSpeech.getVoice().getName());
            } else {
                Locale selectedLoc = languagesList.get(data.getIntExtra("selectedLang", -1));
                textToSpeech.setLanguage(selectedLoc);
                tvLang.setText("Language: "+selectedLoc.getDisplayLanguage());
                tvVoice.setText("Voice: "+textToSpeech.getVoice().getName());
                Toast.makeText(this, "LANGUAGE: "+selectedLoc.getDisplayLanguage().toUpperCase(), Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == CVOICEACT) {
            int result = data.getIntExtra("selectedVoice", -1);
            if (result == -1) {
                textToSpeech.setVoice(textToSpeech.getDefaultVoice());
                tvLang.setText("Language: "+DEFAULT_LANG.getDisplayLanguage());
                tvVoice.setText("Voice: "+textToSpeech.getVoice().getName());
            } else {
                Voice selectedVoice = voiceList.get(data.getIntExtra("selectedVoice", -1));
                textToSpeech.setVoice(selectedVoice);
                textToSpeech.setLanguage(selectedVoice.getLocale()); //Selezionando una voce si imposta automaticamente la lingua
                Toast.makeText(this, "VOICE: "+selectedVoice.getName().toUpperCase(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
