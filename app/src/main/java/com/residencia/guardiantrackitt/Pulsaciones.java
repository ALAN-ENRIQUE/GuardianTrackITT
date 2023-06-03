package com.residencia.guardiantrackitt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class Pulsaciones extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_SELECT_DEVICE = 2;
    private static final int REQUEST_PERMISSION_LOCATION = 3;

    private ListView listView;
    private TextView textView;
    private Button button;

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> devicesAdapter;
    private ArrayList<BluetoothDevice> devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulsaciones);

        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button2);

        devicesList = new ArrayList<>();
        devicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(devicesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice selectedDevice = devicesList.get(position);
                // Realizar acciones con el dispositivo seleccionado
                // (por ejemplo, establecer una conexión y recibir los datos)
                if (ActivityCompat.checkSelfPermission(Pulsaciones.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, mostrar mensaje de conexión exitosa
                    Toast.makeText(Pulsaciones.this, "Conexión exitosa con el dispositivo: " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();

                    // Aquí puedes llamar a la función o realizar las acciones necesarias
                    // según tus requisitos.
                } else {
                    // Permiso denegado, solicitar permiso
                    ActivityCompat.requestPermissions(Pulsaciones.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBluetoothDevice();
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // El dispositivo no es compatible con Bluetooth
            textView.setText("El dispositivo no es compatible con Bluetooth");
            button.setEnabled(false);
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            checkLocationPermission();
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            loadPairedDevices();
        }
    }

    private void loadPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()) {
            devicesAdapter.clear();
            devicesList.clear();
            for (BluetoothDevice device : pairedDevices) {
                devicesAdapter.add(device.getName());
                devicesList.add(device);
            }
            textView.setText("Dispositivos emparejados");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de ubicación concedido, cargar dispositivos emparejados
                loadPairedDevices();
            } else {
                // Permiso de ubicación denegado, mostrar mensaje de error o tomar medidas apropiadas
                // ...
            }
        }
    }

    private void selectBluetoothDevice() {
        Intent intent = new Intent(Pulsaciones.this, DeviceListActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_DEVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                checkLocationPermission();
            } else {
                textView.setText("El Bluetooth no está habilitado");
                button.setEnabled(false);
            }
        } else if (requestCode == REQUEST_SELECT_DEVICE) {
            if (resultCode == RESULT_OK) {
                // Obtener el dispositivo seleccionado del resultado
                BluetoothDevice selectedDevice = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Realizar acciones con el dispositivo seleccionado
                // (por ejemplo, establecer una conexión y recibir los datos)
            }
        }
    }
}
