package com.example.pointme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlin.math.*

class ArrowFragment : Fragment(), SensorEventListener, LocationListener {
    private var image: ImageView? = null
    private var distanceHeading: TextView? = null

    private var mSensorManager: SensorManager? = null
    private var mLocationManager: LocationManager? = null

    private var curDegree: Float? = null
    private var prevDegree: Float = 0f
    private var destLat: Double = 0.0
    private var destLon: Double = 0.0
    private var curLat: Double? = null
    private var curLon: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arrow, container, false)
    }

    override fun onResume() {
        super.onResume()

        image = view!!.findViewById(R.id.imageViewCompass) as ImageView
        distanceHeading = view!!.findViewById(R.id.heading_distance) as TextView
        mSensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mLocationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val extras = activity!!.intent.extras!!
        destLat = extras.getDouble(EXTRA_LAT)
        destLon = extras.getDouble(EXTRA_LNG)

        // for the system's orientation sensor registered listeners
        mSensorManager!!.registerListener(
            this, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )

        var gpsEnabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        // todo check for permission yo
        mLocationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 250, 0.5f, this)
    }

    override fun onPause() {
        super.onPause()

        // to stop the listener and save battery
        mSensorManager!!.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        curDegree = -(event!!.values[0])

        rotateImage()
    }

    override fun onLocationChanged(event: Location) {
        curLat = event.latitude
        curLon = event.longitude

        rotateImage()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onProviderDisabled(p0: String?) {}

    private fun rotateImage() {
        val curDegreeSnapshot: Float? = curDegree
        val curLatSnapshot: Double? = curLat
        val curLonSnapshot: Double? = curLon

        if (curDegreeSnapshot == null || curLatSnapshot == null || curLonSnapshot == null) {
            return
        }

        val distance = distanceFromCoordinates(curLatSnapshot, curLonSnapshot, destLat, destLon)
        val angle = angleFromCoordinate(curLatSnapshot, curLonSnapshot, destLat, destLon)
        val angleToPoint = (angle + curDegreeSnapshot).toFloat()

        val finalDistance: String
        val units: String

        if (distance > 5280){
            finalDistance = "%.2f".format(distance)
            units = "feet"
        } else {
            finalDistance = "%.0f".format(distance / 5280)
            units = "miles"
        }

        distanceHeading!!.text = String.format(resources.getString(R.string.heading_distance), finalDistance, units)

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