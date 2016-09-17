package ru.mail.park.lesson3;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final static String TEXT_STATE_INIT = "ru.mail.park.lesson3.MainActivity.TEXT_STATE_INIT";
    private final static String TEXT_STATE_LOADING = "ru.mail.park.lesson3.MainActivity.TEXT_STATE_LOADING";
    private final static String TEXT_STATE_UNAVAILABLE = "ru.mail.park.lesson3.MainActivity.TEXT_STATE_UNAVAILABLE";
    private final static String TEXT_STATE_LOADED = "ru.mail.park.lesson3.MainActivity.TEXT_STATE_LOADED";

    private static final String URL_1 = "https://gist.githubusercontent.com/anonymous/66e735b3894c5e534f2cf381c8e3165e/raw/8c16d9ec5de0632b2b5dc3e5c114d92f3128561a/gistfile1.txt";
    private static final String URL_2 = "https://gist.githubusercontent.com/anonymous/be76b41ddf012b761c15a56d92affeb6/raw/bb1d4f849cb79264b53a9760fe428bbe26851849/gistfile1.txt";

    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

    private TextView text1;
    private TextView text2;

    private String text1State;
    private String text2State;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.open_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AnotherActivity.class));
            }
        });

        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);

        changeState(URL_1, TEXT_STATE_INIT);
        changeState(URL_2, TEXT_STATE_INIT);

        UrlDownloader.getInstance().setCallback(new UrlDownloader.Callback() {
            @Override
            public void onLoaded(String key, String value) {
                onTextLoaded(key, value);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void loadFromUrl(String url) {
        changeState(url, TEXT_STATE_LOADING);
    }

    private void onTextLoaded(String url, String loadedText) {
        final String state = loadedText == null ? TEXT_STATE_UNAVAILABLE : TEXT_STATE_LOADED;
        final boolean showToast = loadedText != null;
        changeState(url, state, loadedText);

        if(showToast)
            Toast.makeText(MainActivity.this, loadedText, Toast.LENGTH_SHORT).show();
    }

    private TextView getTextViewByUrl(String url) {
        switch (url) {
            case URL_1:
                return text1;
            case URL_2:
                return text2;
            default:
                throw new IllegalArgumentException("Unknown url: " + url);
        }
    }

    private View.OnClickListener getClickListenerForUrl(String url) {
        final String loadUrl = url;
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFromUrl(loadUrl);
            }
        };
    }

    private void moveStateInit(String url) {
        final TextView text = getTextViewByUrl(url);
        text.setText("Click me");
        text.setOnClickListener(getClickListenerForUrl(url));
    }

    private void moveStateLoading(String url) {
        final TextView text = getTextViewByUrl(url);
        text.setText("Loading...");
        text.setOnClickListener(null);
        UrlDownloader.getInstance().load(url);
    }

    private void moveStateUnavailable(String url) {
        final TextView text = getTextViewByUrl(url);
        text.setText("Data unavailable");
        text.setOnClickListener(getClickListenerForUrl(url));
    }

    private void moveStateLoaded(String url, String loadedText) {
        final TextView text = getTextViewByUrl(url);
        text.setText(loadedText);
        text.setOnClickListener(null);
    }

    private void changeState(String url, String textState, @Nullable String loadedText) {
        switch (textState) {
            case TEXT_STATE_INIT:
                moveStateInit(url);
                break;
            case TEXT_STATE_LOADING:
                moveStateLoading(url);
                break;
            case TEXT_STATE_UNAVAILABLE:
                moveStateUnavailable(url);
                break;
            case TEXT_STATE_LOADED:
                moveStateLoaded(url, loadedText);
                break;
            default:
                throw new IllegalArgumentException("Unknown text state: " + textState);
        }
    }

    private void changeState(String url, String textState) {
        changeState(url, textState, null);
    }
}
