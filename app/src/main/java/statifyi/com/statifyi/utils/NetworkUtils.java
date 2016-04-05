package statifyi.com.statifyi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import statifyi.com.statifyi.api.RemoteServerAPI;
import statifyi.com.statifyi.api.service.UserAPIService;
import statifyi.com.statifyi.api.service.UserAPIServiceImpl;

/**
 * Created by KT on 5/24/15.
 */
public class NetworkUtils {

    public static final String SERVER_IP = "54.201.38.232";
    private static final String BASE_URL = "http://" + SERVER_IP + ":8080";

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
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    public static HttpLoggingInterceptor provideOkHttpClientLogging() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    public static OkHttpClient provideOkHttpClient(Context mContext) {
        OkHttpClient client = new OkHttpClient();
        client.setCache(provideOkHttpCache(mContext));
        client.interceptors().add(provideOkHttpClientLogging());
        return client;
    }

    public static Retrofit provideRetrofit(Context mContext) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .client(provideOkHttpClient(mContext))
                .build();
    }

    public static RemoteServerAPI provideServerAPI(Context mContext) {
        return provideRetrofit(mContext).create(RemoteServerAPI.class);
    }

    public static UserAPIService provideUserAPIService(Context mContext) {
        return new UserAPIServiceImpl(provideServerAPI(mContext));
    }
}
