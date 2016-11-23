package pub.connected.joystick;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static pub.connected.joystick.MainActivity.socket;
import static pub.connected.joystick.MainActivity.writer;

public class SettingActivity extends AppCompatActivity implements Runnable {
    private Button btn_save, btn_conn;
    private EditText txt_ip, txt_port;
    private String server_ip = null;
    private int server_port = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btn_save = (Button) findViewById(R.id.button_save);
        btn_save.setOnClickListener(clickListener);
        btn_conn = (Button) findViewById(R.id.button_conn);
        btn_conn.setOnClickListener(clickListener);
        txt_ip = (EditText) findViewById(R.id.txt_ip);
        txt_port = (EditText) findViewById(R.id.txt_port);
        if (server_ip != null)
            txt_ip.setText(server_ip);
        if (server_port != 0)
            txt_port.setText(Integer.toString(server_port));
    }
    public void run(){
        if (socket.isConnected()){
            try {
                socket.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(server_ip, server_port), 1000);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Button.OnClickListener clickListener=new Button.OnClickListener(){
        public void onClick(View v){
            switch(v.getId()){
                case R.id.button_save:
                    if (socket.isConnected()) {

                    }
                    break;
                case R.id.button_conn:
                    server_ip = txt_ip.getText().toString();
                    server_port = Integer.parseInt(txt_port.getText().toString());
                    new Thread(SettingActivity.this).start();
                    break;
            }
        }
    };
}
