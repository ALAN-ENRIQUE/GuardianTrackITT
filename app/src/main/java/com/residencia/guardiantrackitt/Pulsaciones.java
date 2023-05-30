package com.residencia.guardiantrackitt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Pulsaciones extends AppCompatActivity {

    private TextView textData;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isConnected = false;
    private StringBuilder dataBuffer = new StringBuilder();
    private static final int MESSAGE_READ = 1;
    private static final int CONNECTING_STATUS = 2;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulsaciones);
        textData = findViewById(R.id.textData);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // El dispositivo no es compatible con Bluetooth
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            // El Bluetooth no está habilitado, puedes solicitar al usuario que lo habilite
            return;
        }

        // Obtén el dispositivo Bluetooth al que deseas conectarte (puedes buscarlo o usar una dirección específica)
        bluetoothDevice = bluetoothAdapter.getRemoteDevice("98d3:51:fdefba");

        // Inicia una nueva conexión Bluetooth en un hilo separado
        new ConnectThread().start();
    }

    private class ConnectThread extends Thread {
        public void run() {
            try {
                if (ActivityCompat.checkSelfPermission(Pulsaciones.class.newInstance(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothAdapter.cancelDiscovery();
                bluetoothSocket.connect();
                isConnected = true;

                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();

                // Crea un bucle para leer los datos recibidos del Arduino
                while (isConnected) {
                    byte[] buffer = new byte[1024];
                    int bytes = inputStream.read(buffer);
                    String data = new String(buffer, 0, bytes);
                    dataBuffer.append(data);

                    // Procesa los datos recibidos y actualiza la interfaz de usuario si es necesario
                    if (dataBuffer.toString().contains("\n")) {
                        String message = dataBuffer.toString().trim();
                        // Aquí puedes realizar las operaciones necesarias con los datos recibidos
                        // Por ejemplo, mostrarlos en un TextView o realizar algún cálculo
                        // Puedes usar un Handler para enviar un mensaje a la interfaz de usuario
                        Message messages = handler.obtainMessage(MESSAGE_READ, dataBuffer.length(), -1, dataBuffer.toString());
                        messages.sendToTarget();
                        dataBuffer.setLength(0);
                    }
                }
            } catch (IOException e) {
                // Manejar cualquier excepción que ocurra durante la conexión
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MESSAGE_READ) {
                // Aquí puedes actualizar la interfaz de usuario con los datos recibidos
                String receivedData = (String) msg.obj;
                // Actualiza tu interfaz de usuario con los datos recibidos
                // Por ejemplo, muestra los datos en un TextView
                // textView.setText(receivedData);
                textData.setText(receivedData);
                return true;
            } else if (msg.what == CONNECTING_STATUS) {
                // Aquí puedes manejar el estado de conexión (conectado o desconectado)
                if (msg.arg1 == 1) {
                    // Conexión establecida
                } else {
                    // Error de conexión
                }
            }
            return false;
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isConnected) {
            // Cierra la conexión Bluetooth y libera los recursos
            try {
                isConnected = false;
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (bluetoothSocket != null) {
                    bluetoothSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}