package com.example.instant_translator;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_Text_Translator extends AppCompatActivity {

    // Contantes necesarias para la traducción del texto
    EditText textToTranslated;
    EditText translated;
    private MediaPlayer mediaPlayer;

    // Metodo llamado para printar la vista del Text_Translator.
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_translator);
        // Captura de la configuración de los idiomas.
        String OriLanguage = (String) getIntent().getStringExtra("OriLan");
        String FinLanguage = (String) getIntent().getStringExtra("FinLan");
        // Captura de los elementos del layout.
        EditText textToTranslated = (EditText) findViewById(R.id.content_text_to_translate);
        EditText translated = (EditText) findViewById(R.id.content_text_translated);
        // Boton de translated junto a la petición a la cloud function de traducció de texto.
        Button btnTranslate = (Button) findViewById(R.id.button_text_translate);
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creación de la petición
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                RequestBody body = RequestBody.create(("{'translate': '" + textToTranslated.getText().toString() + "','source': '"+OriLanguage+"','target':'"+FinLanguage+"'}").getBytes());
                Request request = new Request.Builder()
                        .url("https://europe-west1-upheld-fold-582146.cloudfunctions.net/Translate-Test")
                        .addHeader("Content-Type", "application/json")
                        .method("POST", body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    // Tractamiento respuesta erronea de la cloud Function.
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }
                    // Tratamiento respuesta exitosa de la cloud Function.
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String translation = response.body().string();
                            Activity_Text_Translator.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Capturamos la respuesta dada por la cloud Function y seteamos
                                    // la información en el textView correspondiente en el layout.
                                    translated.setText(translation);
                                }
                            });
                        }
                    }
                });
                }
            });

        // Funcionalidad del boton para reproducir el texto del textView.
        Button btnRepro = (Button) findViewById(R.id.rep_text);
        btnRepro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Peticion a la cloud function Text2Speech
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                RequestBody body = RequestBody.create(("{'text': '" + translated.getText().toString() + "'}").getBytes());
                Request request = new Request.Builder()
                        .url("https://europe-west2-sylvan-triumph-5247865.cloudfunctions.net/text2Speech")
                        .addHeader("Content-Type", "application/json")
                        .method("POST", body)
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    // Tractamiento respuesta erronea de la cloud Function.
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }
                    // Tratamiento respuesta exitosa de la cloud Function.
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String speech64 = response.body().string();
                            reproduceAudio(speech64);
                        }
                    }
                });
            }
        });

        // Funcionalidad del boton de return enviando la configuración actual del idioma.
        Button btnReturn = (Button) findViewById(R.id.button_return);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Text_Translator.this, MainActivity.class);
                intent.putExtra("OriLan", OriLanguage);
                intent.putExtra("FinLan", FinLanguage);
                startActivity(intent);
            }
        });

    }

    // Funcion que reproduce el texto.
    public void reproduceAudio(String speech64) {
        try{
            String url = "data:audio/mp3;base64,"+speech64;
            MediaPlayer mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
                mediaPlayer.setVolume(100f, 100f);
                mediaPlayer.setLooping(false);
            } catch (IllegalArgumentException e) {
                Toast.makeText(getApplicationContext(), "You might not set the DataSource correctly!", Toast.LENGTH_LONG).show();
            } catch (SecurityException e) {
                Toast.makeText(getApplicationContext(), "You might not set the DataSource correctly!", Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "You might not set the DataSource correctly!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    player.start();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
