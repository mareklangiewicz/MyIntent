package pl.mareklangiewicz.myutils;

import android.support.annotation.Nullable;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.List;

import retrofit.Call;
import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Marek Langiewicz on 07.12.15.
 */
public final class MyHttp {

    /**
     * See MyCommands.V and VV
     */
    private static final boolean V = true;
    private static final boolean VV = false;

    private MyHttp() {
        throw new AssertionError("MyHttp class is noninstantiable.");
    }

    public static final class GitHub {

        public static final String URL = "https://api.github.com";

        public static class Contributor {
            public String login;
            public int contributions;
        }

        public interface Service {
            @GET("/repos/{owner}/{repo}/contributors") Call<List<Contributor>> contributors(@Path("owner") String owner, @Path("repo") String repo);
        }

        private static final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        public static Service create() {
            return retrofit.create(Service.class);
        }

    }


    public static final class OpenWeatherMap {

        public static final String URL = "http://api.openweathermap.org";

        public static class Clouds {
            public int all;
        }

        public static class Coord {
            public float lat;
            public float lon;
        }

        public static class City {
            public long id;
            public String name;
            public Coord coord;
            public String country;
            public long population;
        }

        public static class Wind {
            public float speed;
            public float deg;
        }

        public static class Sys {
            public float message;
            public String country;
            public long sunrise;
            public long sunset;
        }

        public static class Weather {
            public int id;
            public String icon;
            public String description;
            public String main;
        }

        public static class Main {
            public int humidity;
            public float pressure;
            public float temp_max;
            public float sea_level;
            public float temp_min;
            public float temp;
            public float grnd_level;
        }

        public static class Temp {
            public float min;
            public float max;
            public float day;
            public float night;
            public float eve;
            public float morn;
        }

        public static class Forecast {
            public long id;
            public long dt;
            public Clouds clouds;
            public Coord coord;
            public Wind wind;
            public int cod;
            public Sys sys;
            public String name;
            public String base;
            public Weather[] weather;
            public Main main;
        }

        public static class DailyForecast {
            public long dt;
            public Temp temp;
            public float pressure;
            public int humidity;
            public Weather[] weather;
            public float speed;
            public float deg;
            public int clouds;
            public float rain;
            public float snow;
        }

        public static class Forecasts {
            public City city;
            public String cod;
            public String message;
            public long cnt;
            public Forecast[] list;
        }

        public static class DailyForecasts {
            public City city;
            public String cod;
            public String message;
            public long cnt;
            public DailyForecast[] list;
        }

        /**
         * Id list for cities are here http://bulk.openweathermap.org/sample/
         * Default units are Kelvin, you can change it to "metric" (Celsius), or "imperial" (Fahrenheit)
         */
        public interface Service {

            @GET("/data/2.5/weather") Call<Forecast> getWeatherByLocation(
                    @Query("appid") String appid,
                    @Query("lat") float lat,
                    @Query("lon") float lon,
                    @Query("units") @Nullable String units
            );

            @GET("/data/2.5/weather") Call<Forecast> getWeatherByCity(
                    @Query("appid") String appid,
                    @Query("q") String city,
                    @Query("units") @Nullable String units
            );

            @GET("/data/2.5/weather") Call<Forecast> getWeatherById(
                    @Query("appid") String appid,
                    @Query("id") long id,
                    @Query("units") @Nullable String units
            );

            @GET("/data/2.5/forecast") Call<Forecasts> getForecastByLocation(
                    @Query("appid") String appid,
                    @Query("lat") float lat,
                    @Query("lon") float lon,
                    @Query("cnt") long cnt,
                    @Query("units") @Nullable String units
            );

            @GET("/data/2.5/forecast") Call<Forecasts> getForecastByCity(
                    @Query("appid") String appid,
                    @Query("q") String city,
                    @Query("cnt") long cnt,
                    @Query("units") @Nullable String units
            );

            @GET("/data/2.5/forecast") Call<Forecasts> getForecastById(
                    @Query("appid") String appid,
                    @Query("id") long id,
                    @Query("cnt") long cnt,
                    @Query("units") @Nullable String units
            );

            @GET("/data/2.5/forecast/daily") Call<DailyForecasts> getDailyForecastByLocation(
                    @Query("appid") String appid,
                    @Query("lat") float lat,
                    @Query("lon") float lon,
                    @Query("cnt") long cnt,
                    @Query("units") @Nullable String units
            );

            @GET("/data/2.5/forecast/daily") Call<DailyForecasts> getDailyForecastByCity(
                    @Query("appid") String appid,
                    @Query("q") String city,
                    @Query("cnt") long cnt,
                    @Query("units") @Nullable String units
            );

            @GET("/data/2.5/forecast/daily") Call<DailyForecasts> getDailyForecastById(
                    @Query("appid") String appid,
                    @Query("id") long id,
                    @Query("cnt") long cnt,
                    @Query("units") @Nullable String units
            );
        }

        private static final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        public static Service create() {

            if(VV) {
                OkHttpClient client = new OkHttpClient();
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                client.interceptors().add(interceptor);

                Retrofit loggingretrofit = new Retrofit.Builder()
                        .baseUrl(URL)
                        .client(client)
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build();

                return loggingretrofit.create(Service.class);
            }

            return retrofit.create(Service.class);
        }
    }
}
