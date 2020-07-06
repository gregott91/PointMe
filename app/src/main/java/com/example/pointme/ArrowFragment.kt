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
import android.util.Log
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
import com.example.pointme.listeners.GpsLocationListener
import com.example.pointme.listeners.SensorListener
import com.example.pointme.managers.PositionManager
import com.example.pointme.models.Coordinate
import com.example.pointme.repositories.PositionRepository
import java.util.*
import kotlin.math.*

class ArrowFragment : Fragment() {
    private var image: ImageView? = null
    private var destinationHeading: TextView? = null
    private var distanceHeading: TextView? = null
    private var directionHeading: TextView? = null

    private lateinit var mSensorManager: SensorManager
    private lateinit var mLocationManager: LocationManager

    private var prevDegree: Float = 0f

    private var mPositionManager = PositionManager(PositionRepository())
    private var mSensorListener = SensorListener(mPositionManager, { rotateImage() })
    private var mLocationListener = GpsLocationListener(mPositionManager, { rotateImage() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arrow, container, false)
    }

    override fun onResume() {
        super.onResume()

        setupHyperlink()

        image = view!!.findViewById(R.id.pointer_arrow) as ImageView
        destinationHeading = view!!.findViewById(R.id.heading_destination) as TextView
        distanceHeading = view!!.findViewById(R.id.heading_distance) as TextView
        directionHeading = view!!.findViewById(R.id.heading_direction) as TextView
        mSensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mLocationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val button = activity!!.findViewById(R.id.arrival_button) as Button
        button.setOnClickListener {
            findNavController().navigate(R.id.action_arrow_to_location)
        }

        val destLat = arguments?.getDouble(EXTRA_LAT)!!
        val destLon = arguments?.getDouble(EXTRA_LNG)!!
        mPositionManager.setDestinationCoordinates(Coordinate(destLat, destLon))

        val destName = arguments?.getString(EXTRA_DEST)!!
        destinationHeading!!.text = String.format(resources.getString(R.string.heading_destination), destName)

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(
            mSensorListener,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME)

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(activity!!, permissions, GPS_PERMISSION_CODE)
        } else {
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
        mPositionManager.setCurrentCoordinates(Coordinate(location.latitude, location.longitude))
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        permissions.forEach { permission ->
            val result = ActivityCompat.checkSelfPermission(activity!!, permission)

            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    private fun rotateImage() {
        val currentLocation = mPositionManager.getCurrentLocation()
        val curDegreeSnapshot: Float? = currentLocation.heading
        val curLatSnapshot: Double? = currentLocation.coordinate?.latitude
        val curLonSnapshot: Double? = currentLocation.coordinate?.longitude
        val destCoordinates = mPositionManager.getDestinationCoordinates()!!

        if (curDegreeSnapshot == null || curLatSnapshot == null || curLonSnapshot == null) {
            return
        }

        val distance = distanceFromCoordinates(curLatSnapshot, curLonSnapshot, destCoordinates.latitude, destCoordinates.longitude)
        val angle = angleFromCoordinate(curLatSnapshot, curLonSnapshot, destCoordinates.latitude, destCoordinates.longitude)
        val angleToPoint = (angle + curDegreeSnapshot).toFloat()

        val finalDistance: String
        val units: String

        if (distance > 5280){
            finalDistance = "%.2f".format(distance / 5280)
            units = "miles"
        } else {
            finalDistance = "%.0f".format(distance)
            units = "feet"
        }

        var direction: String? = null
        when ((angle / 45.0).roundToInt()) {
            0,8 -> direction = "N"
            1 -> direction = "NE"
            2 -> direction = "E"
            3 -> direction = "SE"
            4 -> direction = "S"
            5 -> direction = "SW"
            6 -> direction = "W"
            7 -> direction = "NW"
        }

        try {
            distanceHeading!!.text = String.format(resources.getString(R.string.heading_distance), finalDistance, units)
            directionHeading!!.text = String.format(resources.getString(R.string.heading_direction), direction!!)
        } catch (ex: IllegalStateException) {
            // todo log this
            return
        }
        // create a rotation animation (reverse turn degree degrees)
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

    private fun angleFromCoordinate(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): Double {
        val dLon = endLon - startLon
        val y = sin(dLon) * cos(endLat)
        val x = cos(startLat) * sin(endLat) - (sin(startLat) * cos(endLat) * cos(dLon))
        var brng = atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        brng = 360 - brng
        return brng
    }

    private fun distanceFromCoordinates(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): Double {
        val theta = startLon - endLon
        var dist =
            sin(Math.toRadians(startLat)) * sin(
                Math.toRadians(endLat)
            ) + cos(Math.toRadians(startLat)) * cos(
                Math.toRadians(
                    endLat
                )
            ) * cos(Math.toRadians(theta))
        dist = acos(dist)
        dist = Math.toDegrees(dist)
        dist *= 60 * 1.1515

        return dist * 5280
    }
}