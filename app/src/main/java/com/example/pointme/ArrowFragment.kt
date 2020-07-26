package com.example.pointme

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.pointme.logic.managers.PermissionManager
import com.example.pointme.logic.managers.NavigationOperationManager
import com.example.pointme.logic.managers.NavigationRequestManager
import com.example.pointme.logic.settings.DistancePreferenceManager
import com.example.pointme.models.Coordinate
import com.example.pointme.models.dtos.NavigationRequestCoordinate
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.platform.LocationManagerProxy
import com.example.pointme.platform.OrientationSensor
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

    private var prevDegree: Float = 0f
    private var currentDegree: Float = 0f
    private var currentCoordinate: Coordinate? = null

    @Inject lateinit var permissionManager: PermissionManager
    @Inject lateinit var navigationOperationManager: NavigationOperationManager
    @Inject lateinit var distancePreferenceManager: DistancePreferenceManager
    @Inject lateinit var coroutineRunner: CoroutineRunner
    @Inject lateinit var navigationInitializer: NavigationInitializer
    @Inject lateinit var requestManager: NavigationRequestManager
    @Inject lateinit var orientationSensor: OrientationSensor
    @Inject lateinit var locationManagerProxy: LocationManagerProxy

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
        initArrivalButton()

        coroutineRunner.run {
            destination = requestManager.getActiveNavigation()

            coroutineRunner.onUiThread(activity!!, Runnable {
                destinationHeading!!.text =
                    String.format(resources.getString(R.string.heading_destination), destination.placeName)
            })
        }

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        val hasAllPermissions = permissionManager.requestNeededPermissions(permissions, GPS_PERMISSION_CODE, activity!!)
        if (hasAllPermissions) {
            initializeNavigation()
        }

        sensorListener = LocalSensorListener()
        orientationSensor.registerListener(sensorListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Checking whether user granted the permission or not.
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeNavigation()
        }
    }

    override fun onPause() {
        super.onPause()

        // to stop the listener and save battery
        orientationSensor.unregisterListener(sensorListener)
    }

    private fun initializeNavigation() {
        locationManagerProxy.requestLocationUpdates(LocalGpsLocationListener())
        currentCoordinate = locationManagerProxy.getLastKnownCoordinates()

        coroutineRunner.run {
            navigationOperation = navigationInitializer.initializeNavigation(destination, currentCoordinate!!)
        }
    }

    private fun initViewElements() {
        image = view!!.findViewById(R.id.pointer_arrow) as ImageView
        destinationHeading = view!!.findViewById(R.id.heading_destination) as TextView
        distanceHeading = view!!.findViewById(R.id.heading_distance) as TextView
        directionHeading = view!!.findViewById(R.id.heading_direction) as TextView

        val linkTextView: TextView = view!!.findViewById(R.id.footer_attribution)
        linkTextView.movementMethod = LinkMovementMethod.getInstance()
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

    private fun rotateImage() {
        if (currentCoordinate == null) {
            return
        }

        val destinationCoordinate = Coordinate(destination.latitude, destination.longitude)
        val currentLocation = com.example.pointme.models.Location(currentCoordinate, currentDegree)

        setDistanceHeading(currentLocation, destinationCoordinate)
        setDirectionHeading(currentLocation, destinationCoordinate)
    }

    private fun setDistanceHeading(currentLocation: com.example.pointme.models.Location, destination: Coordinate) {
        val distanceInfo = getDistanceInfo(
            currentLocation.coordinate!!,
            destination,
            distancePreferenceManager.getDistancePreference(activity!!.applicationContext))

        distanceHeading!!.text = String.format(resources.getString(R.string.heading_distance), distanceInfo.distance, distanceInfo.units)
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

    private inner class LocalGpsLocationListener : GpsLocationListener() {
        override fun coordinatesChanged(location: Coordinate) {
            currentCoordinate = location
            rotateImage()
        }
    }

    private inner class LocalSensorListener : SensorListener() {
        override fun degreeChanged(currentHeading: Float) {
            currentDegree = currentHeading
            rotateImage()
        }
    }
}