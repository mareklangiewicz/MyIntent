package pl.mareklangiewicz.myutils

import com.squareup.moshi.Moshi
import org.junit.After
import org.junit.Before
import org.junit.Test
import pl.mareklangiewicz.myutils.myhttp.GitHub
import pl.mareklangiewicz.myutils.myhttp.OpenWeatherMap
import retrofit2.Call
import retrofit2.Response


/**
 * Created by Marek Langiewicz on 07.12.15.
 */
class MyHttpTest {

    private val log = MySystemLogger()

    @Before fun setUp() {
        sUT = true
    }

    @After fun tearDown() { }

    @Test fun testGitHubGetUser() {
        val service = GitHub.service
        var call: Call<GitHub.User> = service.getUser("langara")
        var response: Response<GitHub.User> = call.execute()
        var body: GitHub.User = response.body()
        log.w(body.str) // set breakpoint here to see properties
        call = service.getUser("JakeWharton")
        response = call.execute()
        body = response.body()
        log.w(body.str) // set breakpoint here to see properties
    }

    @Test fun testGitHubGetUserAuth() {
        val service = GitHub.service
        val call = service.getUserAuth("Basic some_bad_base64_pass")
        val response = call.execute()
        log.w(response.isSuccessful)
        val body = response.body()
        log.w(body.str) // set breakpoint here to see properties
    }

    @Test fun testGitHubGetUserTFA() {
        val service = GitHub.service
        val call = service.getUserTFA("Basic some_bad_base64_pass", "421164")
        val response = call.execute()
        log.w(response.isSuccessful)
        val body = response.body()
        log.w(body.str) // set breakpoint here to see properties
    }

    @Test fun testGitHubGetUserRepos() {
        val service = GitHub.service
        var call: Call<List<GitHub.Repository>> = service.getUserRepos("langara")
        var response: Response<List<GitHub.Repository>> = call.execute()
        var body: List<GitHub.Repository> = response.body()
        log.w(body.str) // set breakpoint here to see properties
        call = service.getUserRepos("JakeWharton")
        response = call.execute()
        body = response.body()
        log.w(body.str) // set breakpoint here to see properties
    }

    @Test fun testGitHubGetUserReposAuth() {
        val service = GitHub.service
        val call = service.getUserReposAuth("Basic some_bad_base64")
        val response = call.execute()
        val body = response.body()
        log.w(body.str) // set breakpoint here to see properties
    }

    @Test fun testGitHubGetUserReposTFA() {
        val service = GitHub.service
        val call = service.getUserReposTFA("Basic some_bad_base64", "197187")
        val response = call.execute()
        val body = response.body()
        log.w(body.str) // set breakpoint here to see properties
    }

    @Test fun testOpenWeatherMapGetWeather() {
        // Moshi adapter just for logging..
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(OpenWeatherMap.Forecast::class.java)

        val service = OpenWeatherMap.service

        var call: Call<OpenWeatherMap.Forecast> = service.getWeatherByLocation("8932d2a1192be84707c381df649a2925", 30.9f, 30.9f, "metric")
        var response: Response<OpenWeatherMap.Forecast> = call.execute()
        var body: OpenWeatherMap.Forecast? = response.body()

        log.w(adapter.toJson(body))

        call = service.getWeatherByCity("8932d2a1192be84707c381df649a2925", "London", "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getWeatherByCity("8932d2a1192be84707c381df649a2925", "Wroclaw", "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getWeatherByCity("8932d2a1192be84707c381df649a2925", "Jelcz Laskowice", "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getWeatherById("8932d2a1192be84707c381df649a2925", 2172797, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))
    }

    @Test fun testOpenWeatherMapForecast() {

        // Moshi adapter just for logging..
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(OpenWeatherMap.Forecasts::class.java)

        val service = OpenWeatherMap.service

        var call: Call<OpenWeatherMap.Forecasts> = service.getForecastByLocation("8932d2a1192be84707c381df649a2925", 50.5f, 50.5f, 3, "metric")
        var response: Response<OpenWeatherMap.Forecasts> = call.execute()
        var body: OpenWeatherMap.Forecasts? = response.body()

        log.w(adapter.toJson(body))

        call = service.getForecastByCity("8932d2a1192be84707c381df649a2925", "London", 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getForecastByCity("8932d2a1192be84707c381df649a2925", "Wroclaw,pl", 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getForecastById("8932d2a1192be84707c381df649a2925", 2172797, 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))
    }

    @Test fun testOpenWeatherMapDailyForecast() {

        // Moshi adapter just for logging..
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(OpenWeatherMap.DailyForecasts::class.java)

        val service = OpenWeatherMap.service

        var call: Call<OpenWeatherMap.DailyForecasts> = service.getDailyForecastByLocation("8932d2a1192be84707c381df649a2925", 50.5f, 50.5f, 3, "metric")
        var response: Response<OpenWeatherMap.DailyForecasts> = call.execute()
        var body: OpenWeatherMap.DailyForecasts? = response.body()

        log.w(adapter.toJson(body))

        call = service.getDailyForecastByCity("8932d2a1192be84707c381df649a2925", "London", 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getDailyForecastByCity("8932d2a1192be84707c381df649a2925", "Wroclaw,pl", 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getDailyForecastById("8932d2a1192be84707c381df649a2925", 2172797, 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))
    }
}