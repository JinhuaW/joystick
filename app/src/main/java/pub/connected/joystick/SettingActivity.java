package pub.connected.joystick;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import static pub.connected.joystick.MainActivity.socket;
import static pub.connected.joystick.MainActivity.writer;

public class SettingActivity extends AppCompatActivity implements Runnable {
    private Button btn_save, btn_conn;
    private EditText txt_ip, txt_port;
    private String server_ip = null;
    private int server_port = 0;
    private PreferencesService service;

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
        service = new PreferencesService(this);
        Map<String, String> params = service.getPerferences();
        server_ip = params.get("ip");
        server_port = Integer.parseInt(params.get("port"));
        if (!server_ip.isEmpty() && server_port !=0) {
            txt_ip.setText(params.get("ip"));
            txt_port.setText(params.get("port"));
        }
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
                    server_ip = txt_ip.getText().toString();
                    server_port = Integer.parseInt(txt_port.getText().toString());
                    if (!is_ip_addr(server_ip)){
                        Toast.makeText(getApplicationContext(), "设置失败，非法IP格式", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (server_port <= 0) {
                        Toast.makeText(getApplicationContext(), "设置失败，非法端口地址", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    service.save(server_ip, server_port);
                    break;
                case R.id.button_conn:
                    server_ip = txt_ip.getText().toString();
                    server_port = Integer.parseInt(txt_port.getText().toString());
                    if (!is_ip_addr(server_ip)){
                        Toast.makeText(getApplicationContext(), "设置失败，非法IP格式", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (server_port <= 0) {
                        Toast.makeText(getApplicationContext(), "设置失败，非法端口地址", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    new Thread(SettingActivity.this).start();
                    break;
            }
        }
    };

    private boolean is_ip_addr(String ip) {
        String m_ip[]=ip.split("\\.");
        if (m_ip.length != 4)
            return false;
        for (int i = 0; i < 4; i++) {
            int temp = Integer.parseInt(m_ip[i]);
            if (temp < 0 || temp > 255)
                return false;
        }
        return true;
    }
}
