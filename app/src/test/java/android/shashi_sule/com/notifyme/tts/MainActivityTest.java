package android.shashi_sule.com.notifyme.tts;

import android.content.Context;
import android.shashi_sule.com.notifyme.storage.LocalPreferences;
import android.shashi_sule.com.notifyme.utils.Utils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock
    Context mMockContext;

    @Mock
    private LocalPreferences mLocalPreferences;

    @Before
    public void initMock() {
        mLocalPreferences = LocalPreferences.getInstance();
    }

    @Test
    public void checkNotificationsEnabledOrNot() {

        boolean notificationsEnabled = mLocalPreferences.isNotificationsEnabled(mMockContext);

        Assert.assertEquals(true, notificationsEnabled);
    }

    @Test
    public void checkHeadSetPluggedOrNot() {

        boolean headsets = Mockito.verify(Utils.isHeadsetsConnected(mMockContext));

        Assert.assertEquals(true, headsets);
    }

    @Test
    public void checkPermissions() {
        // TODO: 9/19/2018
    }

} 