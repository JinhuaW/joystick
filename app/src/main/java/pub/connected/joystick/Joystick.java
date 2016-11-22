package pub.connected.joystick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;


/**
 * Created by jinhuawu on 16-11-20.
 */

public class Joystick extends SurfaceView implements Callback {
    private SurfaceHolder sfh;
    private Canvas canvas;
    private Paint mPaint;
    private int RockerCircleX,RockerCircleY,RockerCircleR,SmallRockerCircleX,SmallRockerCircleY,SmallRockerCircleR;
    private int speed = 0, Angle, dir = 0;
    /*
          0
        3   1 //dir
          2
     */
    Bitmap bitmap,bitmap2;
    Display display;
    Point size;

    public Joystick(Context context, AttributeSet attrs) {
        super(context, attrs);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        RockerCircleR=size.y/5;
        SmallRockerCircleR=RockerCircleR/4;
        SmallRockerCircleX=RockerCircleX=size.y/3;
        SmallRockerCircleY=RockerCircleY=size.y*2/3;
        /*th = new Thread(this);*/
        if (isInEditMode()) {
            return;
        }
        sfh = this.getHolder();
        sfh.addCallback(this);
        sfh.setFormat(PixelFormat.TRANSLUCENT);

        mPaint = new Paint();


        setFocusable(true);
        setZOrderOnTop(true);
        setFocusableInTouchMode(true);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rocker);
        bitmap = Bitmap.createScaledBitmap(bitmap, RockerCircleR*2, RockerCircleR*2, false);
        bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.rocker_point);
        bitmap2 = Bitmap.createScaledBitmap(bitmap2, SmallRockerCircleR*2, SmallRockerCircleR*2, false);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int len, x, y, action, new_speed = -1, new_dir = -1;
        action = event.getAction();
        try {
            if (MotionEvent.ACTION_DOWN == action || MotionEvent.ACTION_MOVE == action) {
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                len = (int) Math.sqrt(Math.pow((RockerCircleX - x), 2) + Math.pow((RockerCircleY - y), 2));

                if (len > 3 * RockerCircleR) {
                    return true;
                } else if (len < RockerCircleR) {
                    SmallRockerCircleX = x;
                    SmallRockerCircleY = y;
                } else {
                    Point point = getBorderPoint(RockerCircleX, RockerCircleY, x, y);
                    SmallRockerCircleX = point.x;
                    SmallRockerCircleY = point.y;
                }
                new_speed = len * 3 / RockerCircleR;
                if (new_speed > 4)
                    new_speed = 4;
                Angle = (getAngle(RockerCircleX, RockerCircleY, x, y) + 360) % 360;
                new_dir = get_dir(Angle);
            } else if (MotionEvent.ACTION_UP == action) {
                new_speed=0; new_dir = 0;
                SmallRockerCircleX = RockerCircleX;
                SmallRockerCircleY = RockerCircleY;
            }
            draw();
            Thread.sleep(20);
        } catch (Exception e){

        }

        if (new_speed >= 0 && new_dir >=0) {
            if (new_speed != speed || (new_speed == speed && new_speed != 0 && new_dir != dir)) {
                listener.onSteeringWheelChanged(new_speed, new_dir);
                speed = new_speed;
                dir = new_dir;
            }
        }

        return true;
    }

    private int get_dir(int angle) {
        if (angle >= 315 || angle <45)
            return 0;
        else
            return (int) (angle-45)/90 +1;
    }

    private static int getAngle (float px1, float py1, float px2, float py2) {
        float lenA = py1 - py2;
        float lenB = px1 - px2;
        float lenC = (float)Math.sqrt(lenA*lenA+lenB*lenB);
        float ang = (float)Math.acos(lenA/lenC);
        ang = ang * (px2 < px1 ? -1 : 1);
        return ((int)Math.round(ang/Math.PI*180));
    }

    private Point getBorderPoint (float px1, float py1, float px2, float py2) {
        float lenA = px2-px1;
        float lenB = py2-py1;
        float lenC = (float)Math.sqrt(lenA*lenA+lenB*lenB);
        float ang = (float)Math.acos(lenA/lenC);
        ang = ang * (py2 < py1 ? -1 : 1);
        return new Point((int)px1 + (int)(RockerCircleR * Math.cos(ang)), (int)py1 + (int)(RockerCircleR * Math.sin(ang)));
    }

    private void draw() {
        try {
            canvas = sfh.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
            mPaint.setColor(Color.GREEN);
            canvas.drawBitmap(bitmap, RockerCircleX-RockerCircleR, RockerCircleY-RockerCircleR, mPaint);
            canvas.drawBitmap(bitmap2, SmallRockerCircleX - SmallRockerCircleR, SmallRockerCircleY - SmallRockerCircleR, mPaint);

            mPaint.setColor(0xbbffffff);	//绘制中心点
            canvas.drawCircle(RockerCircleX, RockerCircleY, RockerCircleR/12, mPaint);

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            try {
                if (canvas != null)
                    sfh.unlockCanvasAndPost(canvas);
            } catch (Exception e2) {

            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        draw();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private SingleRudderListener listener = null;
    public interface SingleRudderListener {
        void onSteeringWheelChanged(int speed, int dir);//具体的方法
    }
    public void setSingleRudderListener(SingleRudderListener rockerListener) {
        listener = rockerListener;
    }
}