package pub.connected.joystick;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements Runnable {
    private Joystick mSurfaceView;
    private TextView tv1;
    private static boolean isExit = false;

    private BufferedWriter writer = null;
   // private BufferedReader reader = null;
    private Socket socket=null;
    private Button btn_a,btn_b, btn_setting;

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

        mSurfaceView = (Joystick) findViewById(R.id.rudder);		//摇杆
        Display display = getWindowManager().getDefaultDisplay();	//取得萤幕大小
        Point size = new Point();									//
        display.getSize(size);										//
        mSurfaceView.getHolder().setFixedSize(size.y, size.y);	//设定摇杆操作区的大小
        SurfaceHolder holder=mSurfaceView.getHolder();
        tv1=(TextView) findViewById(R.id.txt1);
        tv1.setText("方向= "+ 0 +"		速度= "+ 0);
        btn_a = (Button) findViewById(R.id.button_A);
        btn_a.setOnClickListener(clickListener);
        btn_b = (Button) findViewById(R.id.button_B);
        btn_b.setOnClickListener(clickListener);
        btn_setting = (Button) findViewById(R.id.button_Setting);
        btn_setting.setOnClickListener(clickListener);
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
                    public void onSteeringWheelChanged(int speed, int dir)
                    {
                        if (socket.isConnected()) {
                            try {
                                writer.write("r: " + dir +" "+ speed+"\n");
                                writer.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        tv1.setText("方向= "+ dir +"		速度= "+ speed);
                    }
                }
        );
        new Thread(MainActivity.this).start();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 1000);
        } else {
            finish();
            System.exit(0);
        }
    }


    public void run(){
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("192.168.1.8", 9999), 4000);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Button.OnClickListener clickListener=new Button.OnClickListener(){
        public void onClick(View v){
            switch(v.getId()){
                case R.id.button_A:		//重力感应
                    if (socket.isConnected()) {
                        try {
                            writer.write("b: a\n");
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.button_B:
                    if (socket.isConnected()) {
                        try {
                            writer.write("b: b\n");
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.button_Setting:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SettingActivity.class);
                    MainActivity.this.startActivity(intent);
            }
        }
    };
}
