package com.example.instant_translator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class Activity_settings extends AppCompatActivity {

    // Variables necesaries per especificar els idiomes.
    private String[] languages = new String[]{"Spanish", "English", "Catalan", "German", "French"};
    private String Ori = "";
    private String Fin = "";
    private Spinner OriSpinner;
    private Spinner FinSpinner;

    // Metodo llamado para printar la vista de los settings del App.
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // Seleccionar el View a mostrar.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Seleccion de los spinners del layout
        OriSpinner = findViewById(R.id.Spinner_OrignalLanguage);
        FinSpinner = findViewById(R.id.Spinner_FinalLanguage);
        // Transformacion del Array de texto a un adaptador pera trabajar con el.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Indicar al spinner Ori (lenguage Origen) que datos tiene que mostrar y contener.
        OriSpinner.setAdapter(adapter);
        OriSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){ //Spannish
                    Ori = "ES";
                }
                if(position == 1){ //English
                    Ori = "EN";
                }
                if(position == 2){ //Catalan
                    Ori = "CA";
                }
                if(position == 3){ //German
                    Ori = "DE";
                }
                if(position == 4){ //French
                    Ori = "FR";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Indicar al spinner Fin (lenguage a traducir) que datos tiene que mostrar y contener.
        FinSpinner.setAdapter(adapter);
        FinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){ //Spannish
                    Fin = "ES";
                }
                if(position == 1){ //English
                    Fin = "EN";
                }
                if(position == 2){ //Catalan
                    Fin = "CA";
                }
                if(position == 3){ //German
                    Fin = "DE";
                }
                if(position == 4){ //French
                    Fin = "FR";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Botón de retorno que nos envia al mainActivity.
        Button btnReturn = (Button) findViewById(R.id.button_Setting_Return);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_settings.this, MainActivity.class);
                intent.putExtra("OriLan", Ori);
                intent.putExtra("FinLan", Fin);
                startActivity(intent);
            }
        });
    }
}
