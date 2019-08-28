package io.github.kelvao.cloudflarescrape;

import android.os.AsyncTask;

import java.util.HashMap;

public class CloudflareScrapeTask extends AsyncTask<Void, Void, HashMap<String, String>> {

    private final String UA;
    private final String URL;
    private final Callback callback;

    @SuppressWarnings("WeakerAccess")
    public CloudflareScrapeTask(String UA, String URL, Callback callback) {
        this.UA = UA;
        this.URL = URL;
        this.callback = callback;
    }

    @Override
    protected HashMap<String, String> doInBackground(Void... voids) {
        CloudflareScrapeCore cloudflareScrapeCore = new CloudflareScrapeCore(URL);
        cloudflareScrapeCore.setUA(UA);
        return cloudflareScrapeCore.Cookies2HashMap(cloudflareScrapeCore.cookies());
    }

    @Override
    protected void onPostExecute(HashMap<String, String> cookies) {
        callback.CloudflareScrapedCookies(cookies);
    }

    public interface Callback {
        void CloudflareScrapedCookies(HashMap<String, String> cookies);
    }
}
