package org.covidwatch.android.data

//TODO: Add logic for download uris
class UriManager(private val serverEndpoint: String) {

    //TODO: Check if we going to use separate urls for different regions
    fun uploadUris(regions: List<String>) = listOf(serverEndpoint)
}