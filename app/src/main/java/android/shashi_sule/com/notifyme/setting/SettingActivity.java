package android.shashi_sule.com.notifyme.setting;

import android.content.Intent;
import android.os.Bundle;
import android.shashi_sule.com.notifyme.R;
import android.shashi_sule.com.notifyme.storage.LocalPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.FileNotFoundException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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

    // FIXME: 9/18/2018 Remove this function
    private void test() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        if (intent.resolveActivity(getPackageManager()) != null) {
            Intent.createChooser(intent, "Choose");
        }

        Certificate ca;
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            ca = factory.generateCertificate(openFileInput(".cer"));

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.setCertificateEntry("ca", ca);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
        } catch (KeyStoreException | CertificateException | FileNotFoundException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

    }
}
