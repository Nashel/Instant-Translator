package com.example.instant_translator;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.getbase.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    // Contantes necesarias para la camara, audio e idioma
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private String OriLanguage = "ES"; // Idioma Origen
    private String FinLanguage = "EN"; // Idioma a Traducir


    // Metodo llamado para printar la vista del MainActivity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Seleccionar el View a mostrar.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Comprobación si ha habido una modificación en los idiomas del traductor.
        String tempOriLanguage = (String) getIntent().getStringExtra("OriLan");
        String tempFinLanguage = (String) getIntent().getStringExtra("FinLan");
        if(tempOriLanguage != null && tempFinLanguage != null){
            OriLanguage = tempOriLanguage;
            FinLanguage = tempFinLanguage;
        }

        // Tratamiento de los eventos on click de los elementos de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Acción asociada al evento click del Elemento "Traducción de tipo Texto"
        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.fab_text);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_Text_Translator.class);
                intent.putExtra("OriLan", OriLanguage);
                intent.putExtra("FinLan", FinLanguage);
                startActivity(intent);
            }
        });
        // Acción asociada al evento click del Elemento "Traducción de tipo Camara"
        FloatingActionButton cameraBtn = (FloatingActionButton) findViewById(R.id.fab_camera);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprobación de los permisos de la camara.
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    // Inicia la camara, Permiso concedido por el usuario.
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }

            }
        });
        // Acción asociada al evento click del Elemento "Traducción de tipo Audio"
        FloatingActionButton audioBtn = (FloatingActionButton) findViewById(R.id.fab_audio);
        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

    }
    // Relleno del menu de la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    // Metodo cuando se selecciona un elemento de la toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Seleccionado elemento Settings de la toolbar
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Activity_settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // Metodo llamado para los permisos de la camara
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Metodo que muestra el Pop-Up del evento para capturar el sonido.
    protected void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speack to translated");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo que se llama cuando se ha realizado una foto o grabado un audio.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // Captura de la imagen junto a la configuración del idioma el qual se envia al
        // view de la camara para su tratamiento.
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(MainActivity.this, Activity_Camera_Translation.class);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            intent.putExtra("photo", photo);
            intent.putExtra("OriLan", OriLanguage);
            intent.putExtra("FinLan", FinLanguage);
            startActivity(intent);
        }
        // Captura  del audio transformado en lista de String  junto a la configuración del idioma
        // que se envia al view del audio para su tratamiento.
        if(requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {
            ArrayList<String> textList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            Intent intent = new Intent(MainActivity.this, Activity_Audio_Translation.class);
            intent.putExtra("audioText", textList);
            intent.putExtra("OriLan", OriLanguage);
            intent.putExtra("FinLan", FinLanguage);
            startActivity(intent);
        }
    }



}