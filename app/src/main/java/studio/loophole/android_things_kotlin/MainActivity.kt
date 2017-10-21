package studio.loophole.android_things_kotlin

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManagerService

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */


class MainActivity : Activity() {

    // set gpio pins
    private val MOTION_SENSOR_PIN = "GPIO_35"
    private val LIGHT_PIN = "GPIO_174"
    private val LIGHT_2_PIN = "GPIO_10"

    //some display stuff
    private lateinit var textViewMain : TextView
    private lateinit var MotionImageView : ImageView

    // create vars for all the toys
    private var motionSensorGpio: Gpio? = null
    private var lightGpio: Gpio? = null
    private var light2Gpio: Gpio? = null
    private var camera: CustomCamera? = null

    //saving time with logs
    private val TAG = "MainActivity"

    //this runs when we start the class
    override fun onCreate(savedInstanceState: Bundle?) {
        //no clue whats this does
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //more of the views
        textViewMain = findViewById(R.id.text_main)
        MotionImageView = findViewById(R.id.image_picture)

        //run the start func
        start()

    }

    private fun setup() {
        //setup Camera
        camera = CustomCamera.getInstance()
        camera?.initializeCamera(this, Handler(), imageAvailableListener)

        //setup motion sensor
        motionSensorGpio = PeripheralManagerService().openGpio(MOTION_SENSOR_PIN)
        motionSensorGpio?.setDirection(Gpio.DIRECTION_IN)
        motionSensorGpio?.setActiveType(Gpio.ACTIVE_HIGH)
        motionSensorGpio?.setEdgeTriggerType(Gpio.EDGE_BOTH)

        //Setup light1
        lightGpio = PeripheralManagerService().openGpio(LIGHT_PIN)
        lightGpio?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

        //setup light 2
        light2Gpio = PeripheralManagerService().openGpio(LIGHT_2_PIN)
        light2Gpio?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH)

    }

    //prints picture on screen
    private val imageAvailableListener = object : CustomCamera.ImageCapturedListener {
        override fun onImageCaptured(bitmap: Bitmap) {
            MotionImageView.setImageBitmap(bitmap)
            //motionViewModel.uploadMotionImage(bitmap)
        }
    }

    //toggles light
    fun lightToggle(toggleOn: Boolean){
        lightGpio?.value = !toggleOn
        light2Gpio?.value = toggleOn
    }

    //main loop
    fun start() {

        //setup Pins
        setup()

        //this will do stuff if we see motion
        motionSensorGpio?.registerGpioCallback(object : GpioCallback() {
            override fun onGpioEdge(gpio: Gpio): Boolean {
                if (!gpio.value) {
                    textViewMain.setText("MOTION OMG!")
                    camera?.takePicture()
                } else {
                    textViewMain.setText("all is quiet...")
                }

                lightToggle(gpio.value)
                return true
            }
        })
    }


    fun setdown(){
        motionSensorGpio?.close()
        motionSensorGpio = null;

        lightGpio?.close()
        lightGpio = null

        light2Gpio?.close()
        light2Gpio = null
    }

    // this runs on shutdown
    override fun onDestroy() {
        setdown()
        super.onDestroy()
    }

}
