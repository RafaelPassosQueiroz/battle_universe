//Manopla do Thanos
package com.example.projetomarvel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/** The existing gauntlet silhouette with only the earned stones illuminated. */
final class GauntletView extends View {
    private static final int[] STONE_COLORS = {
            0xff3d7bff, 0xffffd23f, 0xffff3b4d, 0xffa05cff, 0xff55da7d, 0xffff9a38
    };
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int unlocked;

    GauntletView(Context context, int unlocked) {
        super(context);
        this.unlocked = Math.max(0, Math.min(6, unlocked));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private Path polygon(float... coordinates) {
        Path path = new Path();
        path.moveTo(coordinates[0], coordinates[1]);
        for (int i = 2; i < coordinates.length; i += 2)
            path.lineTo(coordinates[i], coordinates[i + 1]);
        path.close();
        return path;
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        float scale = Math.min(getWidth() / 128f, getHeight() / 160f);
        canvas.translate((getWidth() - scale * 128f) / 2f,
                (getHeight() - scale * 160f) / 2f);
        canvas.scale(scale, scale);
        Path hand = new Path();
        hand.moveTo(37, 151);
        hand.lineTo(22, 116);
        hand.lineTo(15, 79);
        hand.quadTo(14, 71, 21, 69);
        hand.quadTo(28, 68, 31, 76);
        hand.lineTo(35, 88);
        hand.lineTo(30, 42);
        hand.quadTo(29, 33, 37, 32);
        hand.quadTo(45, 32, 46, 41);
        hand.lineTo(51, 75);
        hand.lineTo(51, 22);
        hand.quadTo(51, 13, 59, 13);
        hand.quadTo(67, 13, 67, 22);
        hand.lineTo(69, 73);
        hand.lineTo(73, 30);
        hand.quadTo(74, 21, 82, 22);
        hand.quadTo(90, 23, 89, 32);
        hand.lineTo(84, 76);
        hand.lineTo(92, 47);
        hand.quadTo(95, 38, 103, 41);
        hand.quadTo(111, 44, 108, 53);
        hand.lineTo(99, 91);
        hand.lineTo(108, 75);
        hand.quadTo(113, 68, 119, 73);
        hand.quadTo(125, 78, 120, 86);
        hand.lineTo(103, 121);
        hand.lineTo(94, 151);
        hand.close();
        paint.setColor(0xffb77a24);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(hand, paint);
        paint.setColor(0xfff6c85f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        canvas.drawPath(hand, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffe0a935);
        canvas.drawRect(38, 132, 94, 143, paint);
        paint.setColor(0xff805114);
        canvas.drawRect(40, 136, 92, 138, paint);
        float[] x = {43, 57, 71, 85, 49, 64};
        float[] y = {101, 97, 98, 101, 116, 115};
        for (int index = 0; index < 6; index++) {
            int color = index < unlocked ? STONE_COLORS[index] : 0xff35271c;
            if (index < unlocked) paint.setShadowLayer(6, 0, 0, color);
            paint.setColor(color);
            canvas.drawPath(polygon(x[index], y[index] - 7,
                    x[index] + 7, y[index],
                    x[index], y[index] + 7,
                    x[index] - 7, y[index]), paint);
            paint.clearShadowLayer();
            paint.setColor(index < unlocked ? 0xffffe49b : 0xff73532c);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.6f);
            canvas.drawCircle(x[index], y[index], 8, paint);
            paint.setStyle(Paint.Style.FILL);
        }
        canvas.restore();
    }
}
