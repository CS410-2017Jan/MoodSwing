package com.moodswing.emotion_effects_helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.os.Process;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.affectiva.android.affdex.sdk.detector.Face;
import com.moodswing.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by daniel on 04/03/17.
 * Some code in this class has been adapted/and or modified from https://github.com/Affectiva/affdexme-android with
 * permission via MIT license
 */

public class EmotionView extends SurfaceView implements SurfaceHolder.Callback {
    private final static String LOG_TAG = "MoodSwing";
    private Map<String, Bitmap> emojiMarkerBitmapToEmojiTypeMap;
    private SurfaceHolder surfaceHolder;
    private EmotionViewThread emotionViewThread;
    private EmotionThreadEventListener emotionThreadEventListener;

    public EmotionView(Context context) {
        super(context);
        initView();
    }

    public EmotionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public EmotionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private static int getDrawable(@NonNull Context context, @NonNull String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public void setEventListener(EmotionThreadEventListener emotionThreadEventListener) {
        this.emotionThreadEventListener = emotionThreadEventListener;

        if (emotionViewThread != null) {
            emotionViewThread.setEventListener(emotionThreadEventListener);
        }
    }

    void initView() {
        surfaceHolder = getHolder(); //The SurfaceHolder object will be used by the thread to request canvas to draw on SurfaceView
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT); //set to Transparent so this surfaceView does not obscure the one it is overlaying (the one displaying the camera).
        surfaceHolder.addCallback(this); //become a Listener to the three events below that SurfaceView generates

        emotionViewThread = new EmotionViewThread(surfaceHolder, emotionThreadEventListener);

        //statically load the emoji bitmaps on-demand and cache
        emojiMarkerBitmapToEmojiTypeMap = new HashMap<>();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (emotionViewThread.isStopped()) {
            emotionViewThread = new EmotionViewThread(surfaceHolder, emotionThreadEventListener);
        }
        emotionViewThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //command thread to stop, and wait until it stops
        boolean retry = true;
        emotionViewThread.stopThread();
        while (retry) {
            try {
                emotionViewThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        cleanup();
    }

    private void cleanup() {
        if (emojiMarkerBitmapToEmojiTypeMap != null) {
            for (Bitmap bitmap : emojiMarkerBitmapToEmojiTypeMap.values()) {
                bitmap.recycle();
            }
            emojiMarkerBitmapToEmojiTypeMap.clear();
        }
    }

    public void updatePoints(List<Face> faces) {
        emotionViewThread.updatePoints(faces);
    }

    public void invalidatePoints() {
        emotionViewThread.invalidatePoints();
    }

    public void requestBitmap() {
        if (emotionThreadEventListener == null) {
            String msg = "Attempted to request screenshot without first attaching event listener";
            Log.e(LOG_TAG, msg);
            return;
        }
        if (emotionViewThread == null || emotionViewThread.isStopped()) {
            String msg = "Attempted to request screenshot without a running drawing thread";
            Log.e(LOG_TAG, msg);
            return;
        }
        emotionViewThread.requestCaptureBitmap = true;
    }

    class FacesSharer {
        List<Face> facesToDraw;

        public FacesSharer() {
            facesToDraw = new ArrayList<>();
        }
    }

    public interface EmotionThreadEventListener {
        void onBitmapGenerated(Bitmap bitmap, String currentEmoji);
    }

    private class EmotionViewThread extends Thread {
        private final FacesSharer facesSharer;
        private final SurfaceHolder surfaceHolder;
        private volatile boolean stopFlag = false; //boolean to indicate when thread has been told to stop
        private volatile boolean requestCaptureBitmap = false; //boolean to indicate a snapshot of the surface has been requested
        private EmotionThreadEventListener emotionThreadEventListener;
        private GifDrawable currentAnimation;
        private String currentEmoji = "UNKNOWN";
        private int currentAnimationResource = -1;

        public EmotionViewThread(SurfaceHolder surfaceHolder, EmotionThreadEventListener emotionThreadEventListener) {
            this.surfaceHolder = surfaceHolder;
            this.emotionThreadEventListener = emotionThreadEventListener;
            this.facesSharer = new FacesSharer();
        }

        public void setEventListener(EmotionThreadEventListener emotionEventThreadListener) {
            this.emotionThreadEventListener = emotionEventThreadListener;
        }

        public boolean isStopped() {
            return stopFlag;
        }

        public void stopThread() {
            stopFlag = true;
        }

        //Updates thread with latest faces returned by the onImageResults() event.
        public void updatePoints(List<Face> faces) {
            synchronized (facesSharer) {
                facesSharer.facesToDraw.clear();
                if (faces != null) {
                    facesSharer.facesToDraw.addAll(faces);
                }
            }
        }

        public void invalidatePoints() {
            synchronized (facesSharer) {
                facesSharer.facesToDraw.clear();
            }
        }


        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            while (!stopFlag) {

                /**
                 * We use SurfaceHolder.lockCanvas() to get the canvas that draws to the SurfaceView.
                 * After we are done drawing, we let go of the canvas using SurfaceHolder.unlockCanvasAndPost()
                 * **/
                Canvas c = null;
                Canvas screenshotCanvas = null;
                Bitmap screenshotBitmap = null;
                try {
                    c = surfaceHolder.lockCanvas();

                    if (requestCaptureBitmap) {
                        Rect surfaceBounds = surfaceHolder.getSurfaceFrame();
                        screenshotBitmap = Bitmap.createBitmap(surfaceBounds.width(), surfaceBounds.height(), Bitmap.Config.ARGB_8888);
                        screenshotCanvas = new Canvas(screenshotBitmap);
                        requestCaptureBitmap = false;
                    }

                    if (c != null) {
                        synchronized (surfaceHolder) {
                            c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //clear
                            draw(c, screenshotCanvas);
                        }
                    }

                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                    if (screenshotBitmap != null && emotionThreadEventListener != null) {
                        emotionThreadEventListener.onBitmapGenerated(Bitmap.createBitmap(screenshotBitmap), currentEmoji);
                        screenshotBitmap.recycle();
                    }
                }
            }
        }

        void draw(@NonNull Canvas c, @Nullable Canvas c2) {
            Face nextFaceToDraw;
            int index = 0;

            synchronized (facesSharer) {
                if (facesSharer.facesToDraw.isEmpty()) {
                    nextFaceToDraw = null;
                } else {
                    nextFaceToDraw = facesSharer.facesToDraw.get(index);
                    index++;
                }
            }

            while (nextFaceToDraw != null) {
                drawFaceAttributes(c, nextFaceToDraw);

                if (c2 != null) {
                    drawFaceAttributes(c2, nextFaceToDraw);
                }

                synchronized (facesSharer) {
                    if (index < facesSharer.facesToDraw.size()) {
                        nextFaceToDraw = facesSharer.facesToDraw.get(index);
                        index++;
                    } else {
                        nextFaceToDraw = null;
                    }
                }
            }
        }

        private void drawFaceAttributes(Canvas c, Face nextFaceToDraw) {
            // Record current emoji
            currentEmoji = nextFaceToDraw.emojis.getDominantEmoji().name();
            // Draw the Emoji markers
            drawDominantEmoji(c, nextFaceToDraw);
            drawEmotionProgressCircle(c, getDominantEmojiScore(nextFaceToDraw));
            drawEmotionAnimation(c, nextFaceToDraw);
        }

        private float getDominantEmojiScore(Face face) {
            String dominantEmoji = face.emojis.getDominantEmoji().name();
            switch (dominantEmoji) {
                case "RELAXED":
                    return face.emojis.getRelaxed();
                case "SMILEY":
                    return face.emojis.getSmiley();
                case "LAUGHING":
                    return face.emojis.getLaughing();
                case "WINK":
                    return face.emojis.getWink();
                case "SMIRK":
                    return face.emojis.getSmirk();
                case "KISSING":
                    return face.emojis.getKissing();
                case "STUCK_OUT_TONGUE":
                    return face.emojis.getStuckOutTongue();
                case "STUCK_OUT_TONGUE_WINKING_EYE":
                    return face.emojis.getStuckOutTongueWinkingEye();
                case "DISAPPOINTED":
                    return face.emojis.getDisappointed();
                case "RAGE":
                    return face.emojis.getRage();
                case "SCREAM":
                    return face.emojis.getScream();
                case "FLUSHED":
                    return face.emojis.getFlushed();
                default: // UNKNOWN
                    return -1;
            }
        }

        private void drawEmotionProgressCircle(Canvas c, float dominantEmojiScore) {
            if (dominantEmojiScore == -1) {
                return;
            }

            Paint paint = new Paint();

            paint.setStrokeWidth(10);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor("#FFDB4C"));
            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.FILL);
            final RectF oval = new RectF();
            paint.setStyle(Paint.Style.STROKE);
            oval.set(10, 10, 290, 290);


            int xPadding = (int) getResources().getDimension(R.dimen.xEmotionCirlePadding);
            int yPadding = (int) getResources().getDimension(R.dimen.yEmotionCirclePadding);
            c.save();
            c.translate(c.getWidth() - oval.centerX() - dpToPx(xPadding), c.getHeight() - oval.centerY() - dpToPx(yPadding));
            c.drawArc(oval, 270, ((dominantEmojiScore * 360) / 100), false, paint);
            c.restore();
        }

