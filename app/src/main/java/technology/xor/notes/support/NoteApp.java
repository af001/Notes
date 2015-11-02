package technology.xor.notes.support;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;

import orangegangsters.lollipin.managers.LockManager;
import technology.xor.notes.lockscreen.CustomLockPin;
import technology.xor.notes.notes.NewNote;
import technology.xor.notes.notes.R;
import technology.xor.notes.sites.NewSite;
import technology.xor.notes.sites.SiteNoteView;
import technology.xor.notes.sites.SiteView;

public class NoteApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // INITIALIZE ANDROID BOOTSTRAP LIBRARY
        TypefaceProvider.registerDefaultIconSets();

        // INITIALIZE THE PINLOCK
        LockManager<CustomLockPin> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomLockPin.class);
        lockManager.getAppLock().setTimeout(AppData.APPLICATION_TIMEOUT);

        // PINLOCK IGNORE ACTIVITIES
        lockManager.getAppLock().addIgnoredActivity(NewNote.class);
        lockManager.getAppLock().addIgnoredActivity(NewSite.class);
        lockManager.getAppLock().addIgnoredActivity(SiteNoteView.class);
        lockManager.getAppLock().addIgnoredActivity(CameraView.class);

        lockManager.getAppLock().setLogoId(R.mipmap.security_lock);
    }
}
