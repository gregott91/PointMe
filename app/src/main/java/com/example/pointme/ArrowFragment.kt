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
import com.example.pointme.platform.listeners.GpsLocationListener
import com.example.pointme.platform.listeners.SensorListener
import com.example.pointme.logic.*
import com.example.pointme.models.Coordinate
import com.example.pointme.data.repositories.ActivityCompatRepository
import com.example.pointme.data.repositories.PositionRepository
import com.example.pointme.logic.managers.DatabaseManager
import com.example.pointme.logic.managers.NavigationOperationManager
import com.example.pointme.logic.managers.NavigationRequestManager
import com.example.pointme.logic.settings.DistancePreferenceManager
import com.example.pointme.logic.settings.PreferenceProxy
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.utility.helpers.*

class ArrowFragment : Fragment() {
    private var image: ImageView? = null
    private var destinationHeading: TextView? = null
    private var distanceHeading: TextView? = null
    private var directionHeading: TextView? = null

    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager

    private var prevDegree: Float = 0f

    private lateinit var permissionManager: PermissionManager
    private lateinit var positionManager: PositionManager
    private lateinit var navigationOperationManager: NavigationOperationManager
    private lateinit var navigationRequestManager: NavigationRequestManager
    private lateinit var sensorListener: SensorListener
    private lateinit var locationListener: GpsLocationListener
    private lateinit var navigationInitializer: NavigationInitializer
    private lateinit var distancePreferenceManager: DistancePreferenceManager
    private var coroutineRunner: CoroutineRunner = CoroutineRunner()

    private lateinit var navigationOperation: NavigationOperation

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
        initManagers()
        initArrivalButton()

        setupHyperlink()

        var currentLocation: Location? = null
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        val hasAllPermissions = permissionManager.requestNeededPermissions(permissions, GPS_PERMISSION_CODE, activity!!)
        if (hasAllPermissions) {
            currentLocation = requestLocationUpdates()
        }

        coroutineRunner.run {
            val (request, operation) = navigationInitializer.initializeNavigation(currentLocation!!)
            destinationHeading!!.text = String.format(resources.getString(R.string.heading_destination), request.placeName)

            navigationOperation = operation
        }

        // for the system's orientation sensor registered listeners
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
            requestLocationUpdates()
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
            locationListener
        )

        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        positionManager.setCurrentCoordinates(Coordinate(location.latitude, location.longitude))

        return location
    }

    private fun initManagers(){
        val database = DatabaseManager().getDatabase(activity!!.applicationContext)

        permissionManager = PermissionManager(ActivityCompatRepository())
        positionManager = PositionManager(PositionRepository())
        navigationOperationManager =
            NavigationOperationManager(database.navigationOperationRepository())
        navigationRequestManager =
            NavigationRequestManager(database.navigationStartRepository(),
                database.coordinateEntityRepository())

        navigationInitializer = NavigationInitializer(
            navigationOperationManager,
            navigationRequestManager,
            positionManager,
            database.coordinateEntityRepository())

        distancePreferenceManager = DistancePreferenceManager(PreferenceProxy())

        sensorListener = SensorListener(positionManager) { rotateImage() }
        locationListener = GpsLocationListener(positionManager) { rotateImage() }
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
        sensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun rotateImage() {
        val currentLocation = positionManager.getCurrentLocation()
        val curDegreeSnapshot: Float? = currentLocation.heading
        val curLatSnapshot: Double? = currentLocation.coordinate?.latitude
        val curLonSnapshot: Double? = currentLocation.coordinate?.longitude

        if (curDegreeSnapshot == null || curLatSnapshot == null || curLonSnapshot == null) {
            return
        }

        val destCoordinates = positionManager.getDestinationCoordinates()!!
        val directionInfo = getDirectionInfo(
            currentLocation.coordinate!!,
            destCoordinates,
            distancePreferenceManager.getDistancePreference(activity!!.applicationContext))

        try {
            distanceHeading!!.text = String.format(resources.getString(R.string.heading_distance), directionInfo.distance, directionInfo.units)
            directionHeading!!.text = String.format(resources.getString(R.string.heading_direction), directionInfo.direction)
        } catch (ex: IllegalStateException) {
            // todo log this
            return
        }

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
}