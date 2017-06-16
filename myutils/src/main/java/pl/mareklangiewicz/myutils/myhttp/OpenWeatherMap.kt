package pl.mareklangiewicz.myutils.myhttp

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object OpenWeatherMap {

    const val URL = "http://api.openweathermap.org"

    class Clouds {
        /** Cloudiness, % */
        var all: Int = 0
    }

    class Coord {
        /** Latitude */
        var lat: Float = 0f
        /** Longitude */
        var lon: Float = 0f
    }

    class City {
        /** City id as in [http://bulk.openweathermap.org/sample/] */
        var id: Long = 0
        /** City name */
        var name: String? = null
        /** City location */
        var coord: Coord? = null
        /** Country code */
        var country: String? = null
        /** City population */
        var population: Long = 0
    }

    class Wind {
        /** Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour. */
        var speed: Float = 0f
        /** Wind direction, degrees (meteorological) */
        var deg: Float = 0f
    }

    class Sys {
        var message: Float = 0f
        var country: String? = null
        var sunrise: Long = 0
        var sunset: Long = 0
    }

    class Weather {
        /** Weather condition id */
        var id: Int = 0
        /** Weather icon id */
        var icon: String? = null
        /** Weather condition within the group */
        var description: String? = null
        /** Group of weather parameters (Rain, Snow, Extreme, etc.) */
        var main: String? = null
    }

    class Main {
        /** Humidity, % */
        var humidity: Int = 0
        /** Atmospheric pressure on the sea level by default, hPa */
        var pressure: Float = 0f
        /**
         * Maximum temperature at the moment of calculation. This is deviation from 'temp' that is possible
         * for large cities and megalopolises geographically expanded (use these parameter optionally).
         * Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
         */
        var temp_max: Float = 0f
        /** Atmospheric pressure on the sea level, hPa */
        var sea_level: Float = 0f
        /**
         * Minimum temperature at the moment of calculation. This is deviation from 'temp' that is possible
         * for large cities and megalopolises geographically expanded (use these parameter optionally).
         * Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
         */
        var temp_min: Float = 0f
        /** Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit. */
        var temp: Float = 0f
        /** Atmospheric pressure on the ground level, hPa */
        var grnd_level: Float = 0f
    }

    class Temp {
        /** Min daily temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit. */
        var min: Float = 0f
        /** Max daily temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit. */
        var max: Float = 0f
        /** Averaged daily temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit. */
        var day: Float = 0f
        /** Night temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit. */
        var night: Float = 0f
        /** Evening temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit. */
        var eve: Float = 0f
        /** Morning temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit. */
        var morn: Float = 0f
    }

    class Forecast {
        var id: Long = 0
        /** Time of data forecasted, unix, UTC */
        var dt: Long = 0
        /** Cloudiness */
        var clouds: Clouds? = null
        /** Location */
        var coord: Coord? = null
        /** Wind conditions */
        var wind: Wind? = null
        /** Internal parameter */
        var cod: Int = 0
        var sys: Sys? = null
        var name: String? = null
        var base: String? = null
        /** More info weather condition codes */
        var weather: Array<Weather>? = null
        /** Main conditions */
        var main: Main? = null
    }

    class DailyForecast {
        /** Time of data forecasted, unix, UTC */
        var dt: Long = 0
        /** Temperature */
        var temp: Temp? = null
        /** Atmospheric pressure on the sea level, hPa */
        var pressure: Float = 0f
        /** Humidity, % */
        var humidity: Int = 0
        /** More info weather condition codes */
        var weather: Array<Weather>? = null
        /** Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour. */
        var speed: Float = 0f
        /** Wind direction, degrees (meteorological) */
        var deg: Float = 0f
        /** Cloudiness, % */
        var clouds: Int = 0
        /** Rain */
        var rain: Float = 0f
        /** Snow */
        var snow: Float = 0f
    }

    class Forecasts {
        /** City information */
        var city: City? = null
        /** Internal parameter */
        var cod: String? = null
        /** Internal parameter */
        var message: String? = null
        /** Number of forecasts returned in "list" */
        var cnt: Long = 0
        /** Array of forecasts */
        var list: Array<Forecast>? = null
    }

    class DailyForecasts {
        /** City information */
        var city: City? = null
        /** Internal parameter */
        var cod: String? = null
        /** Internal parameter */
        var message: String? = null
        /** Number of daily forecasts returned in "list" */
        var cnt: Long = 0
        /** Array of daily forecasts */
        var list: Array<DailyForecast>? = null
    }

    /**
     * Id list for cities are here [http://bulk.openweathermap.org/sample/]
     */
    interface Service {

        /**
         * @param appid You have to get app id from [http://openweathermap.org/appid]
         * @param lat Latitude
         * @param lon Longitude
         * @param units Default (null) means in Kelvin, "metric" means in Celsius, "imperial" means in Fahrenheit
         */
        @GET("/data/2.5/weather") fun getWeatherByLocationCall(
                @Query("appid") appid: String,
                @Query("lat") lat: Float,
                @Query("lon") lon: Float,
                @Query("units") units: String? = null): Call<Forecast>

        /**
         * @param appid You have to get app id from [http://openweathermap.org/appid]
         * @param city City name + optional country code after comma
         * @param units Default (null) means in Kelvin, "metric" means in Celsius, "imperial" means in Fahrenheit
         */
        @GET("/data/2.5/weather") fun getWeatherByCityCall(
                @Query("appid") appid: String,
                @Query("q") city: String,
                @Query("units") units: String? = null): Call<Forecast>

        /**
         * @param appid You have to get app id from [http://openweathermap.org/appid]
         * @param id City id (this is cheaper than city name) You can get the list from [http://bulk.openweathermap.org/sample/]
         * @param units Default (null) means in Kelvin, "metric" means in Celsius, "imperial" means in Fahrenheit
         */
        @GET("/data/2.5/weather") fun getWeatherByIdCall(
                @Query("appid") appid: String,
                @Query("id") id: Long,
                @Query("units") units: String? = null): Call<Forecast>

        /**
         * Up to five days forecasts with data every 3 hours for given latitude and longitude
         * @param appid You have to get app id from [http://openweathermap.org/appid]
         * @param lat Latitude
         * @param lon Longitude
         * @param cnt Optional maximum number of forecasts in returned "list"
         * @param units Default (null) means in Kelvin, "metric" means in Celsius, "imperial" means in Fahrenheit
         */
        @GET("/data/2.5/forecast") fun getForecastByLocationCall(
                @Query("appid") appid: String,
                @Query("lat") lat: Float,
                @Query("lon") lon: Float,
                @Query("cnt") cnt: Long? = null,
                @Query("units") units: String? = null): Call<Forecasts>

        /**
         * Up to five days forecasts with data every 3 hours for given city name
         * @param appid You have to get app id from [http://openweathermap.org/appid]
         * @param city City name + optional country code after comma
         * @param cnt Optional maximum number of forecasts in returned "list"
         * @param units Default (null) means in Kelvin, "metric" means in Celsius, "imperial" means in Fahrenheit
         */
        @GET("/data/2.5/forecast") fun getForecastByCityCall(
                @Query("appid") appid: String,
                @Query("q") city: String,
                @Query("cnt") cnt: Long? = null,
                @Query("units") units: String? = null): Call<Forecasts>

        /**
         * Up to five days forecasts with data every 3 hours for given city id
         * @param appid You have to get app id from [http://openweathermap.org/appid]
         * @param id City id (this is cheaper than city name) You can get the list from [http://bulk.openweathermap.org/sample/]
         * @param cnt Optional maximum number of forecasts in returned "list"
         * @param units Default (null) means in Kelvin, "metric" means in Celsius, "imperial" means in Fahrenheit
         */
        @GET("/data/2.5/forecast") fun getForecastByIdCall(
                @Query("appid") appid: String,
                @Query("id") id: Long,
                @Query("cnt") cnt: Long? = null,
                @Query("units") units: String? = null): Call<Forecasts>

        /**
         * Up to 16 days daily forecasts for given location
         * @param appid You have to get app id from [http://openweathermap.org/appid]
         * @param lat Latitude
         * @param lon Longitude
         * @param cnt Optional maximum number of daily forecasts in returned "list"
         * @param units Default (null) means in Kelvin, "metric" means in Celsius, "imperial" means in Fahrenheit
         */
        @GET("/data/2.5/forecast/daily") fun getDailyForecastByLocationCall(
                @Query("appid") appid: String,
                @Query("lat") lat: Float,
                @Query("lon") lon: Float,
                @Query("cnt") cnt: Long? = null,
                @Query("units") units: String? = null): Call<DailyForecasts>

        /**
         * Up to 16 days daily forecasts for given city
         * @param appid You have to get app id from [http://openweathermap.org/appid]
         * @param city City name + optional country code after comma
         * @param cnt Optional maximum number of daily forecasts in returned "list"
         * @param units Default (null) means in Kelvin, "metric" means in Celsius, "imperial" means in Fahrenheit
         */
        @GET("/data/2.5/forecast/daily") fun getDailyForecastByCityCall(
                @Query("appid") appid: String,
                @Query("q") city: String,
                @Query("cnt") cnt: Long? = null,
                @Query("units") units: String? = null): Call<DailyForecasts>

        /**
         * Up to 16 days daily forecasts for given city id
         * @param appid You have to get app id from [http://openweathermap.org/appid]
         * @param id City id (this is cheaper than city name) You can get the list from [http://bulk.openweathermap.org/sample/]
         * @param cnt Optional maximum number of daily forecasts in returned "list"
         * @param units Default (null) means in Kelvin, "metric" means in Celsius, "imperial" means in Fahrenheit
         */
        @GET("/data/2.5/forecast/daily") fun getDailyForecastByIdCall(
                @Query("appid") appid: String,
                @Query("id") id: Long,
                @Query("cnt") cnt: Long? = null,
                @Query("units") units: String? = null): Call<DailyForecasts>
    }

//    private val interceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

//    private val logclient = OkHttpClient.Builder().addInterceptor(interceptor).build()

    private val retrofit = Retrofit.Builder()
            .baseUrl(URL)
//            .client(logclient)
            .addConverterFactory(MoshiConverterFactory.create()).build()

    val service = retrofit.create(Service::class.java)!!


}