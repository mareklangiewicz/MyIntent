package pl.mareklangiewicz.myutils

import com.squareup.moshi.Moshi
import io.reactivex.Observable
import org.junit.Ignore
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

    @Ignore
    @Test fun testGitHubGetUserCall() {
        val service = GitHub.service
        var call: Call<GitHub.User> = service.getUserCall("langara")
        var response: Response<GitHub.User> = call.execute()
        var body: GitHub.User = response.body()!!
        log.w(body.str) // set breakpoint here to see properties
        call = service.getUserCall("JakeWharton")
        response = call.execute()
        body = response.body()!!
        log.w(body.str) // set breakpoint here to see properties
    }

    // WARNING: You need base64 encoded user name and password to run this test.
    // you can calculate it easily in python:
    // import base64
    // base64.b64encode("someuser:somepassword")
    @Test fun testGitHubGetUserAuthCall() {
        val service = GitHub.service
        val call = service.getUserAuthCall("Basic some_bad_base64_pass")
        val response = call.execute()
        log.w(response.isSuccessful)
        val body = response.body()
        log.w(body.str) // set breakpoint here to see properties
    }

    // WARNING: see test above, and use correct OTP code as a second parameter
    @Test fun testGitHubGetUserTFACall() {
        val service = GitHub.service
        val call = service.getUserTFACall("Basic some_bad_base64_pass", "421164")
        val response = call.execute()
        log.w(response.isSuccessful)
        val body = response.body()
        log.w(body.str) // set breakpoint here to see properties
    }

    @Ignore
    @Test fun testGitHubGetUserReposCall() {
        val service = GitHub.service
        var call: Call<List<GitHub.Repository>> = service.getUserReposCall("langara")
        var response: Response<List<GitHub.Repository>> = call.execute()
        var body: List<GitHub.Repository> = response.body()!!
        log.w(body.str) // set breakpoint here to see properties
        call = service.getUserReposCall("JakeWharton")
        response = call.execute()
        body = response.body()!!
        log.w(body.str) // set breakpoint here to see properties
    }

    // WARNING: You need base64 encoded user name and password to run this test.
    // you can calculate it easily in python:
    // import base64
    // base64.b64encode("someuser:somepassword")
    @Test fun testGitHubGetUserReposAuthCall() {
        val service = GitHub.service
        val call = service.getUserReposAuthCall("Basic some_bad_base64")
        val response = call.execute()
        val body = response.body()
        log.w(body.str) // set breakpoint here to see properties
    }

    // WARNING: see test above, and use correct OTP code as a second parameter
    @Test fun testGitHubGetUserReposTFACall() {
        val service = GitHub.service
        val call = service.getUserReposTFACall("Basic some_bad_base64", "197187")
        val response = call.execute()
        val body = response.body()
        log.w(body.str) // set breakpoint here to see properties
    }
    
    @Test
    @Throws(Exception::class)
    fun testGitHubGetUserObservable() {

        val service = GitHub.service
        var observable: Observable<GitHub.User> = service.getUserObservable("langara")
        observable.lsubscribe(log)

        observable = service.getUserObservable("JakeWharton")
        observable.lsubscribe(log)
    }

    // WARNING: You need base64 encoded user name and password to run this test.
    // you can calculate it easily in python:
    // import base64
    // base64.b64encode("someuser:somepassword")
    @Test
    @Throws(Exception::class)
    fun testGitHubGetUserAuthObservable() {
        val service = GitHub.service
        val observable = service.getUserAuthObservable("Basic some_bad_base64_pass")
        observable.lsubscribe(log)
    }

    // WARNING: see test above, and use correct OTP code as a second parameter
    @Test
    @Throws(Exception::class)
    fun testGitHubGetUserTFAObservable() {
        val service = GitHub.service
        val observable = service.getUserTFAObservable("Basic some_bad_base64_pass", "421164")
        observable.lsubscribe(log)
    }

    @Test
    @Throws(Exception::class)
    fun testGitHubGetUserReposObservable() {
        val service = GitHub.service
        var observable: Observable<List<GitHub.Repository>> = service.getUserReposObservable("langara")
        observable.lsubscribe(log)
        observable = service.getUserReposObservable("JakeWharton")
        observable.lsubscribe(log)
    }


    // WARNING: You need base64 encoded user name and password to run this test.
    // you can calculate it easily in python:
    // import base64
    // base64.b64encode("someuser:somepassword")
    @Test
    @Throws(Exception::class)
    fun testGitHubGetUserReposAuthObservable() {
        val service = GitHub.service
        val observable = service.getUserReposAuthObservable("Basic some_bad_base64")
        observable.lsubscribe(log)
    }


    // WARNING: see test above, and use correct OTP code as a second parameter
    @Test
    @Throws(Exception::class)
    fun testGitHubGetUserReposTFAObservable() {
        val service = GitHub.service
        val observable = service.getUserReposTFAObservable("Basic some_bad_base64", "197187")
        observable.lsubscribe(log)
    }





    @Test fun testOpenWeatherMapGetWeatherCall() {
        // Moshi adapter just for logging..
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(OpenWeatherMap.Forecast::class.java)

        val service = OpenWeatherMap.service

        var call: Call<OpenWeatherMap.Forecast> = service.getWeatherByLocationCall("8932d2a1192be84707c381df649a2925", 30.9f, 30.9f, "metric")
        var response: Response<OpenWeatherMap.Forecast> = call.execute()
        var body: OpenWeatherMap.Forecast? = response.body()

        log.w(adapter.toJson(body))

        call = service.getWeatherByCityCall("8932d2a1192be84707c381df649a2925", "London", "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getWeatherByCityCall("8932d2a1192be84707c381df649a2925", "Wroclaw", "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getWeatherByCityCall("8932d2a1192be84707c381df649a2925", "Jelcz Laskowice", "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getWeatherByIdCall("8932d2a1192be84707c381df649a2925", 2172797, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))
    }

    @Test fun testOpenWeatherMapForecastCall() {

        // Moshi adapter just for logging..
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(OpenWeatherMap.Forecasts::class.java)

        val service = OpenWeatherMap.service

        var call: Call<OpenWeatherMap.Forecasts> = service.getForecastByLocationCall("8932d2a1192be84707c381df649a2925", 50.5f, 50.5f, null, "metric")
        var response: Response<OpenWeatherMap.Forecasts> = call.execute()
        var body: OpenWeatherMap.Forecasts? = response.body()

        log.w(adapter.toJson(body))

        call = service.getForecastByCityCall("8932d2a1192be84707c381df649a2925", "London", 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getForecastByCityCall("8932d2a1192be84707c381df649a2925", "Wroclaw,pl", 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getForecastByIdCall("8932d2a1192be84707c381df649a2925", 2172797, 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))
    }

    @Test fun testOpenWeatherMapDailyForecastCall() {

        // Moshi adapter just for logging..
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(OpenWeatherMap.DailyForecasts::class.java)

        val service = OpenWeatherMap.service

        var call: Call<OpenWeatherMap.DailyForecasts> = service.getDailyForecastByLocationCall("8932d2a1192be84707c381df649a2925", 50.5f, 50.5f, null, "metric")
        var response: Response<OpenWeatherMap.DailyForecasts> = call.execute()
        var body: OpenWeatherMap.DailyForecasts? = response.body()

        log.w(adapter.toJson(body))

        call = service.getDailyForecastByCityCall("8932d2a1192be84707c381df649a2925", "London", 16, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getDailyForecastByCityCall("8932d2a1192be84707c381df649a2925", "Wroclaw,pl", 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))

        call = service.getDailyForecastByIdCall("8932d2a1192be84707c381df649a2925", 2172797, 3, "metric")
        response = call.execute()
        body = response.body()

        log.w(adapter.toJson(body))
    }
}
