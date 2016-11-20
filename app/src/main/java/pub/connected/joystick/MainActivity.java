package pub.connected.joystick;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Joystick mSurfaceView;
    private TextView tv1;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContentView = findViewById(R.id.activity_main);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mSurfaceView = (Joystick) findViewById(R.id.rudder);		//摇杆
        Display display = getWindowManager().getDefaultDisplay();	//取得萤幕大小
        Point size = new Point();									//
        display.getSize(size);										//
        mSurfaceView.getHolder().setFixedSize(size.y, size.y);	//设定摇杆操作区的大小
        SurfaceHolder holder=mSurfaceView.getHolder();
        tv1=(TextView) findViewById(R.id.txt1);
        tv1.setText("角度= "+ 0 +"		速度= "+ 0);
        holder.addCallback(
                new Callback(){
                    public void surfaceDestroyed(SurfaceHolder holder){}
                    public void surfaceCreated(SurfaceHolder holder){}
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){	//当摇杆有变化时 会呼叫回来
                    }
                }
        );
        mSurfaceView.setSingleRudderListener(
                new Joystick.SingleRudderListener() {
                    public void onSteeringWheelChanged(int speed, int angle)
                    {//具体实现
                        tv1.setText("角度= "+ angle +"		速度= "+ speed);
                    }
                }
        );

    }
}
