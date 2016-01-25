package de.kuschku.quasseldroid_ng.ui.coresetup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import de.kuschku.libquassel.objects.types.StorageBackend;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.ui.parcelableUtil.StorageBackendParcelable;
import de.kuschku.util.ui.parcelableUtil.ClassLoaderUtils;

@UiThread
public class CoreSetupActivity extends AppCompatActivity implements StorageBackendFragment.OnListFragmentInteractionListener {

    int page = 0;
    ArrayList<StorageBackendParcelable> storageBackends;
    int selectedBackend;
    Map<String, QVariant> config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_setup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        bundle.setClassLoader(ClassLoaderUtils.loader);
        storageBackends = bundle.getParcelableArrayList("backends");

        if (savedInstanceState != null) {
            page = savedInstanceState.getInt("page", 0);
        }

        switchToPage(page);
    }

    private void switchToPage(int page) {
        if (page < 0) page = 0;
        Fragment fragment = getFragment(page);
        if (fragment == null) finished();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.host, fragment);
        transaction.commit();
    }

    private Fragment getFragment(int page) {
        if (page == 0) {
            return StorageBackendFragment.newInstance(storageBackends);
        } else {
            return null;
        }
    }

    private void finished() {
        Toast.makeText(this, "Selection done!", Toast.LENGTH_LONG).show();
        Log.e("DONE", String.valueOf(selectedBackend));
        Log.e("DONE", String.valueOf(config));
        Intent intent = new Intent("RESULT_SELECTED_BACKEND");
        intent.putExtra("backend", storageBackends.get(selectedBackend).DisplayName);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onListFragmentInteraction(StorageBackend item) {
        selectedBackend = storageBackends.indexOf(item);
        switchToPage(page+1);
    }
}
