package com.matheussilvagarcia.ecomunity.Model;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.matheussilvagarcia.ecomunity.R;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TabLayout apptabs;
    ViewPager2 pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initvar();

        mAuth = FirebaseAuth.getInstance();
        Toast.makeText(this,mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
    }
    void initvar()
    {
        mAuth = FirebaseAuth.getInstance();
        apptabs = findViewById(R.id.apptabs);
        pager = findViewById(R.id.pager);
    }
}