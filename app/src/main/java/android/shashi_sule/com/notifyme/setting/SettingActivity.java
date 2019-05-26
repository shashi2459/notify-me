package android.shashi_sule.com.notifyme.setting;

import android.os.Bundle;
import android.shashi_sule.com.notifyme.R;
import android.shashi_sule.com.notifyme.storage.LocalPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Switch notificationView = (Switch) findViewById(R.id.notifications_view);

        boolean notificationsEnabled = LocalPreferences
                .getInstance().isNotificationsEnabled(this);
        notificationView.setChecked(notificationsEnabled);

        notificationView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                LocalPreferences
                        .getInstance()
                        .put(isChecked, SettingActivity.this);
            }
        });
    }

}
