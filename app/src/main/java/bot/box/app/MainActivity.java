package bot.box.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;


import bot.box.universal.batch.Bot;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getStoragePermission(this);

        EditText edit = findViewById(R.id.edit);
        Button btn = findViewById(R.id.btn);

        btn.setOnClickListener(v -> {
            Bot bot = new Bot.InstaBot()
                    .feedUrl(edit.getText().toString())
                    .fileName("TestFile")
                    .instaResult(((isSuccess, mStoragePath) -> {
                        Log.d(LOG_TAG, isSuccess + " " + mStoragePath);
                    }))
                    .storageDirectory(Environment.getExternalStorageDirectory() + "/XXX Test Folder").engage();


        });

    }

    public static boolean getStoragePermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
