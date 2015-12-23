package pl.mareklangiewicz.myutils;

import android.support.annotation.Nullable;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.List;

import retrofit.Call;
import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Marek Langiewicz on 07.12.15.
 */
public final class MyHttp {

    /**
     * See MyCommands.V and VV
     */
    @SuppressWarnings("unused")
    private static final boolean V = true;
    private static final boolean VV = true;

    private MyHttp() {
        throw new AssertionError("MyHttp class is noninstantiable.");
    }

    public static final class GitHub {

        public static final String URL = "https://api.github.com";

        @SuppressWarnings("unused")
        public static class Plan {
            public String name; // e.g.: "Medium"
            public Long space;
            public Long private_repos;
            public Long collaborators;
        }

        @SuppressWarnings("unused")
        public static class User {
            public String login;
            public long id;
            public String avatar_url;
            public String gravatar_id; // e.g.: ""
            public String url; // e.g.: "https://api.github.com/users/octocat"
            public String html_url; // e.g.: "https://github.com/octocat"
            public String followers_url; // e.g.: "https://api.github.com/users/octocat/followers"
            public String following_url; // e.g.: "https://api.github.com/users/octocat/following{/other_user}"
            public String gists_url; // e.g.: "https://api.github.com/users/octocat/gists{/gist_id}"
            public String starred_url; // e.g.: "https://api.github.com/users/octocat/starred{/owner}{/repo}"
            public String subscriptions_url; // e.g.: "https://api.github.com/users/octocat/subscriptions"
            public String organizations_url; // e.g.: "https://api.github.com/users/octocat/orgs"
            public String repos_url; // e.g.: "https://api.github.com/users/octocat/repos"
            public String events_url; // e.g.: "https://api.github.com/users/octocat/events{/privacy}"
            public String received_events_url; // e.g.: "https://api.github.com/users/octocat/received_events"
            public String type; // e.g.: "User"
            public Boolean site_admin;
            public String name; // e.g.: "monalisa octocat"
            public String company; // e.g.: "GitHub"
            public String blog; // e.g.: "https://github.com/blog"
            public String location; // e.g.: "San Francisco"
            public String email; // e.g.: "octocat@github.com"
            public Boolean hireable;
            public String bio; // e.g.: "There once was..."
            public Long public_repos;
            public Long public_gists;
            public Long followers;
            public Long following;
            public String created_at; // e.g.: "2008-01-14T04:33:35Z"
            public String updated_at; // e.g.: "2008-01-14T04:33:35Z"
            public Long total_private_repos;
            public Long owned_private_repos;
            public Long private_gists;
            public Long disk_usage;
            public Long collaborators;
            public Plan plan;
        }

        @SuppressWarnings("unused")
        public static class Owner {
            public String login; // e.g.: "octocat"
            public Long id;
            public String avatar_url; // e.g.: "https://github.com/images/error/octocat_happy.gif"
            public String gravatar_id; // e.g.: ""
            public String url; // e.g.: "https://api.github.com/users/octocat"
            public String html_url; // e.g.: "https://github.com/octocat"
            public String followers_url; // e.g.: "https://api.github.com/users/octocat/followers"
            public String following_url; // e.g.: "https://api.github.com/users/octocat/following{/other_user}"
            public String gists_url; // e.g.: "https://api.github.com/users/octocat/gists{/gist_id}"
            public String starred_url; // e.g.: "https://api.github.com/users/octocat/starred{/owner}{/repo}"
            public String subscriptions_url; // e.g.: "https://api.github.com/users/octocat/subscriptions"
            public String organizations_url; // e.g.: "https://api.github.com/users/octocat/orgs"
            public String repos_url; // e.g.: "https://api.github.com/users/octocat/repos"
            public String events_url; // e.g.: "https://api.github.com/users/octocat/events{/privacy}"
            public String received_events_url; // e.g.: "https://api.github.com/users/octocat/received_events"
            public String type; // e.g.: "User"
            public Boolean site_admin;

        }

        @SuppressWarnings("unused")
        public static class Permissions {
            public Boolean admin;
            public Boolean push;
            public Boolean pull;
        }

