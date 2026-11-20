package com.gathin.tvhome.weather;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * The weather for the launcher, which has no INTERNET permission: content://com.gathin.tvhome.weather/today, one
 * row of temp, icon, max, min, rain and place. BoM is read at most every REFRESH_MS, and only when asked, so
 * nothing runs while Home is not on screen. Only apps signed like this one may ask.
 */
public final class WeatherProvider extends ContentProvider {
    static final String[] COLUMNS = {"temp", "icon", "max", "min", "rain", "place"};
    private static final String TAG = "BomWeather";
    private static final long REFRESH_MS = 30 * 60 * 1000L, RETRY_MS = 5 * 60 * 1000L;
    private static final int TIMEOUT_MS = 8000, MAX_BYTES = 2 * 1024 * 1024;

    private String[] row;          // the last good reading
    private long nextFetch;

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] args, String sort) {
        Context context = getContext();
        String caller = getCallingPackage();
        if (caller == null || context.getPackageManager().checkSignatures(caller, context.getPackageName())
                != PackageManager.SIGNATURE_MATCH) {
            throw new SecurityException(caller + " is not signed like " + context.getPackageName());
        }
        long now = System.currentTimeMillis();
        if (now >= nextFetch) {
            try {
                row = fetch(context);
                nextFetch = now + REFRESH_MS;
            } catch (Exception e) {
                Log.w(TAG, "BoM not read: " + e);
                nextFetch = now + RETRY_MS;
            }
        }
        MatrixCursor c = new MatrixCursor(COLUMNS);
        if (row != null) c.addRow(row);
        return c;
    }

    /** null when no place is set. */
    private static String[] fetch(Context context) throws Exception {
        String obs = context.getString(R.string.observations_url), fc = context.getString(R.string.forecast_url);
        String aac = context.getString(R.string.forecast_aac);
        if (obs.isEmpty() || fc.isEmpty() || aac.isEmpty()) return null;
        String temp = Bom.airTemp(new String(get(obs), StandardCharsets.UTF_8));
        Bom.Today t = Bom.today(new ByteArrayInputStream(get(fc)), aac);
        if (t == null) throw new IOException(aac + " is not in " + fc);
        return new String[]{temp == null ? "" : temp, t.icon, t.max, t.min, t.rain, t.place};
    }

    private static byte[] get(String url) throws IOException {
        if (!url.startsWith("https://")) throw new IOException("not https: " + url);
        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
        c.setConnectTimeout(TIMEOUT_MS);
        c.setReadTimeout(TIMEOUT_MS);
        c.setInstanceFollowRedirects(false);
        try {
            if (c.getResponseCode() != 200) throw new IOException(url + " answered " + c.getResponseCode());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (InputStream in = c.getInputStream()) {
                byte[] buf = new byte[16384];
                for (int n; (n = in.read(buf)) > 0; ) {
                    out.write(buf, 0, n);
                    if (out.size() > MAX_BYTES) throw new IOException(url + " is too big");
                }
            }
            return out.toByteArray();
        } finally {
            c.disconnect();
        }
    }

    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int delete(Uri uri, String s, String[] a) { throw new UnsupportedOperationException(); }
    @Override public int update(Uri uri, ContentValues v, String s, String[] a) { throw new UnsupportedOperationException(); }
}
