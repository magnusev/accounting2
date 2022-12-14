package com.evensberget.accounting.service.country

import com.evensberget.accounting.common.domain.Country
import com.evensberget.accounting.common.json.JsonUtils
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class CountryService(
    private val repository: CountryRepository
) {

    private val countryCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(1))
        .maximumSize(50)
        .build<String, Country>()

    fun getCountryByISO3166(code: String): Country {
        val country = countryCache.getIfPresent(code)
            ?: repository.getCountryByISO3166(code)
            ?: getFromFile(code)

        countryCache.put(code, country)
        return country
    }

    private fun getFromFile(code: String): Country {

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class CountryFileEntry(
            @JsonProperty("name") val name: String,
            @JsonProperty("region") val region: String,
            @JsonProperty("alpha-2") val alpha2: String,
            @JsonProperty("alpha-3") val alpha3: String
        )

        val countriesStream = this.javaClass.classLoader.getResourceAsStream("countries.json")

        val country = JsonUtils.fromJson(countriesStream, Array<CountryFileEntry>::class.java)
            .first { it.alpha2 == code }

//        val string = File("data/countries.json").readText(Charsets.UTF_8)


        val newCountry = Country(
            id = UUID.randomUUID(),
            name = country.name,
            region = country.region,
            alpha2 = country.alpha2,
            alpha3 = country.alpha3
        )

        repository.insert(newCountry)

        return newCountry

    }
}
