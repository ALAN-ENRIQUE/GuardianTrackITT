package com.residencia.guardiantrackitt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Contacto extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextNumero;
    private Button buttonAgregar;
    private ListView listViewContactos;
    private List<String> contactosList;
    private ArrayAdapter<String> contactosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        // Referencias a los componentes del layout
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextNumero = findViewById(R.id.editTextNumero);
        buttonAgregar = findViewById(R.id.buttonAgregar);
        listViewContactos = findViewById(R.id.listViewContactos);

        // Lista de contactos
        contactosList = new ArrayList<>();
        contactosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactosList);
        listViewContactos.setAdapter(contactosAdapter);

        // Acción del botón "Agregar"
        buttonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombre.getText().toString();
                String numero = editTextNumero.getText().toString();
                String contacto = nombre + " - " + numero;

                // Agregar el contacto a la lista y actualizar el ListView
                contactosList.add(contacto);
                contactosAdapter.notifyDataSetChanged();

                // Limpiar los campos de texto
                editTextNombre.setText("");
                editTextNumero.setText("");
            }
        });
    }
}
