package studio.loophole.android_things_kotlin

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import kotlinx.android.synthetic.main.activity_main.*

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
    private lateinit var capBtn: Button

    private lateinit var mTextureView: TextureView
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
        setContentView(R.layout.main)

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )

        if (null == savedInstanceState) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, CustomCamera.getInstance())
                    .commit()
        }
       /* //more of the views
        capBtn = findViewById(R.id.capBtn)
        MotionImageView = findViewById(R.id.image_picture)

//        mTextureView.surfaceTextureListener(textureListener)
        texture.surfaceTextureListener = textureListener
        //run the start func
        start()*/

    }



   /* private fun setup() {
        camera = CustomCamera.getInstance()
        camera?.initializeCamera(this@MainActivity, Handler(), firstImageListener)
    }*/

    //prints picture on screen
    private val firstImageListener = object : CustomCamera.ImageCapturedListener {
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
//        setup()

        val capture = findViewById<View>(R.id.capBtn) as Button
        capture.setOnClickListener {
            Log.d(TAG, "capture start");
            camera?.takePicture()
        }
      /*  //this will do stuff if we see motion
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
        })*/
    }


    fun setdown(){

    }

    // this runs on shutdown
    override fun onDestroy() {
        setdown()
        super.onDestroy()
    }

    companion object {

        internal val TAG = MainActivity::class.java.simpleName

    }

}
