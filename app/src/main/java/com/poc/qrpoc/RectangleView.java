package com.poc.qrpoc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Natalia on 10/04/2019.
 */

public class RectangleView extends View {
    Paint paint = new Paint();
    Point[] points;

    public RectangleView(Context context) {
        super(context);
    }

    RectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    RectangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        Path wallpath = new Path();
        wallpath.reset();
        if(points != null) {
            wallpath.moveTo(points[0].x, points[0].y);
            wallpath.lineTo(points[1].x, points[1].y);
            wallpath.lineTo(points[2].x, points[2].y);
            wallpath.lineTo(points[3].x, points[3].y);
            wallpath.lineTo(points[0].x, points[0].y);
        }
        canvas.drawPath(wallpath, paint);
    }
}
