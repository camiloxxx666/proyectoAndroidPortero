package grupo6.aplicacionportero.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import grupo6.aplicacionportero.Fragment.FragmentEventosHoy;
import grupo6.aplicacionportero.Fragment.FragmentScanner;
import grupo6.aplicacionportero.R;

public class InicioActivity extends AppCompatActivity
        implements FragmentEventosHoy.OnFragmentInteractionListener,
        FragmentScanner.OnFragmentInteractionListener,
        FragmentEventosHoy.Scannear
{

    public String FRAGMENT_SCANNER = "scannerFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment = new FragmentEventosHoy();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_inicio, fragment)
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent intent = new Intent(InicioActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_SCANNER) != null) {
            Fragment fragment = new FragmentEventosHoy();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_inicio, fragment)
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void invocarScanner(String text)
    {
        FragmentScanner fragment = new FragmentScanner();
        Bundle bundle = new Bundle();
        bundle.putString("idEspectaculo", text);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_inicio, fragment, FRAGMENT_SCANNER)
                .commit();
    }
}
