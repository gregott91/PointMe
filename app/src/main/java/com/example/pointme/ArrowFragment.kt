package com.example.pointme

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.LocationManager
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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Database
import com.example.pointme.helpers.angleFromCoordinate
import com.example.pointme.helpers.distanceFromCoordinates
import com.example.pointme.helpers.getShortDirectionFromAngle
import com.example.pointme.helpers.getUserReadableDistance
import com.example.pointme.listeners.GpsLocationListener
import com.example.pointme.listeners.SensorListener
import com.example.pointme.managers.*
import com.example.pointme.models.Coordinate
import com.example.pointme.repositories.ActivityCompatRepository
import com.example.pointme.repositories.PositionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ArrowFragment : Fragment() {
    private var image: ImageView? = null
    private var destinationHeading: TextView? = null
    private var distanceHeading: TextView? = null
    private var directionHeading: TextView? = null

    private lateinit var mSensorManager: SensorManager
    private lateinit var mLocationManager: LocationManager

    private var prevDegree: Float = 0f

    private var mPermissionManager: PermissionManager? = null
    private var mPositionManager: PositionManager? = null
    private var mNavigationOperationManager: NavigationOperationManager? = null
    private var mNavigationStartManager: NavigationStartManager? = null
    private var mSensorListener: SensorListener? = null
    private var mLocationListener: GpsLocationListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arrow, container, false)
    }

    override fun onResume() {
        super.onResume()

        initViewElements()
        initSystemServices()
        initManagers()

        setupHyperlink()

        runBlocking {
            withContext(Dispatchers.IO) {
                val nav = mNavigationStartManager!!.getActiveNavigation();

                val destLat = nav.latitude
                val destLon = nav.longitude
                mPositionManager?.setDestinationCoordinates(Coordinate(destLat, destLon))

                val destName = nav.place_name
                destinationHeading!!.text = String.format(resources.getString(R.string.heading_destination), destName)
            }
        }

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(
            mSensorListener,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME)

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        val hasAllPermissions = mPermissionManager!!.requestNeededPermissions(permissions, GPS_PERMISSION_CODE, activity!!)
        if (hasAllPermissions) {
            requestLocationUpdates()
        }
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
        mSensorManager.unregisterListener(mSensorListener)
    }

    private fun setupHyperlink() {
        val linkTextView: TextView = view!!.findViewById(R.id.footer_attribution)
        linkTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(){
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 250, 0.5f, mLocationListener)

        val location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        mPositionManager?.setCurrentCoordinates(Coordinate(location.latitude, location.longitude))
    }

    private fun initManagers(){
        mPermissionManager = PermissionManager(ActivityCompatRepository())
        mPositionManager = PositionManager(PositionRepository())
        mNavigationOperationManager = NavigationOperationManager(
            DatabaseManager().getDatabase(activity!!.applicationContext).navigationOperationRepository())
        mNavigationStartManager = NavigationStartManager(
            DatabaseManager().getDatabase(activity!!.applicationContext).navigationStartRepository())

        mSensorListener = SensorListener(mPositionManager!!, { rotateImage() })
        mLocationListener = GpsLocationListener(mPositionManager!!, { rotateImage() })
    }

    fun initViewElements() {
        image = view!!.findViewById(R.id.pointer_arrow) as ImageView
        destinationHeading = view!!.findViewById(R.id.heading_destination) as TextView
        distanceHeading = view!!.findViewById(R.id.heading_distance) as TextView
        directionHeading = view!!.findViewById(R.id.heading_direction) as TextView

        val button = activity!!.findViewById(R.id.arrival_button) as Button
        button.setOnClickListener {
            findNavController().navigate(R.id.action_arrow_to_location)
        }
    }

    private fun initSystemServices() {
        mSensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mLocationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun rotateImage() {
        val currentLocation = mPositionManager!!.getCurrentLocation()
        val curDegreeSnapshot: Float? = currentLocation.heading
        val curLatSnapshot: Double? = currentLocation.coordinate?.latitude
        val curLonSnapshot: Double? = currentLocation.coordinate?.longitude

        if (curDegreeSnapshot == null || curLatSnapshot == null || curLonSnapshot == null) {
            return
        }

        val destCoordinates = mPositionManager!!.getDestinationCoordinates()!!

        val distance = distanceFromCoordinates(
            curLatSnapshot,
            curLonSnapshot,
            destCoordinates.latitude,
            destCoordinates.longitude
        )
        val angle = angleFromCoordinate(
            curLatSnapshot,
            curLonSnapshot,
            destCoordinates.latitude,
            destCoordinates.longitude
        )

        val (finalDistance, units) = getUserReadableDistance(distance)

        try {
            distanceHeading!!.text = String.format(resources.getString(R.string.heading_distance), finalDistance, units)
            directionHeading!!.text = String.format(resources.getString(R.string.heading_direction), getShortDirectionFromAngle(angle))
        } catch (ex: IllegalStateException) {
            // todo log this
            return
        }
        // create a rotation animation (reverse turn degree degrees)
        val angleToPoint = (angle + curDegreeSnapshot).toFloat()
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