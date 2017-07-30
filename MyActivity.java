package application.oneal.nick.customscreenresolutionresizing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Timer;
import java.util.TimerTask;
public class MyActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_my);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static class GameFragment extends Fragment {
        private InterstitialAd mInterstitialAd;
        public int DEVICEscreenWidth, DEVICEscreenHeight;

        private RelativeLayout backgroundID;

        public int aMX, aMY, aMW,aMH, SMX, SMY, MX, MY; // Screen Mouse X, Screen Mouse Y, Mouse X, Mouse Y
        public int DEV = 3; // Device
        public int [] Dev_X = {16, 15, 16, 15, 16};
        public int [] Dev_Y = {12, 10, 10, 9, 9};

        public int TIME = 0; // Timer

        public Bitmap MAINBITMAP;
        public Bitmap DEVSCREENBITMAP;

        public String [] mapOn = map1(DEV, Dev_Y[DEV]);
        public int mapNum = 1;
        public boolean grid = true;


        public int margin = 0; // 1 percent of Width
        public String [] s_TopOptions = {"4:3","3:2","16:10","5:3","16:9", "FULL"};
        public String [] s_BottomOptions = {"Grid", "Map1", "Map2","Map3", "Map4", "Map5", "Map6"};
        public String [] s_STATS_L = {"free", "free", "free", "free", "free", "free", "free", "free", "free", "free", "free", "free", "free", "free", "free"};
        public String [] s_STATS_R = {"free", "free", "free", "free", "free", "free", "free", "free", "free", "free", "free", "free", "free", "free", "free"};

        public void startLoad(){
           DisplayMetrics metrics = getResources().getDisplayMetrics();
           DEVICEscreenWidth = metrics.widthPixels;
           DEVICEscreenHeight = metrics.heightPixels;
            s_STATS_L[0] = "MX: NA";
            s_STATS_L[1] = "MY: NA";
            s_STATS_L[2] = "S_Width: " + DEVICEscreenWidth;
            s_STATS_L[3] = "S_Height: " + DEVICEscreenHeight;
            s_STATS_R[1] = "SMX: NA";
            s_STATS_R[2] = "SMY: NA";
           margin = (int)(DEVICEscreenWidth * .015);
           timerRefresh(300);
       }
        public void timerRefresh(int intervals){
            TimerTask mTimerTask;
            final Handler timerHandler;
            Timer timer = new Timer();
            timerHandler = new Handler();
            mTimerTask = new TimerTask() {
                //this method is called every 1ms
                public void run() {
                    timerHandler.post(new Runnable() {
                        public void run() {
                            MAINBITMAP = createBitMap(DEVICEscreenWidth, DEVICEscreenHeight);
                            screenBitmap_Touch();
                            TIME++;
                            s_STATS_R[0] = "Redraws: " + String.valueOf(TIME);
                            s_STATS_R[3] = "SMW: " + (aMW - aMX);
                            s_STATS_R[4] = "SMH: " + (aMH - aMY);
                        }
                    });
                }
            };
            timer.schedule(mTimerTask, 10, intervals);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            startLoad();
            return rootView;
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            // Initialize the game and the ad.
            super.onActivityCreated(savedInstanceState);
            screenBitmap_Touch();
            initAd();
        }
        @Override
        public void onResume() {
            // Initialize the timer if it hasn't been initialized yet.
            // Start the game.
            super.onResume();
            startGame();
        }
        @Override
        public void onPause() {
            super.onPause();
        }

