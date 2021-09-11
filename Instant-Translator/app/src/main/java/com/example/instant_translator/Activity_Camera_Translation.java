package com.example.instant_translator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.Base64;

public class Activity_Camera_Translation extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_translation);

        // TEXTVIEW DONDE PONER LA TRADUCCIÓN
        EditText textCameraTranslated = (EditText) findViewById(R.id.content_camera_translated);
        String OriLanguage = (String) getIntent().getStringExtra("OriLan");
        String FinLanguage = (String) getIntent().getStringExtra("FinLan");

        // CAPTURA DE LA IMAGEN
        Bitmap photo = (Bitmap) getIntent().getParcelableExtra("photo");

        // TRANSFORMAR IMAGE TO BASE64
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        photo.recycle();

        Base64.Encoder encoder = Base64.getEncoder();
        String encoded = encoder.encodeToString(byteArray);

        // PETICIO POST AL SERVER
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        RequestBody body = RequestBody.create(("{'image': '" + encoded + "'}").getBytes());
        Request request = new Request.Builder()
                .url("https://us-central1-upheld-fold-865231.cloudfunctions.net/Image-to-Text")
                .addHeader("Content-Type", "application/json")
                .method("POST", body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("////////////////////");
                System.out.println("Response FAILURE !");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {

                    final String translation = response.body().string();

                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    RequestBody body = RequestBody.create(("{'translate': '" + translation + "','source': '"+OriLanguage+"','target':'"+FinLanguage+"'}").getBytes());
                    Request request = new Request.Builder()
                            .url("https://europe-west1-upheld-fold-865231.cloudfunctions.net/Translate-Test")
                            .addHeader("Content-Type", "application/json")
                            .method("POST", body)
                            .build();


                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                final String translation2 = response.body().string();
                                Activity_Camera_Translation.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textCameraTranslated.setText(translation2);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        // Funcionalidad del boton para reproducir el texto del textView.
        Button btnRepro = (Button) findViewById(R.id.rep_camera);
        btnRepro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                RequestBody body = RequestBody.create(("{'text': '" + textCameraTranslated.getText().toString() + "'}").getBytes());
                Request request = new Request.Builder()
                        .url("https://europe-west2-sylvan-triumph-526847.cloudfunctions.net/text2Speech")
                        .addHeader("Content-Type", "application/json")
                        .method("POST", body)
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

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
        Button btn = (Button) findViewById(R.id.button_return_from_camera);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Camera_Translation.this, MainActivity.class);
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