# App

Display restaurants around the user’s current location on a map
- Use the FourSquare Search API to query for restaurants: https://developer.foursquare.com/docs/api/venues/search
- Load more restaurants when the user pans the map.
- Cache results in-memory.
- Read restaurants from the cache to show results early, but only if the restaurants fit within the user’s current viewport.
- Include a simple restaurant detail page.

## Setup
Add your Foursquare client ID and secret to `local.gradle`. See `local.gradle.example` for details.
Add a res/values/google_maps_api.xml file and insert your Google Maps API key:
```xml
<resources>
    <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve">
        YOUR_KEY_HERE
    </string>
</resources>
```
See this page for more information:
    https://developers.google.com/maps/documentation/android/start#get_an_android_certificate_and_the_google_maps_api_key
