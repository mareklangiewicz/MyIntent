package pl.mareklangiewicz.myutils;

import com.google.common.truth.Expect;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import pl.mareklangiewicz.myutils.myhttp.GHRepository;
import pl.mareklangiewicz.myutils.myhttp.GHService;
import pl.mareklangiewicz.myutils.myhttp.GHUser;
import pl.mareklangiewicz.myutils.myhttp.MyHttpKt;
import pl.mareklangiewicz.myutils.myhttp.OWMDailyForecasts;
import pl.mareklangiewicz.myutils.myhttp.OWMForecast;
import pl.mareklangiewicz.myutils.myhttp.OWMForecasts;
import pl.mareklangiewicz.myutils.myhttp.OWMService;
import retrofit2.Call;
import retrofit2.Response;

import static pl.mareklangiewicz.myutils.MyTextUtilsKt.getStr;


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
        GHService service = MyHttpKt.createGHService();
        Call<GHUser> call = service.getUser("langara");
        Response<GHUser> response = call.execute();
        GHUser body = response.body();
        log.w(getStr(body)); // set breakpoint here to see properties
        call = service.getUser("JakeWharton");
        response = call.execute();
        body = response.body();
        log.w(getStr(body)); // set breakpoint here to see properties
    }


    @Test
    public void testGitHubGetUserAuth() throws Exception {
        GHService service = MyHttpKt.createGHService();
        Call<GHUser> call = service.getUserAuth("Basic some_bad_base64_pass");
        Response<GHUser> response = call.execute();
        GHUser body = response.body();
        log.w(getStr(body)); // set breakpoint here to see properties
    }


    @Test
    public void testGitHubGetUserTFA() throws Exception {
        GHService service = MyHttpKt.createGHService();
        Call<GHUser> call = service.getUserTFA("Basic some_bad_base64_pass", "421164");
        Response<GHUser> response = call.execute();
        GHUser body = response.body();
        log.w(getStr(body)); // set breakpoint here to see properties
    }

    @Test
    public void testGitHubGetUserRepos() throws Exception {
        GHService service = MyHttpKt.createGHService();
        Call<List<GHRepository>> call = service.getUserRepos("langara");
        Response<List<GHRepository>> response = call.execute();
        List<GHRepository> body = response.body();
        log.w(getStr(body)); // set breakpoint here to see properties
        call = service.getUserRepos("JakeWharton");
        response = call.execute();
        body = response.body();
        log.w(getStr(body)); // set breakpoint here to see properties
    }


    @Test
    public void testGitHubGetUserReposAuth() throws Exception {
        GHService service = MyHttpKt.createGHService();
        Call<List<GHRepository>> call = service.getUserReposAuth("Basic some_bad_base64");
        Response<List<GHRepository>> response = call.execute();
        List<GHRepository> body = response.body();
        log.w(getStr(body)); // set breakpoint here to see properties
    }


    @Test
    public void testGitHubGetUserReposTFA() throws Exception {
        GHService service = MyHttpKt.createGHService();
        Call<List<GHRepository>> call = service.getUserReposTFA("Basic some_bad_base64", "197187");
        Response<List<GHRepository>> response = call.execute();
        List<GHRepository> body = response.body();
        log.w(getStr(body)); // set breakpoint here to see properties
    }

    @Test
    public void testOpenWeatherMapGetWeather() throws Exception {

        // Moshi adapter just for logging..
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<OWMForecast> adapter = moshi.adapter(OWMForecast.class);

        OWMService service = MyHttpKt.createOWMService();

        Call<OWMForecast> call = service.getWeatherByLocation("8932d2a1192be84707c381df649a2925", 30.9f, 30.9f, "metric");
        Response<OWMForecast> response = call.execute();
        OWMForecast body = response.body();

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
        JsonAdapter<OWMForecasts> adapter = moshi.adapter(OWMForecasts.class);

        OWMService service = MyHttpKt.createOWMService();

        Call<OWMForecasts> call = service.getForecastByLocation("8932d2a1192be84707c381df649a2925", 50.5f, 50.5f, 3, "metric");
        Response<OWMForecasts> response = call.execute();
        OWMForecasts body = response.body();

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
        JsonAdapter<OWMDailyForecasts> adapter = moshi.adapter(OWMDailyForecasts.class);

        OWMService service = MyHttpKt.createOWMService();

        Call<OWMDailyForecasts> call = service.getDailyForecastByLocation("8932d2a1192be84707c381df649a2925", 50.5f, 50.5f, 3, "metric");
        Response<OWMDailyForecasts> response = call.execute();
        OWMDailyForecasts body = response.body();

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