        private void drawDominantEmoji(Canvas c, Face f) {
            drawEmojiFromCache(c, f.emojis.getDominantEmoji().name());
        }

        private void drawEmojiFromCache(Canvas c, String emojiName) {
            Bitmap emojiBitmap;
            float markerPosX;
            float markerPosY;
            int xPadding;
            int yPadding;

            try {
                emojiBitmap = getEmojiBitmapByName(emojiName);
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, "Error, file not found!", e);
                return;
            }

            if (emojiBitmap != null) {
                xPadding = (int) getResources().getDimension(R.dimen.xEmojiPadding);
                yPadding = (int) getResources().getDimension(R.dimen.yEmojiPadding);

                markerPosX = c.getWidth() - emojiBitmap.getWidth() - dpToPx(xPadding);
                markerPosY = c.getHeight() - emojiBitmap.getHeight() - dpToPx(yPadding);
                c.drawBitmap(emojiBitmap, markerPosX, markerPosY, null);
            }
        }

        private Bitmap getEmojiBitmapByName(String emojiName) throws FileNotFoundException {
            // Use blank emoji when no emoji is recognized
            if (emojiName.equals(Face.EMOJI.UNKNOWN.name())) {
                emojiName = "blank";
            }

            String emojiResourceName = emojiName.trim().replace(' ', '_').toLowerCase(Locale.US).concat("_emoji");
            String emojiFileName = emojiResourceName + ".png";

            //Try to get the emoji from the cache
            Bitmap desiredEmojiBitmap = emojiMarkerBitmapToEmojiTypeMap.get(emojiFileName);

            if (desiredEmojiBitmap != null) {
                //emoji bitmap found in the cache
                return desiredEmojiBitmap;
            }

            //Cache miss, try and load the bitmap from disk
            desiredEmojiBitmap = ImageHelper.loadBitmapFromInternalStorage(getContext(), emojiFileName);

            if (desiredEmojiBitmap != null) {
                //emoji bitmap found in the app storage


                //Bitmap loaded, add to cache for subsequent use.
                emojiMarkerBitmapToEmojiTypeMap.put(emojiFileName, desiredEmojiBitmap);

                return desiredEmojiBitmap;
            }

            Log.d(LOG_TAG, "Emoji not found on disk: " + emojiFileName);

            //Still unable to find the file, try to locate the emoji resource
            final int resourceId = getDrawable(getContext(), emojiFileName);

            if (resourceId == 0) {
                //unrecognised emoji file name
                throw new FileNotFoundException("Resource not found for file named: " + emojiFileName);
            }

            desiredEmojiBitmap = BitmapFactory.decodeResource(getResources(), resourceId);

            if (desiredEmojiBitmap == null) {
                //still unable to load the resource from the file
                throw new FileNotFoundException("Resource id [" + resourceId + "] but could not load bitmap: " + emojiFileName);
            }

            //Bitmap loaded, add to cache for subsequent use.
            emojiMarkerBitmapToEmojiTypeMap.put(emojiFileName, desiredEmojiBitmap);

            return desiredEmojiBitmap;
        }

        private void drawEmotionAnimation(Canvas c, Face face) {
            String dominantEmoji = face.emojis.getDominantEmoji().name();
            int nextAnimationResource = -1;
            switch (dominantEmoji) {
                case "RELAXED":
                    nextAnimationResource = R.drawable.relaxed_animation;
                    break;
                case "SMILEY":
                    nextAnimationResource = R.drawable.smiley_animation;
                    break;
                case "LAUGHING":
                    nextAnimationResource = R.drawable.laughing_animation;
                    break;
                case "WINK":
                    nextAnimationResource = R.drawable.wink_animation;
                    break;
                case "SMIRK":
                    nextAnimationResource = R.drawable.smirk_animation;
                    break;
                case "KISSING":
                    nextAnimationResource = R.drawable.kissing_animation;
                    break;
                case "STUCK_OUT_TONGUE":
                    nextAnimationResource = R.drawable.stuck_out_tongue_animation;
                    break;
                case "STUCK_OUT_TONGUE_WINKING_EYE":
                    nextAnimationResource = R.drawable.tonguewink_animation;
                    break;
                case "DISAPPOINTED":
                    nextAnimationResource = R.drawable.disappointed_animation;
                    break;
                case "RAGE":
                    nextAnimationResource = R.drawable.rage_animation;
                    break;
                case "SCREAM":
                    nextAnimationResource = R.drawable.scream_animation;
                    break;
                case "FLUSHED":
                    nextAnimationResource = R.drawable.flushed_animation;
                    break;
                default:
                    nextAnimationResource = -1;
            }
            playGIF(nextAnimationResource, c);
        }

        private void playGIF(int nextAnimationResource, Canvas c) {
            if (currentAnimationResource != nextAnimationResource) { // we are changing GIFs
                if (currentAnimation != null) {
                    currentAnimation.recycle();
                }
                currentAnimationResource = nextAnimationResource;
                if (currentAnimationResource != -1) {
                    try {
                        currentAnimation = new GifDrawable(getResources(), currentAnimationResource);
                        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
                        currentAnimation.setBounds(width / 4, 0, width * 3 / 4, height / 2);
                        currentAnimation.draw(c);
                    } catch (IOException e) {
                        Log.v(LOG_TAG, e.getMessage());
                    }
                }
            } else if (currentAnimationResource != -1) { // not changing GIFs
                currentAnimation.draw(c);
            }
        }

        private int dpToPx(int dp) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int)((dp * displayMetrics.density) + 0.5);
        }

        private int pxToDp(int px) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) ((px/displayMetrics.density)+0.5);
        }
    }
}
