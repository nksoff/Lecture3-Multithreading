package ru.mail.park.lesson3;

import android.support.v4.util.LruCache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UrlDownloader {

    private static final UrlDownloader DOWNLOADER = new UrlDownloader();

    public static UrlDownloader getInstance() {
        return DOWNLOADER;
    }

    public interface Callback {
        void onLoaded(String request, String value);
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    private LruCache<String, String> cache = new LruCache<>(32);

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void load(final String url) {
        String cachedResult = cache.get(url);
        if (cachedResult != null) {
            callback.onLoaded(url, cachedResult);
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                String result;
                try {
                    result = loadInternal(url);
                } catch (IOException e) {
                    result = null;
                }
                notifyLoaded(url, result);
            }
        });
    }

    private void notifyLoaded(final String url, final String result) {
        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (result != null) {
                    cache.put(url, result);
                }
                if (callback != null) {
                    callback.onLoaded(url, result);
                }
            }
        });
    }

    private String loadInternal(String url) throws IOException {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        StringBuilder result = new StringBuilder();

        URL oracle = new URL(url);
        InputStream in = oracle.openStream();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            for (;;) {
                String inputLine = bufferedReader.readLine();
                if (inputLine == null) {
                    break;
                }

                result.append(inputLine).append('\n');
            }
        } finally {
            in.close();
        }

        return result.toString();
    }

}
