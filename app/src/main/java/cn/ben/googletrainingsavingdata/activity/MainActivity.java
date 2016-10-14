package cn.ben.googletrainingsavingdata.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cn.ben.googletrainingsavingdata.R;
import cn.ben.googletrainingsavingdata.fragment.SQLFragment;
import cn.ben.googletrainingsavingdata.fragment.SavingFilesFragment;
import cn.ben.googletrainingsavingdata.fragment.SharedPreferencesFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
    }

    public void switchSharedPreferences(@SuppressWarnings("UnusedParameters") View view) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new SharedPreferencesFragment());
        fragmentTransaction.commit();
    }

    public void switchSavingFiles(@SuppressWarnings("UnusedParameters") View view) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new SavingFilesFragment());
        fragmentTransaction.commit();
    }

    public void switchSQL(@SuppressWarnings("UnusedParameters") View view) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new SQLFragment());
        fragmentTransaction.commit();
    }
}