        @SuppressWarnings("unused")
        public static class Repository {
            public Long id;
            Owner owner;
            public String name; // e.g.: "Hello-World"
            public String full_name; // e.g.: "octocat/Hello-World"
            public String description; // e.g.: "This your first repo!"
            // public Boolean private; FIXME: private is a java keyword too.. check in moshi what can we do..
            public Boolean fork;
            public String url; // e.g.: "https://api.github.com/repos/octocat/Hello-World"
            public String html_url; // e.g.: "https://github.com/octocat/Hello-World"
            public String archive_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/{archive_format}{/ref}"
            public String assignees_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/assignees{/user}"
            public String blobs_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/git/blobs{/sha}"
            public String branches_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/branches{/branch}"
            public String clone_url; // e.g.: "https://github.com/octocat/Hello-World.git"
            public String collaborators_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/collaborators{/collaborator}"
            public String comments_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/comments{/number}"
            public String commits_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/commits{/sha}"
            public String compare_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/compare/{base}...{head}"
            public String contents_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/contents/{+path}"
            public String contributors_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/contributors"
            public String downloads_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/downloads"
            public String events_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/events"
            public String forks_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/forks"
            public String git_commits_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/git/commits{/sha}"
            public String git_refs_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/git/refs{/sha}"
            public String git_tags_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/git/tags{/sha}"
            public String git_url; // e.g.: "git:github.com/octocat/Hello-World.git"
            public String hooks_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/hooks"
            public String issue_comment_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/issues/comments{/number}"
            public String issue_events_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/issues/events{/number}"
            public String issues_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/issues{/number}"
            public String keys_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/keys{/key_id}"
            public String labels_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/labels{/name}"
            public String languages_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/languages"
            public String merges_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/merges"
            public String milestones_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/milestones{/number}"
            public String mirror_url; // e.g.: "git:git.example.com/octocat/Hello-World"
            public String notifications_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/notifications{?since, all, participating}"
            public String pulls_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/pulls{/number}"
            public String releases_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/releases{/id}"
            public String ssh_url; // e.g.: "git@github.com:octocat/Hello-World.git"
            public String stargazers_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/stargazers"
            public String statuses_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/statuses/{sha}"
            public String subscribers_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/subscribers"
            public String subscription_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/subscription"
            public String svn_url; // e.g.: "https://svn.github.com/octocat/Hello-World"
            public String tags_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/tags"
            public String teams_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/teams"
            public String trees_url; // e.g.: "http://api.github.com/repos/octocat/Hello-World/git/trees{/sha}"
            public String homepage; // e.g.: "https://github.com"
            public String language; // e.g.: null
            public Long forks_count;
            public Long stargazers_count;
            public Long watchers_count;
            public Long size;
            public String default_branch; // e.g.: "master"
            public Long open_issues_count;
            public Boolean has_issues;
            public Boolean has_wiki;
            public Boolean has_pages;
            public Boolean has_downloads;
            public String pushed_at; // e.g.: "2011-01-26T19:06:43Z"
            public String created_at; // e.g.: "2011-01-26T19:01:12Z"
            public String updated_at; // e.g.: "2011-01-26T19:14:43Z"
            Permissions permissions;
        }

        private static final String ACCEPT = "Accept: application/vnd.github.v3+json";
        private static final String AGENT = "User-Agent: MyHttp";

        public interface Service {

            @Headers({ACCEPT, AGENT})
            @GET("/users/{user}")
            Call<User> getUser(@Path("user") String user);

            @Headers({ACCEPT, AGENT})
            @GET("/user")
            Call<User> getUserAuth(@Header("Authorization") String auth);

            @Headers({ACCEPT, AGENT})
            @GET("/users/{user}/repos")
            Call<List<Repository>> getUserRepos(@Path("user") String user);

            @Headers({ACCEPT, AGENT})
            @GET("/user/repos")
            Call<List<Repository>> getUserReposAuth(@Header("Authorization") String auth);

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


    public static final class OpenWeatherMap {

        public static final String URL = "http://api.openweathermap.org";

        public static class Clouds {
            public int all;
        }

        @SuppressWarnings("unused")
        public static class Coord {
            public float lat;
            public float lon;
        }

        @SuppressWarnings("unused")
        public static class City {
            public long id;
            public String name;
            public Coord coord;
            public String country;
            public long population;
        }

        @SuppressWarnings("unused")
        public static class Wind {
            public float speed;
            public float deg;
        }

        @SuppressWarnings("unused")
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

        @SuppressWarnings("unused")
        public static class Main {
            public int humidity;
            public float pressure;
            public float temp_max;
            public float sea_level;
            public float temp_min;
            public float temp;
            public float grnd_level;
        }

        @SuppressWarnings("unused")
        public static class Temp {
            public float min;
            public float max;
            public float day;
            public float night;
            public float eve;
            public float morn;
        }

        @SuppressWarnings("unused")
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

        @SuppressWarnings("unused")
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

        @SuppressWarnings("unused")
        public static class Forecasts {
            public City city;
            public String cod;
            public String message;
            public long cnt;
            public Forecast[] list;
        }

        @SuppressWarnings("unused")
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
