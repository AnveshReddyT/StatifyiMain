package statifyi.com.statifyi.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StatFs;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import statifyi.com.statifyi.api.GCMServerAPI;
import statifyi.com.statifyi.api.RemoteServerAPI;
import statifyi.com.statifyi.api.service.GCMAPIService;
import statifyi.com.statifyi.api.service.GCMAPIServiceImpl;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.api.service.UserAPIServiceImpl;

/**
 * Created by KT on 5/24/15.
 */
public class NetworkUtils {

    public static final String SERVER_IP = "54.201.38.232";
    private static final String BASE_URL = "http://" + SERVER_IP + ":8080";
    private static final String GCM_URL = "https://iid.googleapis.com";
    private static final String BASE_CONTEXT = "/Statifyi/src/users";
    private static final String BIG_CACHE_PATH = "picasso-big-cache";
    private static final int MIN_DISK_CACHE_SIZE = 32 * 1024 * 1024;       // 32MB
    private static final int MAX_DISK_CACHE_SIZE = 512 * 1024 * 1024;      // 512MB
    private static final float MAX_AVAILABLE_SPACE_USE_FRACTION = 0.9f;
    private static final float MAX_TOTAL_SPACE_USE_FRACTION = 0.25f;

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public static Cache provideOkHttpCache(Context mContext) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(mContext.getCacheDir(), cacheSize);
    }

    public static Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("EEE MMM dd HH:mm:ss 'Z' yyyy");
        return gsonBuilder.create();
    }

    public static HttpLoggingInterceptor provideOkHttpClientLogging() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    public static OkHttpClient provideOkHttpClient(Context mContext, boolean enableLogging) {
        OkHttpClient client = new OkHttpClient();
        client.setCache(provideOkHttpCache(mContext));
        client.setConnectTimeout(5, TimeUnit.MINUTES);
        client.setReadTimeout(5, TimeUnit.MINUTES);
        if (enableLogging) {
            client.interceptors().add(provideOkHttpClientLogging());
        }
        return client;
    }

    public static Retrofit provideRetrofit(Context mContext, String baseUrl, boolean enableLogging) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .baseUrl(baseUrl)
                .client(provideOkHttpClient(mContext, enableLogging))
                .build();
    }

    public static Picasso providePicasso(Context mContext) {
        return Picasso.with(mContext);
    }

    public static GCMServerAPI provideGCMServerAPI(Context mContext) {
        return provideRetrofit(mContext, GCM_URL, false).create(GCMServerAPI.class);
    }

    public static RemoteServerAPI provideServerAPI(Context mContext) {
        return provideRetrofit(mContext, BASE_URL, true).create(RemoteServerAPI.class);
    }

    public static UserAPIService provideUserAPIService(Context mContext) {
        return new UserAPIServiceImpl(provideServerAPI(mContext));
    }

    public static GCMAPIService provideGCMAPIService(Context mContext) {
        return new GCMAPIServiceImpl(provideGCMServerAPI(mContext));
    }

    public static String provideAvatarUrl(String mobile) {
        return BASE_URL + BASE_CONTEXT + "/image/" + mobile;
    }

    public static Downloader createBigCacheDownloader(Context ctx) {
        File cacheDir = createDefaultCacheDir(ctx, BIG_CACHE_PATH);
        long cacheSize = calculateDiskCacheSize(cacheDir);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().header("Cache-Control", "max-age=" + (60 * 60 * 24 * 365)).build();
            }
        });

        okHttpClient.setCache(new Cache(cacheDir, cacheSize));
        OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);

        OkHttpDownloader downloader = new OkHttpDownloader(cacheDir, cacheSize);
        return okHttpDownloader;
    }

    private static File createDefaultCacheDir(Context context, String path) {
        File cacheDir = context.getApplicationContext().getExternalCacheDir();
        if (cacheDir == null)
            cacheDir = context.getApplicationContext().getCacheDir();
        File cache = new File(cacheDir, path);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    /**
     * Calculates bonded min max cache size. Min value is {@link #MIN_DISK_CACHE_SIZE}
     *
     * @param dir cache dir
     * @return disk space in bytes
     */

    private static long calculateDiskCacheSize(File dir) {
        long size = Math.min(calculateAvailableCacheSize(dir), MAX_DISK_CACHE_SIZE);
        return Math.max(size, MIN_DISK_CACHE_SIZE);
    }

    /**
     * Calculates minimum of available or total fraction of disk space
     *
     * @param dir
     * @return space in bytes
     */
    @SuppressLint("NewApi")
    private static long calculateAvailableCacheSize(File dir) {
        long size = 0;
        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            int sdkInt = Build.VERSION.SDK_INT;
            long totalBytes;
            long availableBytes;
            if (sdkInt < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                int blockSize = statFs.getBlockSize();
                availableBytes = ((long) statFs.getAvailableBlocks()) * blockSize;
                totalBytes = ((long) statFs.getBlockCount()) * blockSize;
            } else {
                availableBytes = statFs.getAvailableBytes();
                totalBytes = statFs.getTotalBytes();
            }
            // Target at least 90% of available or 25% of total space
            size = (long) Math.min(availableBytes * MAX_AVAILABLE_SPACE_USE_FRACTION, totalBytes * MAX_TOTAL_SPACE_USE_FRACTION);
        } catch (IllegalArgumentException ignored) {
            // ignored
        }
        return size;
    }
}