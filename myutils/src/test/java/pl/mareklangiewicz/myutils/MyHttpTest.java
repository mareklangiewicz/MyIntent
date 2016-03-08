package pl.mareklangiewicz.myutils;

import com.google.common.truth.Expect;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import pl.mareklangiewicz.myutils.MyHttp.OpenWeatherMap.DailyForecasts;
import pl.mareklangiewicz.myutils.MyHttp.OpenWeatherMap.Forecast;
import pl.mareklangiewicz.myutils.MyHttp.OpenWeatherMap.Forecasts;
import retrofit.Call;
import retrofit.Response;

import static pl.mareklangiewicz.myutils.MyTextUtilsKt.str;

/**
 * Created by Marek Langiewicz on 07.12.15.
 */
public class MyHttpTest {

    private static final IMyLogger log = new MySystemLogger();

    @Rule public final Expect EXPECT = Expect.create();

    @Before
    public void setUp() throws Exception {
        MyCommands.sUT = true;
    }

    @After
    public void tearDown() throws Exception {

    }



    @Test
    public void testGitHubGetUser() throws Exception {
        MyHttp.GitHub.Service service = MyHttp.GitHub.create();
        Call<MyHttp.GitHub.User> call = service.getUser("langara");
        Response<MyHttp.GitHub.User> response = call.execute();
        MyHttp.GitHub.User body = response.body();
        log.w(str(body)); // set breakpoint here to see properties
        call = service.getUser("JakeWharton");
        response = call.execute();
        body = response.body();
        log.w(str(body)); // set breakpoint here to see properties
    }


    @Test
    public void testGitHubGetUserAuth() throws Exception {
        MyHttp.GitHub.Service service = MyHttp.GitHub.create();
        Call<MyHttp.GitHub.User> call = service.getUserAuth("Basic some_bad_base64_pass");
        Response<MyHttp.GitHub.User> response = call.execute();
        MyHttp.GitHub.User body = response.body();
        log.w(str(body)); // set breakpoint here to see properties
    }


    @Test
    public void testGitHubGetUserTFA() throws Exception {
        MyHttp.GitHub.Service service = MyHttp.GitHub.create();
        Call<MyHttp.GitHub.User> call = service.getUserTFA("Basic some_bad_base64_pass", "421164");
        Response<MyHttp.GitHub.User> response = call.execute();
        MyHttp.GitHub.User body = response.body();
        log.w(str(body)); // set breakpoint here to see properties
    }

    @Test
    public void testGitHubGetUserRepos() throws Exception {
        MyHttp.GitHub.Service service = MyHttp.GitHub.create();
        Call<List<MyHttp.GitHub.Repository>> call = service.getUserRepos("langara");
        Response<List<MyHttp.GitHub.Repository>> response = call.execute();
        List<MyHttp.GitHub.Repository> body = response.body();
        log.w(str(body)); // set breakpoint here to see properties
        call = service.getUserRepos("JakeWharton");
        response = call.execute();
        body = response.body();
        log.w(str(body)); // set breakpoint here to see properties
    }


    @Test
    public void testGitHubGetUserReposAuth() throws Exception {
        MyHttp.GitHub.Service service = MyHttp.GitHub.create();
        Call<List<MyHttp.GitHub.Repository>> call = service.getUserReposAuth("Basic some_bad_base64");
        Response<List<MyHttp.GitHub.Repository>> response = call.execute();
        List<MyHttp.GitHub.Repository> body = response.body();
        log.w(str(body)); // set breakpoint here to see properties
    }


    @Test
    public void testGitHubGetUserReposTFA() throws Exception {
        MyHttp.GitHub.Service service = MyHttp.GitHub.create();
        Call<List<MyHttp.GitHub.Repository>> call = service.getUserReposTFA("Basic some_bad_base64", "197187");
        Response<List<MyHttp.GitHub.Repository>> response = call.execute();
        List<MyHttp.GitHub.Repository> body = response.body();
        log.w(str(body)); // set breakpoint here to see properties
    }

    @Test
    public void testOpenWeatherMapGetWeather() throws Exception {

        // Moshi adapter just for logging..
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Forecast> adapter = moshi.adapter(Forecast.class);

        MyHttp.OpenWeatherMap.Service service = MyHttp.OpenWeatherMap.create();

        Call<Forecast> call = service.getWeatherByLocation("8932d2a1192be84707c381df649a2925", 30.9f, 30.9f, "metric");
        Response<Forecast> response = call.execute();
        Forecast body = response.body();

        log.w(adapter.toJson(body));

        call = service.getWeatherByCity("8932d2a1192be84707c381df649a2925", "London", "metric");
        response = call.execute();
        body = response.body();

        log.w(adapter.toJson(body));

        call = service.getWeatherByCity("8932d2a1192be84707c381df649a2925", "Wroclaw", "metric");
        response = call.execute();
        body = response.body();

        log.w(adapter.toJson(body));

        call = service.getWeatherByCity("8932d2a1192be84707c381df649a2925", "Jelcz Laskowice", "metric");
        response = call.execute();
        body = response.body();

        log.w(adapter.toJson(body));

        call = service.getWeatherById("8932d2a1192be84707c381df649a2925", 2172797, "metric");
        response = call.execute();
        body = response.body();

        log.w(adapter.toJson(body));
    }

    @Test
    public void testOpenWeatherMapForecast() throws Exception {

        // Moshi adapter just for logging..
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Forecasts> adapter = moshi.adapter(Forecasts.class);

        MyHttp.OpenWeatherMap.Service service = MyHttp.OpenWeatherMap.create();

        Call<Forecasts> call = service.getForecastByLocation("8932d2a1192be84707c381df649a2925", 50.5f, 50.5f, 3, "metric");
        Response<Forecasts> response = call.execute();
        Forecasts body = response.body();

        log.w(adapter.toJson(body));

        call = service.getForecastByCity("8932d2a1192be84707c381df649a2925", "London", 3, "metric");
        response = call.execute();
        body = response.body();

        log.w(adapter.toJson(body));

        call = service.getForecastByCity("8932d2a1192be84707c381df649a2925", "Wroclaw,pl", 3, "metric");
        response = call.execute();
        body = response.body();

        log.w(adapter.toJson(body));

        call = service.getForecastById("8932d2a1192be84707c381df649a2925", 2172797, 3, "metric");
        response = call.execute();
        body = response.body();

        log.w(adapter.toJson(body));
    }

    @Test
    public void testOpenWeatherMapDailyForecast() throws Exception {

        // Moshi adapter just for logging..
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<DailyForecasts> adapter = moshi.adapter(DailyForecasts.class);

        MyHttp.OpenWeatherMap.Service service = MyHttp.OpenWeatherMap.create();

        Call<DailyForecasts> call = service.getDailyForecastByLocation("8932d2a1192be84707c381df649a2925", 50.5f, 50.5f, 3, "metric");
        Response<DailyForecasts> response = call.execute();
        DailyForecasts body = response.body();

        log.w(adapter.toJson(body));

        call = service.getDailyForecastByCity("8932d2a1192be84707c381df649a2925", "London", 3, "metric");
        response = call.execute();
        body = response.body();

        log.w(adapter.toJson(body));

        call = service.getDailyForecastByCity("8932d2a1192be84707c381df649a2925", "Wroclaw,pl", 3, "metric");
        response = call.execute();
        body = response.body();

        log.w(adapter.toJson(body));

        call = service.getDailyForecastById("8932d2a1192be84707c381df649a2925", 2172797, 3, "metric");
        response = call.execute();
        body = response.body();

        log.w(adapter.toJson(body));
    }
}