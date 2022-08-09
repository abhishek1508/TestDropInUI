package com.mapbox.navigation.testapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.testapplication.databinding.ActivityMainBinding
import com.mapbox.navigation.ui.app.internal.SharedApp
import com.mapbox.navigation.ui.app.internal.navigation.NavigationState
import com.mapbox.navigation.ui.app.internal.navigation.NavigationStateAction

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
class MainActivity : AppCompatActivity() {

    private val origin = Point.fromLngLat(88.410607, 22.606913)
    private val destination = Point.fromLngLat(88.407131, 22.602317)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapboxNavigation = MapboxNavigationApp.current()!!
        // Once you are ready with your search results
        binding.startRoutePreview.setOnClickListener {
            requestRoutes(mapboxNavigation)
        }
    }


    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    private fun requestRoutes(mapboxNavigation: MapboxNavigation) {
        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .applyLanguageAndVoiceUnitOptions(this.applicationContext)
                .coordinatesList(listOf(origin, destination))
                .alternatives(true)
                .build(),

            object : NavigationRouterCallback {
                override fun onRoutesReady(
                    routes: List<NavigationRoute>,
                    routerOrigin: RouterOrigin
                ) {
                    binding.navigationView.api.setPreviewRoutes(routes)
                    SharedApp.store.dispatch(NavigationStateAction.Update(NavigationState.RoutePreview))
                    binding.startNavigation.setOnClickListener {
                        binding.navigationView.api.setRoutes(routes)
                        binding.navigationView.api.enableReplaySession()
                        SharedApp.store.dispatch(NavigationStateAction.Update(NavigationState.ActiveNavigation))
                    }
                }
                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {


                }


                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {

                }
            }

        )

    }
}