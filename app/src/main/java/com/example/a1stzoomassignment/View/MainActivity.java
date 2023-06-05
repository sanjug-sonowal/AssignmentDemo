package com.example.a1stzoomassignment.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a1stzoomassignment.Model.Repository;
import com.example.a1stzoomassignment.R;
import com.example.a1stzoomassignment.ViewModel.RepositoryAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fabBtn;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        fabBtn = (FloatingActionButton) findViewById(R.id.fabBtn);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new RepoScreenFragment())
                .commit();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new RepoScreenFragment()).commit();
        }

        replaceFragment(new RepoScreenFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new RepoScreenFragment());
                    break;

//                    TODO: if required i'll use this all tabs'

//                case R.id.shorts:
//                    replaceFragment(new GitLinkFragment());
//                    break;
//                case R.id.subscriptions:
//                    replaceFragment(new SubScriberFragment());
//                    break;
//                case R.id.library:
//                    replaceFragment(new ProfileFragment());
//                    break;
            }

            return true;
        });

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RepoScreenFragment fragment = (RepoScreenFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                if (fragment != null) {
                    fragment.showBottomDialog();
                }
            }
        });
    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


}