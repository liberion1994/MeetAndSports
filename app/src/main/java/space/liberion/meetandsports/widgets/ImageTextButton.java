package space.liberion.meetandsports.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import space.liberion.meetandsports.R;

/**
 * Created by apple on 16/3/25.
 */
public class ImageTextButton extends View {

    private static final ColorMatrix chosenColorMatrix = new ColorMatrix(new float[] {
        0, 0, 0, 0, 0,// 红色值
        0, 0, 0, 0, 127,// 绿色值
        0, 0, 0, 0, 255,// 蓝色值
        0, 0, 0, 1, 0 // 透明度
    });

    private Paint paint;
    private Paint chosenPaint;
    private Bitmap targetPic;
    private int imageId = -1;
    private String text;
    private int picTop = 0;
    private int picLeft = 0;
    private float fontSize = 20;
    private int baseline = 0;
    private boolean chosen = false;


    public ImageTextButton(Context context) {
        super(context);
        init();
    }

    public ImageTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    public ImageTextButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageTextButton);
        text = typedArray.getString(R.styleable.ImageTextButton_text);
        imageId = typedArray.getResourceId(R.styleable.ImageTextButton_imageSrc, -1);
        chosen = typedArray.getBoolean(R.styleable.ImageTextButton_chosen, false);

        typedArray.recycle();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        chosenPaint = new Paint();
        chosenPaint.setAntiAlias(true);
        chosenPaint.setTextAlign(Paint.Align.CENTER);
        chosenPaint.setColorFilter(new ColorMatrixColorFilter(chosenColorMatrix));
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        int picHeight = (int)(h * 0.7);
        fontSize = (h - picHeight) * 0.8f;
        paint.setTextSize(fontSize);
        chosenPaint.setTextSize(fontSize);

        baseline = (int) (h - (h - picHeight) * 0.1);

        if (picHeight > w) {
            if (imageId != -1) {
                targetPic = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(getResources(), imageId), w, w, true);
            }
            picTop = (picHeight - w) / 2;
            picLeft = 0;
        } else {
            if (imageId != -1) {
                targetPic = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(getResources(), imageId), picHeight, picHeight, true);
            }
            picTop = 0;
            picLeft = (w - picHeight) / 2;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint targetPaint = chosen ? chosenPaint : paint;

        canvas.drawBitmap(targetPic, picLeft, picTop, targetPaint);
        canvas.drawText(text, getWidth() / 2, baseline, targetPaint);
    }
}
