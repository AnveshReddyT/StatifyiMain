package statifyi.com.statifyi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
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

class GsonDateDeSerializer implements JsonDeserializer<Date> {

    private SimpleDateFormat format1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    private SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss");

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String j = json.getAsJsonPrimitive().getAsString();
            Log.d("STAT", j + " ==== ");
            return parseDate(j);
        } catch (ParseException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    private Date parseDate(String dateString) throws ParseException {
        if (dateString != null && dateString.trim().length() > 0) {
            try {
                return format1.parse(dateString);
            } catch (ParseException pe) {
                return format2.parse(dateString);
            }
        } else {
            return null;
        }
    }

}
