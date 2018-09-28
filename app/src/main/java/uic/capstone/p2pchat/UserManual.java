package uic.capstone.p2pchat;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserManual extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext());
        viewPager.setAdapter(imageAdapter);
    }
}
