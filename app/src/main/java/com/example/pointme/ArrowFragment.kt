package com.example.pointme

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pointme.logic.CoroutineRunner
import com.example.pointme.logic.NavigationInitializer
import com.example.pointme.logic.PermissionManager
import com.example.pointme.logic.PositionManager
import com.example.pointme.logic.managers.NavigationOperationManager
import com.example.pointme.logic.managers.NavigationRequestManager
import com.example.pointme.logic.settings.DistancePreferenceManager
import com.example.pointme.models.Coordinate
import com.example.pointme.models.dtos.NavigationRequestCoordinate
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.platform.listeners.GpsLocationListener
import com.example.pointme.platform.listeners.SensorListener
import com.example.pointme.utility.helpers.getDirectionInfo
import com.example.pointme.utility.helpers.getDistanceInfo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArrowFragment : Fragment() {
    private var image: ImageView? = null
    private var destinationHeading: TextView? = null
    private var distanceHeading: TextView? = null
    private var directionHeading: TextView? = null

    // todo wrap these in a manager
    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager

    private var prevDegree: Float = 0f

    @Inject lateinit var permissionManager: PermissionManager
    @Inject lateinit var positionManager: PositionManager
    @Inject lateinit var navigationOperationManager: NavigationOperationManager
    @Inject lateinit var distancePreferenceManager: DistancePreferenceManager
    @Inject lateinit var coroutineRunner: CoroutineRunner
    @Inject lateinit var navigationInitializer: NavigationInitializer
    @Inject lateinit var requestManager: NavigationRequestManager

    private lateinit var sensorListener: LocalSensorListener
    private lateinit var navigationOperation: NavigationOperation
    private lateinit var destination: NavigationRequestCoordinate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arrow, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        initViewElements()
        initSystemServices()
        initArrivalButton()
        setupHyperlink()

        coroutineRunner.run {
            destination = requestManager.getActiveNavigation()

            activity!!.runOnUiThread({
                destinationHeading!!.text =
                    String.format(resources.getString(R.string.heading_destination), destination.placeName)
            })
        }

        var currentLocation: Location? = null
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        val hasAllPermissions = permissionManager.requestNeededPermissions(permissions, GPS_PERMISSION_CODE, activity!!)
        if (hasAllPermissions) {
            currentLocation = requestLocationUpdates()
            initializeNavigation(currentLocation)
        }

        // for the system's orientation sensor registered listeners
        sensorListener = LocalSensorListener()
        sensorManager.registerListener(
            sensorListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Checking whether user granted the permission or not.
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            var currentLocation: Location = requestLocationUpdates()
            initializeNavigation(currentLocation)
        }
    }

    override fun onPause() {
        super.onPause()

        // to stop the listener and save battery
        sensorManager.unregisterListener(sensorListener)
    }

    private fun setupHyperlink() {
        val linkTextView: TextView = view!!.findViewById(R.id.footer_attribution)
        linkTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(): Location {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            250,
            0.5f,
            LocalGpsLocationListener()
        )

        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        positionManager.setCurrentCoordinates(Coordinate(location.latitude, location.longitude))

        return location
    }

    private fun initializeNavigation(currentLocation: Location) {
        coroutineRunner.run {
            navigationOperation = navigationInitializer.initializeNavigation(destination, currentLocation)
        }
    }

    private fun initViewElements() {
        image = view!!.findViewById(R.id.pointer_arrow) as ImageView
        destinationHeading = view!!.findViewById(R.id.heading_destination) as TextView
        distanceHeading = view!!.findViewById(R.id.heading_distance) as TextView
        directionHeading = view!!.findViewById(R.id.heading_direction) as TextView
    }

    private fun initArrivalButton() {
        val button = activity!!.findViewById(R.id.arrival_button) as Button
        button.setOnClickListener {
            coroutineRunner.run {
                navigationOperationManager.deactivate(navigationOperation)
            }

            findNavController().navigate(R.id.action_arrow_to_location)
        }
    }

    private fun initSystemServices() {
        // todo wrap these dudes
        sensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun rotateImage() {
        val currentLocation = positionManager.getCurrentLocation()

        if (currentLocation.coordinate == null) {
            return
        }

        val destinationCoordinate = Coordinate(destination.latitude, destination.longitude)

        setDistanceHeading(currentLocation, destinationCoordinate)
        setDirectionHeading(currentLocation, destinationCoordinate)
    }

    private fun setDistanceHeading(currentLocation: com.example.pointme.models.Location, destination: Coordinate) {
        val directionInfo = getDistanceInfo(
            currentLocation.coordinate!!,
            destination,
            distancePreferenceManager.getDistancePreference(activity!!.applicationContext))

        distanceHeading!!.text = String.format(resources.getString(R.string.heading_distance), directionInfo.distance, directionInfo.units)
    }

    private fun setDirectionHeading(currentLocation: com.example.pointme.models.Location, destination: Coordinate) {
        val curDegreeSnapshot: Float = currentLocation.heading ?: return

        val directionInfo = getDirectionInfo(
            currentLocation.coordinate!!,
            destination)

        directionHeading!!.text = String.format(resources.getString(R.string.heading_direction), directionInfo.direction)

        // create a rotation animation (reverse turn degree degrees)
        val angleToPoint = (directionInfo.angle + curDegreeSnapshot).toFloat()
        val ra = RotateAnimation(
            prevDegree,
            angleToPoint,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        prevDegree = angleToPoint

        ra.duration = 210
        ra.fillAfter = true

        image!!.startAnimation(ra)
    }

    private inner class LocalGpsLocationListener : GpsLocationListener(positionManager) {
        override fun coordinatesChanged() {
            rotateImage()
        }
    }

    private inner class LocalSensorListener : SensorListener(positionManager) {
        override fun degreeChanged() {
            rotateImage()
        }
    }
}