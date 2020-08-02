package com.ottsandbox.pointme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ottsandbox.pointme.logic.CoroutineRunner
import com.ottsandbox.pointme.logic.NavigationInitializer
import com.ottsandbox.pointme.logic.managers.NavigationOperationManager
import com.ottsandbox.pointme.logic.managers.NavigationRequestManager
import com.ottsandbox.pointme.logic.managers.NotificationManager
import com.ottsandbox.pointme.logic.managers.PermissionManager
import com.ottsandbox.pointme.logic.settings.DistancePreferenceManager
import com.ottsandbox.pointme.logic.settings.PreferenceManager
import com.ottsandbox.pointme.models.ChannelType
import com.ottsandbox.pointme.models.Coordinate
import com.ottsandbox.pointme.models.NotificationType
import com.ottsandbox.pointme.models.dtos.NavigationRequestCoordinate
import com.ottsandbox.pointme.models.entities.NavigationOperation
import com.ottsandbox.pointme.platform.LocationManagerProxy
import com.ottsandbox.pointme.platform.MessageDisplayer
import com.ottsandbox.pointme.platform.OrientationSensor
import com.ottsandbox.pointme.platform.listeners.GpsLocationListener
import com.ottsandbox.pointme.platform.listeners.SensorListener
import com.ottsandbox.pointme.utility.GPS_PERMISSION_CODE
import com.ottsandbox.pointme.utility.helpers.getDirectionInfo
import com.ottsandbox.pointme.utility.helpers.getDistanceInfo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class ArrowFragment : Fragment() {
    private var image: ImageView? = null
    private var destinationHeading: TextView? = null
    private var distanceHeading: TextView? = null
    private var directionHeading: TextView? = null

    private var sensorListener = LocalSensorListener()
    private var locationListener = LocalGpsLocationListener()

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
    @Inject lateinit var messageDisplayer: MessageDisplayer
    @Inject lateinit var preferenceManager: PreferenceManager
    @Inject lateinit var notificationManager: NotificationManager

    private lateinit var navigationOperation: NavigationOperation
    private lateinit var destination: NavigationRequestCoordinate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        })

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arrow, container, false)
    }

    override fun onStart() {
        super.onStart()

        coroutineRunner.run {
            destination = requestManager.getActiveNavigation()

            coroutineRunner.onUiThread(Runnable {
                val hasAllPermissions = requestPermissions()

                if (hasAllPermissions) {
                    initializeNavigation()
                }
            })
        }
    }

    private fun requestPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return permissionManager.requestNeededPermissions(permissions,
            GPS_PERMISSION_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        initViewElements()
        configureNotification()
        configureFlags()

        destinationHeading!!.text = String.format(resources.getString(R.string.heading_destination), destination.placeName)
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
        } else {
            messageDisplayer.displayMessageWithAction(
                resources.getString(R.string.navigation_error),
                resources.getString(R.string.navigation_error_action)
            ) {
                requestPermissions()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        locationManagerProxy.removeLocationUpdates(locationListener)
        orientationSensor.unregisterListener(sensorListener)
    }

    private fun configureFlags() {
        if (preferenceManager.keepOn) {
            activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun configureNotification() {
        if (preferenceManager.notify) {
            notificationManager.makeNotification(
                String.format(
                    resources.getString(R.string.heading_destination),
                    destination.placeName
                ),
                "",
                MainActivity::class.java,
                NotificationType.NAVIGATION,
                ChannelType.DEFAULT
            )
        } else {
            removeNotification()
        }
    }

    private fun removeNotification() {
        notificationManager.removeNotification(NotificationType.NAVIGATION)
    }

    private fun initializeNavigation() {
        locationManagerProxy.requestLocationUpdates(locationListener)
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

        val button = activity!!.findViewById(R.id.arrival_button) as Button
        button.setOnClickListener {
            navigateBack()
        }
    }

    private fun navigateBack() {
        removeNotification()

        coroutineRunner.run {
            navigationOperationManager.deactivate(navigationOperation)
        }

        findNavController().navigate(R.id.action_arrow_to_location)
    }

    private fun rotateImage() {
        if (currentCoordinate == null) {
            return
        }

        val destinationCoordinate = Coordinate(destination.latitude, destination.longitude)
        val currentLocation = com.ottsandbox.pointme.models.Location(currentCoordinate, currentDegree)

        setDistanceHeading(currentLocation, destinationCoordinate)
        setDirectionHeading(currentLocation, destinationCoordinate)
    }

    private fun setDistanceHeading(currentLocation: com.ottsandbox.pointme.models.Location, destinationCoordinates: Coordinate) {
        val distanceInfo = getDistanceInfo(
            currentLocation.coordinate!!,
            destinationCoordinates,
            distancePreferenceManager.getDistancePreference())

        val distanceText = String.format(resources.getString(R.string.heading_distance), distanceInfo.distance, distanceInfo.units)

        distanceHeading!!.text = distanceText
    }

    private fun setDirectionHeading(currentLocation: com.ottsandbox.pointme.models.Location, destination: Coordinate) {
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