        private void initAd() {
            // Create the InterstitialAd and set the adUnitId.
            mInterstitialAd = new InterstitialAd(getActivity());
            // Defined in values/strings.xml
            mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
        }
        private void displayAd() {
            // Show the ad if it's ready. Otherwise toast and restart the game.
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                //Toast.makeText(getActivity(), "Ad did not load", Toast.LENGTH_SHORT).show();
                startGame();
            }
        }
        private void startGame() {
            // Hide the retry button, load the ad, and start the timer.
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }

        public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                    matrix, false);
            return resizedBitmap;
        }
        public Bitmap createBitMap(int width, int height) {
            Bitmap bitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitMap = bitMap.copy(bitMap.getConfig(), true);
            Canvas canvas = new Canvas(bitMap);
            Paint paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setAntiAlias(true);
            paint.setColor(Color.rgb(230, 230, 230));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, width, height, paint);
            draw_Device(canvas, paint);
            return bitMap;
        }
        public void screenBitmap_Touch() {
            // Create the "retry" button, which tries to show an interstitial between game plays.
            backgroundID = ((RelativeLayout) getView().findViewById(R.id.backgroundid));
            backgroundID.setBackgroundDrawable(new BitmapDrawable(MAINBITMAP));
            backgroundID.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        MX = (int) event.getX();
                        MY = (int) event.getY();
                        s_STATS_L[0] = "MX: " + String.valueOf(MX);
                        s_STATS_L[1] = "MY: " + String.valueOf(MY);
                        if ((((MX-aMX) >= 0) && ((MX-aMX) <= (aMW - aMX))) && (((MY-aMY) >= 0) && ((MY-aMY) <= (aMH - aMY)))) {
                            s_STATS_R[1] = "SMX: " + (MX - aMX);
                            s_STATS_R[2] = "SMY: " + (MY - aMY);
                        } else
                        {
                            s_STATS_R[1] = "SMX: NA";
                            s_STATS_R[2] = "SMY: NA";
                        }
                        s_STATS_R[0] = "Redraws: " + String.valueOf(TIME);
                        MAINBITMAP = createBitMap(DEVICEscreenWidth, DEVICEscreenHeight);
                        TIME++;
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        MX = (int) event.getX();
                        MY = (int) event.getY();
                        check_TopOptions();
                        check_BottomOptions();
                        MAINBITMAP = createBitMap(DEVICEscreenWidth, DEVICEscreenHeight);
                        TIME++;
                        s_STATS_L[0] = "MX: " + String.valueOf(MX);
                        s_STATS_L[1] = "MY: " + String.valueOf(MY);
                        s_STATS_R[3] = "SMW: " + (aMW - aMX);
                        s_STATS_R[4] = "SMH: " + (aMH - aMY);

                        if ((((MX-aMX) >= 0) && ((MX-aMX) <= (aMW - aMX))) && (((MY-aMY) >= 0) && ((MY-aMY) <= (aMH - aMY)))) {
                            s_STATS_R[1] = "SMX: " + (MX - aMX);
                            s_STATS_R[2] = "SMY: " + (MY - aMY);
                        } else
                        {
                            s_STATS_R[1] = "SMX: NA";
                            s_STATS_R[2] = "SMY: NA";
                        }
                        MAINBITMAP = createBitMap(DEVICEscreenWidth, DEVICEscreenHeight);
                        TIME++;
                        s_STATS_R[0] = "Redraws: " + String.valueOf(TIME);
                        backgroundID.setBackgroundDrawable(new BitmapDrawable(MAINBITMAP));
                      //  startGame();
                      //  displayAd();
                    }
                    return true;
                }
            });
        }

        public void draw_Device (Canvas canvas, Paint paint){
            paint.setColor(Color.rgb(0,0,0));
            draw_TopOptions(canvas, paint);
            draw_BottomOptions(canvas, paint);
            draw_DeviceScreen(canvas, paint);
            draw_LeftBubble(canvas, paint);
            draw_RightBubble(canvas, paint);
        }

        public void draw_TopOptions (Canvas canvas, Paint paint){
            int boxSizeW , boxSizeH;
            boxSizeW =  (int)((DEVICEscreenWidth - (margin * (s_TopOptions.length + 1)))/s_TopOptions.length);
            boxSizeH = (int) ((DEVICEscreenHeight * .20) - (margin * 2));

            for (int i = 0; i < s_TopOptions.length; ++i) {
                if (DEV == i)
                paint =paintSet(paint,false,1,Color.rgb(0, 255, 137));
                else
                paint =paintSet(paint,false,1,Color.rgb(0, 255, 234));
                draw_Rect(canvas, paint, ((boxSizeW * i) + (margin * i) + margin), margin,((boxSizeW * (i+1)) + (margin * i) + margin), margin + boxSizeH);
                paint =paintSet(paint,true,4,Color.rgb(0,0,0));
                draw_Rect(canvas, paint, ((boxSizeW * i) + (margin * i) + margin), margin,((boxSizeW * (i+1)) + (margin * i) + margin), margin + boxSizeH);
                paint.setStrokeWidth(1);
                paint.setTextSize(boxSizeH/2);
                String temp = s_TopOptions[i];

                int x, y, s, s1;

                Rect bounds = new Rect();
                paint.getTextBounds(temp, 0, temp.length(), bounds);
                x = (int)paint.measureText(temp);
                y = bounds.height();

                s = (int)(boxSizeW/2 - x/2);
                s1 = (int)(boxSizeH/2 - y/2);
                paint =paintSet(paint,false,1,Color.rgb(0,0,0));
                canvas.drawText(temp,(int)((boxSizeW * i) + (margin * i) + margin + s),(int)(margin + y + s1),paint);
            }
        }
        public void draw_BottomOptions (Canvas canvas, Paint paint){
            int boxSizeW , boxSizeH;
            boxSizeW =  (int)((DEVICEscreenWidth - (margin * (s_BottomOptions.length + 1)))/s_BottomOptions.length);
            boxSizeH = (int) ((DEVICEscreenHeight * .20) - (margin * 2));

            for (int i = 0; i < s_BottomOptions.length; ++i) {
                if (i==0){
                    if (grid == true) {
                        paint =paintSet(paint,false,1,Color.rgb(0, 255, 137));
                    }else
                    paint =paintSet(paint,false,1,Color.rgb(0, 255, 234));
                }
                else
                    paint =paintSet(paint,false,1,Color.rgb(0, 255, 234));

                if ((i==1) || (i==2)|| (i==3)|| (i==4)){
                if (mapNum == i){
                    paint =paintSet(paint,false,1,Color.rgb(0, 255, 137));
                }else
                    paint =paintSet(paint,false,1,Color.rgb(0, 255, 234));
                }

                draw_Rect(canvas, paint, ((boxSizeW * i) + (margin * i) + margin), DEVICEscreenHeight - margin,((boxSizeW * (i+1)) + (margin * i) + margin), DEVICEscreenHeight -margin - boxSizeH);
                paint = paintSet(paint,true,4,Color.rgb(0,0,0));
                draw_Rect(canvas, paint, ((boxSizeW * i) + (margin * i) + margin),DEVICEscreenHeight -  margin,((boxSizeW * (i+1)) + (margin * i) + margin), DEVICEscreenHeight -margin - boxSizeH);
                paint.setStrokeWidth(1);
                paint.setTextSize(boxSizeH/2);
                String temp = s_BottomOptions[i];

                int x, y, s, s1;

                Rect bounds = new Rect();
                paint.getTextBounds(temp, 0, temp.length(), bounds);
                x = (int)paint.measureText(temp);
                y = bounds.height();

                s = (int)(boxSizeW/2 - x/2);
                s1 = (int)(boxSizeH/2 - y/2);
                paint =paintSet(paint,false,1,Color.rgb(0,0,0));
                canvas.drawText(temp,(int)((boxSizeW * i) + (margin * i) + margin + s),(int)(DEVICEscreenHeight - margin - s1),paint);
            }
        }
        public void draw_LeftBubble(Canvas canvas, Paint paint){
            int a = (int)(DEVICEscreenHeight * .20);
            int b = DEVICEscreenHeight - a;
            int c = DEVICEscreenHeight - (a*2);
            int d = (int)(c * 1.77777777777778);
            d = (int)(d/Dev_X[4])*Dev_X[4];

            int e = DEVICEscreenWidth - d;
            int f = e/2;
            paint = paintSet(paint,false,1,Color.rgb(129,129,129));
            draw_Rect(canvas, paint, margin, a,f - margin, b);
            paint = paintSet(paint,true,3,Color.rgb(0,0,0));
            draw_Rect(canvas, paint, margin, a,f - margin, b);
            paint = paintSet(paint,true,3,Color.rgb(255,255,255));

            int h;
            h = (b-a)/s_STATS_L.length;

            paint.setTextSize(h);
            paint = paintSet(paint,false,1,Color.rgb(255,255,255));
            for (int i = 0; i < s_STATS_L.length; ++i) {
                canvas.drawText(s_STATS_L[i], margin, a + h * (i+1), paint);
            }
        }
        public void draw_RightBubble(Canvas canvas, Paint paint){
            int a = (int)(DEVICEscreenHeight * .20);
            int b = DEVICEscreenHeight - a;
            int c = DEVICEscreenHeight - (a*2);
            int d = (int)(c * 1.77777777777778);
            d = (int)(d/Dev_X[4])*Dev_X[4];
            int e = DEVICEscreenWidth - d;
            int f = e/2;

            paint = paintSet(paint,false,1,Color.rgb(129,129,129));
            draw_Rect(canvas, paint, DEVICEscreenWidth - f + margin, a, DEVICEscreenWidth - margin, b);
            paint = paintSet(paint,true,3,Color.rgb(0,0,0));
            draw_Rect(canvas, paint, DEVICEscreenWidth - f + margin, a, DEVICEscreenWidth - margin, b);

            int h;
            h = (b-a)/s_STATS_R.length;

            paint.setTextSize(h);
            paint = paintSet(paint,false,1,Color.rgb(255,255,255));
            for (int i = 0; i < s_STATS_R.length; ++i) {
                canvas.drawText(s_STATS_R[i], DEVICEscreenWidth - f +margin, a + h * (i+1), paint);
            }
        }
        public void draw_DeviceScreen(Canvas canvas, Paint paint){
            int a = (int)(DEVICEscreenHeight * .20);
            int b = DEVICEscreenHeight - a;
            int c = DEVICEscreenHeight - (a*2);

            int d = (int)(c * 1.77777777777778);
            d = (int)(d/Dev_X[4])*Dev_X[4];
            int e = DEVICEscreenWidth - d;
            int f = e/2;
            int h;

            paint = paintSet(paint, false, 1, Color.rgb(0,242,222));
            draw_Rect(canvas, paint, f,a, DEVICEscreenWidth - f, b);

            if (DEV == 0){
                d = (int)(c * 1.33333333333333);
                d = (int)(d/Dev_X[0])*Dev_X[0];
                e = DEVICEscreenWidth - d;
                h = e/2;
                draw_DeviceScreen_4_3(canvas, paint,h,a, DEVICEscreenWidth - h, b);
            }
            else if (DEV == 1){
                d = (int)(c * 1.5);
                d = (int)(d/Dev_X[1])*Dev_X[1];
                e = DEVICEscreenWidth - d;
                h = e/2;
                draw_DeviceScreen_3_2(canvas, paint,h,a, DEVICEscreenWidth - h, b);
            }
            else if (DEV == 2){
                d = (int)(c * 1.6);
                d = (int)(d/Dev_X[2])*Dev_X[2];
                e = DEVICEscreenWidth - d;
                h = e/2;
                draw_DeviceScreen_16_10(canvas, paint,h,a, DEVICEscreenWidth - h, b);
            }
            else if (DEV == 3){
                d = (int)(c * 1.66666666666667);
                d = (int)(d/Dev_X[3])*Dev_X[3];
                e = DEVICEscreenWidth - d;
                h = e/2;
                draw_DeviceScreen_5_3(canvas, paint,h,a, DEVICEscreenWidth - h, b);
            }
            else if (DEV == 4){
                d = (int)(c * 1.77777777777778);
                d = (int)(d/Dev_X[4])*Dev_X[4];
                e = DEVICEscreenWidth - d;
                h = e/2;
                draw_DeviceScreen_16_9(canvas, paint,h,a, DEVICEscreenWidth - h, b);
            }
            paint = paintSet(paint, true, 5, Color.rgb(0,0,0));
            draw_Rect(canvas, paint, f,a, DEVICEscreenWidth - f, b);
            paint = paintSet(paint, true, 5, Color.rgb(255,0,0));
            draw_Rect(canvas, paint, MX, MY, MX + 50, MY +50);
        }

        public void check_TopOptions (){
            int boxSizeW , boxSizeH;
            boxSizeW =  (int)((DEVICEscreenWidth - (margin * (s_TopOptions.length + 1)))/s_TopOptions.length);
            boxSizeH = (int) ((DEVICEscreenHeight * .20) - (margin * 2));
            for (int i = 0; i < s_TopOptions.length -1; ++i) {
                if ((MX >= ((boxSizeW * i) + (margin * i) + margin)) && (MX <= ((boxSizeW * (i+1)) + (margin * i) + margin)))
                    if ((MY >= margin) && (MY <= margin + boxSizeH)){
                        DEV = i;
                        if (mapNum == 1)
                        mapOn = map1(DEV, Dev_Y[DEV]);
                        else if (mapNum == 2)
                            mapOn = map2(DEV, Dev_Y[DEV]);
                        else if (mapNum == 3)
                            mapOn = map3(DEV, Dev_Y[DEV]);
                        else if (mapNum == 4)
                            mapOn = map4(DEV, Dev_Y[DEV]);
                    }
            }
        }
        public void check_BottomOptions (){
            int boxSizeW , boxSizeH;
            boxSizeW =  (int)((DEVICEscreenWidth - (margin * (s_BottomOptions.length + 1)))/s_BottomOptions.length);
            boxSizeH = (int) ((DEVICEscreenHeight * .20) - (margin * 2));
            for (int i = 0; i < s_BottomOptions.length; ++i) {
                if ((MX >= ((boxSizeW * i) + (margin * i) + margin)) && (MX <= ((boxSizeW * (i+1)) + (margin * i) + margin)))
                    if ((MY >=(DEVICEscreenHeight -margin - boxSizeH)) && (MY <=  (DEVICEscreenHeight - margin))){
                        if (i == 0) {
                            if (grid == true)
                                grid = false;
                            else
                                grid = true;
                        }
                        else if (i == 1){
                            mapOn = map1(DEV, Dev_Y[DEV]);
                            mapNum = i;
                        }
                        else if (i == 2){
                            mapOn = map2(DEV, Dev_Y[DEV]);
                            mapNum = i;
                        }
                        else if (i == 3){
                            mapOn = map3(DEV, Dev_Y[DEV]);
                            mapNum = i;
                        }else if (i == 4){
                            mapOn = map4(DEV, Dev_Y[DEV]);
                            mapNum = i;
                        }

                    }
            }
        }

        public void draw_DeviceScreen_4_3 (Canvas canvas, Paint paint, int x, int y, int w, int h){
            aMX = x;
            aMY = y;
            aMW = w;
            aMH = h;
            paint = paintSet(paint, false, 1, Color.rgb(255,255,255));
            draw_Rect(canvas, paint, x, y, w, h);
            draw_map_4_3(canvas, paint, x, y, w, h);
            paint = paintSet(paint, true, 3, Color.rgb(0,0,0));
            draw_Rect(canvas, paint, x, y, w, h);
        }
        public void draw_DeviceScreen_3_2 (Canvas canvas, Paint paint, int x, int y, int w, int h){
            aMX = x;
            aMY = y;
            aMW = w;
            aMH = h;
            paint = paintSet(paint, false, 1, Color.rgb(255,255,255));
            draw_Rect(canvas, paint, x, y, w, h);
            draw_map_3_2(canvas, paint, x, y, w, h);
            paint = paintSet(paint, true, 3, Color.rgb(0,0,0));
            draw_Rect(canvas, paint, x, y, w, h);
        }
        public void draw_DeviceScreen_16_10 (Canvas canvas, Paint paint, int x, int y, int w, int h){
            aMX = x;
            aMY = y;
            aMW = w;
            aMH = h;
            paint = paintSet(paint, false, 1, Color.rgb(255,255,255));
            draw_Rect(canvas, paint, x, y, w, h);
            draw_map_16_10(canvas, paint, x, y, w, h);
            paint = paintSet(paint, true, 3, Color.rgb(0,0,0));
            draw_Rect(canvas, paint, x, y, w, h);
        }
        public void draw_DeviceScreen_5_3 (Canvas canvas, Paint paint, int x, int y, int w, int h){
            aMX = x;
            aMY = y;
            aMW = w;
            aMH = h;
            paint = paintSet(paint, false, 1, Color.rgb(255,255,255));
            draw_Rect(canvas, paint, x, y, w, h);
            draw_map_5_3(canvas, paint, x, y, w, h);
            paint = paintSet(paint, true, 3, Color.rgb(0,0,0));
            draw_Rect(canvas, paint, x, y, w, h);
        }
        public void draw_DeviceScreen_16_9 (Canvas canvas, Paint paint, int x, int y, int w, int h){
            aMX = x;
            aMY = y;
            aMW = w;
            aMH = h;
            paint = paintSet(paint, false, 1, Color.rgb(255,255,255));
            draw_Rect(canvas, paint, x, y, w, h);
            draw_map_16_9(canvas, paint, x, y, w, h);
            paint = paintSet(paint, true, 3, Color.rgb(0,0,0));
            draw_Rect(canvas, paint, x, y, w, h);
        }

        public void draw_map_4_3 (Canvas canvas, Paint paint ,int x, int y, int w, int h){
            int a = Dev_X[0];
            int b = 12;
            double c = (w-x)/a;
            double d = (h-y)/b;
            for (int i = 0; i < a; ++i)
                for (int j = 0; j < b; ++j){
                    paint = paintSet(paint, false,1, getColorMap(mapOn,i,j));
                    draw_Rect(canvas, paint, (int)(x + c * i), (int)(y + d * j), (int)(x + c + c *i),(int) (y + d + d *j));
                    if (grid == true) {
                        paint = paintSet(paint, true, 3, Color.rgb(0, 0, 0));
                        draw_Rect(canvas, paint, (int) (x + c * i), (int) (y + d * j), (int) (x + c + c * i), (int) (y + d + d * j));
                    }
                }
        }
        public void draw_map_3_2 (Canvas canvas, Paint paint ,int x, int y, int w, int h){
            int a = Dev_X[1];
            int b = 10;
            double c = (w-x)/a;
            double d = (h-y)/b;
            paint = paintSet(paint, true,3, Color.rgb(0,0,0));
            for (int i = 0; i < a; ++i)
                for (int j = 0; j < b; ++j){
                    paint = paintSet(paint, false,1, getColorMap(mapOn,i,j));
                    draw_Rect(canvas, paint, (int)(x + c * i), (int)(y + d * j), (int)(x + c + c *i),(int) (y + d + d *j));
                    if (grid == true) {
                    paint = paintSet(paint, true,3, Color.rgb(0,0,0));
                    draw_Rect(canvas, paint, (int)(x + c * i), (int)(y + d * j), (int)(x + c + c *i),(int) (y + d + d *j));
                }}
        }
        public void draw_map_16_10 (Canvas canvas, Paint paint ,int x, int y, int w, int h){
            int a = Dev_X[2];
            int b = 10;
            double c = (w-x)/a;
            double d = (h-y)/b;
            paint = paintSet(paint, true,3, Color.rgb(0,0,0));
            for (int i = 0; i < a; ++i)
                for (int j = 0; j < b; ++j){
                    paint = paintSet(paint, false,1, getColorMap(mapOn,i,j));
                    draw_Rect(canvas, paint, (int)(x + c * i), (int)(y + d * j), (int)(x + c + c *i),(int) (y + d + d *j));
                    if (grid == true) {
                    paint = paintSet(paint, true,3, Color.rgb(0,0,0));
                    draw_Rect(canvas, paint, (int)(x + c * i), (int)(y + d * j), (int)(x + c + c *i),(int) (y + d + d *j));
                }}
        }
        public void draw_map_5_3 (Canvas canvas, Paint paint ,int x, int y, int w, int h){
            int a = Dev_X[3];
            int b = 9;
            double c = (w-x)/a;
            double d = (h-y)/b;
            paint = paintSet(paint, true,3, Color.rgb(0,0,0));
            for (int i = 0; i < a; ++i)
                for (int j = 0; j < b; ++j){
                    paint = paintSet(paint, false,1, getColorMap(mapOn,i,j));
                    draw_Rect(canvas, paint, (int)(x + c * i), (int)(y + d * j), (int)(x + c + c *i),(int) (y + d + d *j));
                    if (grid == true) {
                    paint = paintSet(paint, true,3, Color.rgb(0,0,0));
                    draw_Rect(canvas, paint, (int)(x + c * i), (int)(y + d * j), (int)(x + c + c *i),(int) (y + d + d *j));
                }}
        }
        public void draw_map_16_9 (Canvas canvas, Paint paint ,int x, int y, int w, int h){
            int a = Dev_X[4];
            int b = 9;
            double c = (w-x)/a;
            double d = (h-y)/b;
            paint = paintSet(paint, true,3, Color.rgb(0,0,0));
            for (int i = 0; i < a; ++i)
                for (int j = 0; j < b; ++j){
                    paint = paintSet(paint, false,1, getColorMap(mapOn,i,j));
                    draw_Rect(canvas, paint, (int)(x + c * i), (int)(y + d * j), (int)(x + c + c *i),(int) (y + d + d *j));
                    if (grid == true) {
                    paint = paintSet(paint, true,3, Color.rgb(0,0,0));
                    draw_Rect(canvas, paint, (int)(x + c * i), (int)(y + d * j), (int)(x + c + c *i),(int) (y + d + d *j));
                }}
        }

        public int getColorMap(String []string, int i, int j){
            if (string[j].charAt(i) == '0')
                return Color.rgb(244,164,96);
            else if (string[j].charAt(i) == '1')
                return Color.rgb(152,251,152);
            else
            return Color.rgb(255,0,255);
        }

        public Paint paintSet(Paint paint, boolean stroke, int width, int color){
            paint.setStrokeWidth(width);
            if (stroke == true)
                paint.setStyle(Paint.Style.STROKE);
            else
                paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            return paint;
        }
        public void draw_Rect(Canvas canvas, Paint paint, int x1, int y1, int x2, int y2){
            canvas.drawRect(new Rect(x1,y1,x2,y2),paint);
        }


        public static String[] map1(int device, int sy){
            String[] map = new String[sy];
            if (device == 0){ //16:12
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "1111111111111111";
                map[3] =  "1111111111111111";
                map[4] =  "0000000000000000";
                map[5] =  "1111111111111111";
                map[6] =  "1111111111111111";
                map[7] =  "1111111111111111";
                map[8] =  "1111111111111111";
                map[9] =  "1111111111111111";
                map[10] = "1111111111111111";
                map[11] =  "1111111111111111";
            } else if  (device == 1){ //15:10
                map[0] =  "111111111111111";
                map[1] =  "111111111111111";
                map[2] =  "111111111111111";
                map[3] =  "000000000000000";
                map[4] =  "111111111111111";
                map[5] =  "111111111111111";
                map[6] =  "111111111111111";
                map[7] =  "111111111111111";
                map[8] =  "111111111111111";
                map[9] =  "111111111111111";
            }else if  (device == 2){ //16:10
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "1111111111111111";
                map[3] =  "0000000000000000";
                map[4] =  "1111111111111111";
                map[5] =  "1111111111111111";
                map[6] =  "1111111111111111";
                map[7] =  "1111111111111111";
                map[8] =  "1111111111111111";
                map[9] =  "1111111111111111";
            }else if  (device == 3){ //15:9
                map[0] =  "111111111111111";
                map[1] =  "111111111111111";
                map[2] =  "000000000000000";
                map[3] =  "111111111111111";
                map[4] =  "111111111111111";
                map[5] =  "111111111111111";
                map[6] =  "111111111111111";
                map[7] =  "111111111111111";
                map[8] =  "111111111111111";

            }else if  (device == 4){ //16:9
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "0000000000000000";
                map[3] =  "1111111111111111";
                map[4] =  "1111111111111111";
                map[5] =  "1111111111111111";
                map[6] =  "1111111111111111";
                map[7] =  "1111111111111111";
                map[8] =  "1111111111111111";
            }
            return map;
        }
        public static String[] map2(int device, int sy){
            String[] map = new String[sy];
            if (device == 0){ //16:12
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "1111111111111111";
                map[3] =  "1111111111111111";
                map[4] =  "0000000000000000";
                map[5] =  "1111111101111111";
                map[6] =  "1111111101111111";
                map[7] =  "1111111100011111";
                map[8] =  "1111111111111111";
                map[9] =  "1111111111111111";
                map[10] = "1111111111111111";
                map[11] =  "1111111111111111";
            } else if  (device == 1){ //15:10
                map[0] =  "111111111111111";
                map[1] =  "111111111111111";
                map[2] =  "111111111111111";
                map[3] =  "000000000000000";
                map[4] =  "111111110111111";
                map[5] =  "111111110111111";
                map[6] =  "111111110111111";
                map[7] =  "111111111111111";
                map[8] =  "111111111111111";
                map[9] =  "111111111111111";
            }else if  (device == 2){ //16:10
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "1111111111111111";
                map[3] =  "0000000000000000";
                map[4] =  "1111111101111111";
                map[5] =  "1111111101111111";
                map[6] =  "1111111101111111";
                map[7] =  "1111111111111111";
                map[8] =  "1111111111111111";
                map[9] =  "1111111111111111";
            }else if  (device == 3){ //15:9
                map[0] =  "111111111111111";
                map[1] =  "111111111111111";
                map[2] =  "000000000000000";
                map[3] =  "111111110111111";
                map[4] =  "111111110111111";
                map[5] =  "111111110111111";
                map[6] =  "111111110111111";
                map[7] =  "111111111111111";
                map[8] =  "111111111111111";
            }else if  (device == 4){ //16:9
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "0000000000000000";
                map[3] =  "1111111101111111";
                map[4] =  "1111111101111111";
                map[5] =  "1111111101111111";
                map[6] =  "1111111101111111";
                map[7] =  "1111111111111111";
                map[8] =  "1111111111111111";
            }
            return map;
        }
        public static String[] map3(int device, int sy){
            String[] map = new String[sy];
            if (device == 0){ //16:12
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "1111111111111111";
                map[3] =  "1111111110111111";
                map[4] =  "0000000000000000";
                map[5] =  "1111111101111111";
                map[6] =  "1111111101111111";
                map[7] =  "1111111100011111";
                map[8] =  "1111111111111111";
                map[9] =  "1111111111111111";
                map[10] = "1111111111111111";
                map[11] =  "1111111111111111";
            } else if  (device == 1){ //15:10
                map[0] =  "111111111111111";
                map[1] =  "111111111111111";
                map[2] =  "111111111111111";
                map[3] =  "000000000000000";
                map[4] =  "111111110111111";
                map[5] =  "111111110111111";
                map[6] =  "111111110111111";
                map[7] =  "111111111101111";
                map[8] =  "111111111111111";
                map[9] =  "111111111111111";
            }else if  (device == 2){ //16:10
                map[0] =  "1111111111111111";
                map[1] =  "1111111111011111";
                map[2] =  "1111111111111111";
                map[3] =  "0000000000000000";
                map[4] =  "1111111101111111";
                map[5] =  "1111111101111111";
                map[6] =  "1111111101111111";
                map[7] =  "1111111111111111";
                map[8] =  "1111111111111111";
                map[9] =  "1111111111111111";
            }else if  (device == 3){ //15:9
                map[0] =  "111111111111111";
                map[1] =  "111111111111111";
                map[2] =  "000000000000000";
                map[3] =  "111111110111111";
                map[4] =  "111111110111111";
                map[5] =  "111111110111111";
                map[6] =  "111101110111111";
                map[7] =  "111111111111111";
                map[8] =  "111111111111111";
            }else if  (device == 4){ //16:9
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "0000000000000000";
                map[3] =  "1111111101111111";
                map[4] =  "1111111101111111";
                map[5] =  "1111111101111111";
                map[6] =  "1111111101011111";
                map[7] =  "1111111111111111";
                map[8] =  "1111111111111111";
            }
            return map;
        }
        public static String[] map4(int device, int sy){
            String[] map = new String[sy];
            if (device == 0){ //16:12
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "1111111111111111";
                map[3] =  "1111111111111111";
                map[4] =  "0000000000000000";
                map[5] =  "1111111101111111";
                map[6] =  "1111111101111111";
                map[7] =  "1111111100011111";
                map[8] =  "1111111111111111";
                map[9] =  "1101111111111111";
                map[10] = "1111111111111111";
                map[11] =  "1111111111111111";
            } else if  (device == 1){ //15:10
                map[0] =  "111111111111111";
                map[1] =  "111111111111111";
                map[2] =  "111111111111111";
                map[3] =  "000000000000000";
                map[4] =  "111111110111111";
                map[5] =  "111111110111111";
                map[6] =  "111111110111111";
                map[7] =  "111111111111111";
                map[8] =  "111111111101111";
                map[9] =  "111111111111111";
            }else if  (device == 2){ //16:10
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "1111111111111111";
                map[3] =  "0000000000000000";
                map[4] =  "1111111101111111";
                map[5] =  "1111111101111111";
                map[6] =  "1111111101111111";
                map[7] =  "1111111111111111";
                map[8] =  "1111111111111111";
                map[9] =  "1111111110111111";
            }else if  (device == 3){ //15:9
                map[0] =  "111111111111111";
                map[1] =  "111111111111111";
                map[2] =  "000000000000000";
                map[3] =  "111111110111111";
                map[4] =  "111111110111111";
                map[5] =  "111101110111111";
                map[6] =  "111111110111111";
                map[7] =  "111111111111111";
                map[8] =  "111111111111111";
            }else if  (device == 4){ //16:9
                map[0] =  "1111111111111111";
                map[1] =  "1111111111111111";
                map[2] =  "0000000000010000";
                map[3] =  "1111111101111111";
                map[4] =  "1111111101111111";
                map[5] =  "1111111101111111";
                map[6] =  "1111111101111111";
                map[7] =  "1111111111111111";
                map[8] =  "1111111111111111";
            }
            return map;
        }
    }
}
