package grupo6.aplicacionportero.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grupo6.aplicacionportero.R;
import grupo6.aplicacionportero.Util;
import grupo6.aplicacionportero.modelo.Espectaculo;
import grupo6.aplicacionportero.modelo.TipoEspectaculo;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FragmentEventosHoy extends Fragment
{
    private ListView lvEspectaculos;
    private OnFragmentInteractionListener mListener;
    private String cedula;
    Scannear scanner;

    public FragmentEventosHoy() {
        // Required empty public constructor
    }

    public interface Scannear{
        public void invocarScanner(String text);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_eventos_hoy, container, false);
        lvEspectaculos = (ListView) view.findViewById(R.id.lvEspectaculos);

        SharedPreferences settings = getActivity().getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE);
        cedula = settings.getString("ci", "");

        try
        {
            final String tenant = Util.getProperty("tenant.name", getActivity().getApplicationContext());
            String ip = settings.getString("ip", "");
            String puerto = settings.getString("puerto", "");

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    "http://"+ip+":"+puerto+"/verEspectaculosYSusRealizacionesDeHoy/"+ "?cedula="+cedula ,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Type listaEspectaculos = new TypeToken<List<Espectaculo>>(){}.getType();
                            List<Espectaculo> resultJson = new Gson().fromJson(response, listaEspectaculos);

                            FragmentEventosHoy.EspectaculoAdapter adapter = new FragmentEventosHoy.EspectaculoAdapter(getActivity(), R.layout.espectaculo_row, resultJson);
                            lvEspectaculos.setAdapter(adapter);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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

            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);

            requestQueue.add(stringRequest);

        }
        catch(IOException ioe)
        {
            Toast.makeText(getActivity(), ioe.getMessage(), Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            scanner = (Scannear) getActivity();
        }catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " debe implementar Scanner.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class EspectaculoAdapter extends ArrayAdapter
    {
        private List<Espectaculo> listaEspectaculos;
        private int resource;
        private LayoutInflater inflater;

        public EspectaculoAdapter(Context context, int resource, List<Espectaculo> espectaculos){
            super(context, resource, espectaculos);
            listaEspectaculos = espectaculos;
            this.resource = resource;
            inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(resource, null);

            TextView espNombre;
            TextView espDescripcion;
            TextView espTipo;
            ImageView scann;

            espNombre = (TextView) convertView.findViewById(R.id.espNombre);
            espDescripcion = (TextView) convertView.findViewById(R.id.espDescripcion);
            espTipo = (TextView) convertView.findViewById(R.id.espTipo);
            scann = (ImageView) convertView.findViewById(R.id.scann);

            //No hay imagen pa esta app

            espNombre.setText(listaEspectaculos.get(position).getNombre());
            espDescripcion.setText("Descripcion: " + listaEspectaculos.get(position).getDescripcion());

            StringBuilder sbTipos = new StringBuilder("Género: ");
            if (listaEspectaculos.get(position).getTipoEspectaculo() != null)
            {
                for (TipoEspectaculo te : listaEspectaculos.get(position).getTipoEspectaculo()) {
                    sbTipos.append(" - ");
                    sbTipos.append(te.getNombre());
                }

                espTipo.setText(sbTipos.toString());
            }

            scann.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanner.invocarScanner(listaEspectaculos.get(position).getId());

                }
            });


            return convertView;
        }
    }
}
