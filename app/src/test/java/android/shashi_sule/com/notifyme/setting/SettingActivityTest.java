package android.shashi_sule.com.notifyme.setting;

import android.content.Context;
import android.shashi_sule.com.notifyme.storage.LocalPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SettingActivityTest {

    @Mock
    private LocalPreferences mLocalPreferences;

    @Mock
    private Context mContext;

    @Before
    public void initMock() {
        mLocalPreferences = LocalPreferences.getInstance();
    }

    @Test
    public void checkNotificationsState() {

    }

    @Test
    public void checkPermissions() {

    }
} 