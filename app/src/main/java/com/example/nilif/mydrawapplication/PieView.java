package com.example.nilif.mydrawapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nilif
 * @date 2016/9/19 14:26
 */
public class PieView extends View{

    private Context context;
    private DecimalFormat format;
    private List<BaseMessage> mList;

    private Paint arcPaint; // 绘制圆环的画笔
    private Paint linePaint; // 绘制直线的画笔
    private Paint textPaint; // 绘制文字的画笔

    private float centerX; // 圆心的x坐标
    private float centerY; // 圆心的y坐标
    private float radius; // 半径
    private float total;
    private float startAngle; // 起始角度
    private float textAngle;
    private float roundAngle;
    private boolean isAddText = false;

    private List<PointF[]> lineList;
    private List<PointF> textList;
    private Handler mHandler;
    


    public PieView(Context context) {
        this(context, null);
    }

    public PieView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public PieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.mHandler = new MyHandler(this);
        this.mList = new ArrayList<>();
        this.lineList = new ArrayList<>();
        this.textList = new ArrayList<>();
        this.format = new DecimalFormat("##0.00");

        this.arcPaint = new Paint();
        this.arcPaint.setAntiAlias(true);
        this.arcPaint.setDither(true);
        this.arcPaint.setStyle(Paint.Style.STROKE); // 设置画笔为空心

        this.linePaint = new Paint();
        this.linePaint.setAntiAlias(true);
        this.linePaint.setDither(true);
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setStrokeWidth(dip2px(context, 2)); // 设置空心线宽
        this.linePaint.setColor(Color.parseColor("#FFFFFF"));

        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setDither(true);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setColor(Color.parseColor("#FFFFFF"));
    }

    private int dip2px(Context context, int i) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (i * scale + 0.5f);
    }

    /**
     * 测量View的大小
     * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
            width = Math.min(heightSpecSize,Math.min(getScreenSize(context)[0],getScreenSize(context)[1]));
        }else if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = Math.min(widthSpecSize, Math.min(getScreenSize(context)[0], getScreenSize(context)[1]));
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            width = height = Math.min(getScreenSize(context)[0], getScreenSize(context)[1]);
        } else {
            width = widthSpecSize;
            height = heightSpecSize;
        }
        setMeasuredDimension(width,height);

    }

    private static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }


    /**
     * 确定View的大小
     * */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(centerX,centerY) * 0.725f;
        arcPaint.setStrokeWidth(radius /3 *2);
        textPaint.setTextSize(radius / 7);
    }

    /**
     * 绘制图形
     * */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textList.clear();
        lineList.clear();
        lineList = new ArrayList<>();
        textList = new ArrayList<>();

        if (mList != null) {
            RectF mRectF = new RectF(centerX - radius, centerY - radius, centerX + radius,centerY + radius);
            for (int i = 0; i<mList.size(); i++) {
                arcPaint.setColor(mList.get(i).color);
                canvas.drawArc(mRectF,startAngle,mList.get(i).percent / total * roundAngle,false,arcPaint);
                if (isAddText) {
                    lineList.add(getLinePointFs(startAngle));
                    textAngle = startAngle + mList.get(i).percent / total * roundAngle / 2;
                    textList.add(getTextPointF(textAngle));
                }
                startAngle +=mList.get(i).percent / total * roundAngle;
            }
        }

        if (roundAngle < 360f) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    roundAngle += 10f;
                    if (roundAngle == 360f - 10f) {
                        isAddText = true;
                    }
                    postInvalidate();
                }
            },50);
        }else {
            drawSpaceLine(canvas,lineList);
            draeText(canvas);
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
        }

    }

    private void draeText(Canvas canvas) {
        for (int i = 0; i < textList.size(); i++) {
            textPaint.setTextAlign(Paint.Align.CENTER);
            String text = mList.get(i).content;
            canvas.drawText(text,textList.get(i).x,textList.get(i).y,textPaint);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            canvas.drawText(format.format(mList.get(i).percent * 100 / total) + "%",
                    textList.get(i).x,textList.get(i).y + (fm.descent - fm.ascent),
                    textPaint);
        }

    }

    private void drawSpaceLine(Canvas canvas, List<PointF[]> lineList) {
        for (PointF[] pf : lineList) {
            canvas.drawLine(pf[0].x,pf[0].y,pf[1].x,pf[1].y,linePaint);
        }
    }


    private PointF getTextPointF(float textAngle) {
        float textPointX = (float) (centerX + radius * Math.cos(Math.toRadians(textAngle)));
        float textPointY = (float) (centerY + radius * Math.sin(Math.toRadians(textAngle)));
        return new PointF(textPointX,textPointY);
    }

    private PointF[] getLinePointFs(float startAngle) {
        float stopX = (float) (centerX + (radius + arcPaint.getStrokeWidth() / 2 ) * Math.cos(Math.toRadians(startAngle)));
        float stopY = (float) (centerY + (radius + arcPaint.getStrokeWidth() / 2) * Math.sin(Math.toRadians(startAngle)));
        float startX = (float) (centerX + (radius - arcPaint.getStrokeWidth() / 2) * Math.cos(Math.toRadians(startAngle)));
        float startY = (float) (centerY + (radius - arcPaint.getStrokeWidth() / 2) * Math.sin(Math.toRadians(startAngle)));
        PointF startPoint = new PointF(startX, startY);
        PointF stopPoint = new PointF(stopX, stopY);
        return new PointF[]{startPoint, stopPoint};
    }

    /**
     * 设置文字颜色
     * */
    public void setTextColor(int color) {
        textPaint.setColor(color);
    }

    /**
     * 设置线的颜色
     * */
    public void setLineColor(int color) {
        linePaint.setColor(color);
    }

    /**
     * 设置起始角度
     * */
    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    /**
     * 设置饼的数据
     * */
    public void setPieData(List<BaseMessage> mList) {
        total = 0;
        for (int i = 0; i< mList.size(); i++) {
            total += mList.get(i).percent;
        }
        this.mList.clear();
        this.mList = mList;
        invalidate();
    }

    private class MyHandler extends Handler {
        private WeakReference<PieView> activityWeakReference;
        public MyHandler(PieView pieView) {
            activityWeakReference = new WeakReference<PieView>(pieView);
        }

        @Override
        public void handleMessage(Message msg) {
            PieView pieView = activityWeakReference.get();
            if (pieView == null) {
                return;
            }
        }
    }
}
