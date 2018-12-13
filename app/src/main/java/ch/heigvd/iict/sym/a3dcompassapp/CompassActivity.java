package ch.heigvd.iict.sym.a3dcompassapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    //opengl
    private OpenGLRenderer  opglr           = null;
    private GLSurfaceView   m3DView         = null;

    private  SensorManager mSensorManager;
    private  Sensor mAccelerometer;
    private  Sensor mMagnetic;

    float[] magnet = new float[4];
    float[] accelerometer = new float[4];

    float[] vector = new float[16];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // we need fullscreen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // we initiate the view
        setContentView(R.layout.activity_compass);

        // link to GUI
        this.m3DView = findViewById(R.id.compass_opengl);

        //we create the 3D renderer
        this.opglr = new OpenGLRenderer(getApplicationContext());

        //init opengl surface view
        this.m3DView.setRenderer(this.opglr);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        // Check event to know which sensor changed
        if(event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)){

            accelerometer = event.values;
        }else{
            magnet = event.values;
        }

        // Compute new matrix
        mSensorManager.getRotationMatrix(vector,null,accelerometer,magnet);

        // Swap matrix
        vector = opglr.swapRotMatrix(vector);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    /* TODO */
    // your activity need to register accelerometer and magnetometer sensors' updates
    // then you may want to call
    //  this.opglr.swapRotMatrix()
    // with the 4x4 rotation matrix, everytime a new matrix is computed
    // more information on rotation matrix can be found on-line:
    // https://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[],%20float[],%20float[],%20float[])

}
