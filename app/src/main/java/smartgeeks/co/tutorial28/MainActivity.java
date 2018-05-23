package smartgeeks.co.tutorial28;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btnconsultar, btnGuardar, btnSeleccionar, btnEncriptar;
    EditText etId, etNombres, etTelefono;
    TextView txtViewContacto, txtViewEncriptado;
    private final int PICK_CONTACT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        btnSeleccionar = (Button) findViewById(R.id.btnSeleccionar);
        btnEncriptar = (Button) findViewById(R.id.btnEncriptar);
        txtViewContacto = (TextView) findViewById(R.id.txtViewContact);
      //  txtViewEncriptado = (TextView) findViewById(R.id.txtViewEncript);


        //Prueba 1
        //Prueba 2

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CargarDatos().execute("http://192.168.0.100/CursoAndroid/registro.php?nombres="+txtViewContacto.getText().toString()+"&tel="+txtViewEncriptado.getText().toString());
            }
        });

        btnSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
    }

    //public void LlamarContactos(View v) {
      //  Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //startActivityForResult(intent, PICK_CONTACT);
    //}

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PICK_CONTACT) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);

                if (c.moveToFirst()) {
                    String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    txtViewContacto.setText(name);
                    Toast.makeText(this, "Piked:" + name, Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private class CargarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(getApplicationContext(), "Se almacenaron los datos correctamente", Toast.LENGTH_LONG).show();

        }
    }


    private class ConsultarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            JSONArray ja = null;
            try {
                ja = new JSONArray(result);
                etNombres.setText(ja.getString(1));
                //etTelefono.setText(ja.getString(2));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void crypt(View view) {
        try {
            //Obtenemos el texto desde el cuadro de texto
            String original = txtViewContacto.getText().toString();
            RSA rsa = new RSA();
            //le asignamos el Contexto
            rsa.setContext(getBaseContext());
            //Generamos un juego de claves
            rsa.genKeyPair(1024);
            //Guardamos en la memoria las claves
            rsa.saveToDiskPrivateKey("rsa.pri");
            rsa.saveToDiskPublicKey("rsa.pub");
            //Ciframos
            String encode_text = rsa.Encrypt(original);
            //Mostramos el texto cifrado
            txtViewEncriptado.setText(encode_text);
           // etTelefono.setText(encode_text);
        } catch (Exception e) {

        }
    }

    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl = myurl.replace(" ","%20");
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
