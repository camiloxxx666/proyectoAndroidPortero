package grupo6.aplicacionportero.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import grupo6.aplicacionportero.R;
import grupo6.aplicacionportero.Util;

public class MainActivity extends AppCompatActivity
{
    private ImageView logoConfig;
    private EditText txtCI, txtPassword;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoConfig = (ImageView) findViewById(R.id.logoConfig);
        logoConfig.setOnLongClickListener(mostrarOpciones());

        txtCI = (EditText) findViewById(R.id.txtCI);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        loginBtn = (Button) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(loguearse());

    }

    public View.OnClickListener loguearse()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Moverse con el back
                final String ci = txtCI.getText().toString();
                final String password = txtPassword.getText().toString();

                try
                {
                    SharedPreferences settings = getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("ci", ci);
                    editor.putString("pass", password);
                    editor.commit();
                    finish();

                    final String tenant = Util.getProperty("tenant.name", getApplicationContext());
                    String ip = settings.getString("ip", "");
                    String puerto = settings.getString("puerto", "");

                    //Con esto se mantiene la sesion de este lado.
                    CookieHandler.setDefault(new CookieManager());

                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                            "http://"+ip+":"+puerto+"/loginPortero/"+ "?cedula=" + ci + "&password=" + password,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    Intent intent = new Intent(MainActivity.this, InicioActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Error en el login. Vuelva a inentarlo.", Toast.LENGTH_SHORT).show();

                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("X-TenantID", tenant);

                            return params;
                        }
                    };

                    requestQueue.add(stringRequest);
                }
                catch(IOException ioe)
                {
                    ioe.printStackTrace();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
        };
    }

    public void onBackPressed() {
        finish();
    }

    public View.OnLongClickListener mostrarOpciones()
    {
        return new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                Intent intentConfiguracion = new Intent(MainActivity.this, ConfiguracionActivity.class);
                startActivity(intentConfiguracion);
                return true;
            }
        };
    }
}
