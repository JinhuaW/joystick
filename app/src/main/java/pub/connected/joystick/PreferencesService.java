package pub.connected.joystick;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinhuawu on 16-11-25.
 */

public class PreferencesService {
    private Context context;

    public PreferencesService(Context context) {
        this.context = context;
    }

    public void save(String ip, Integer port) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("joystick", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("ip", ip);
        editor.putInt("port", port);
        editor.commit();
    }

    /**
     * 获取各项参数
     * @return
     */
    public Map<String, String> getPerferences() {
        Map<String, String> params = new HashMap<String, String>();
        SharedPreferences preferences = context.getSharedPreferences("joystick", Context.MODE_PRIVATE);
        params.put("ip", preferences.getString("ip", ""));
        params.put("port", String.valueOf(preferences.getInt("port", 0)));
        return params;
    }

}
