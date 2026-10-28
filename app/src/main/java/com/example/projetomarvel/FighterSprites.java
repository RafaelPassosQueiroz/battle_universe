//ALternativa Png
package com.example.projetomarvel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.HashMap;

/** Uses the supplied character drawings directly, with only their screenshot backdrop removed. */
final class FighterSprites {
    private static Context app;
    private static final HashMap<Integer, Bitmap> cache = new HashMap<>();
    private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    static void initialize(Context context) { app = context.getApplicationContext(); }

    private static int resource(String hero) {
        if (hero == null) return 0;
        if (hero.contains("Aranha")) return R.drawable.fighter_spider_man;
        if (hero.contains("Ferro")) return R.drawable.fighter_iron_man;
        if (hero.contains("Wolverine")) return R.drawable.fighter_wolverine;
        if (hero.contains("Capitão América")) return R.drawable.fighter_captain_america;
        if (hero.equals("Thor")) return R.drawable.fighter_thor;
        if (hero.equals("Hulk")) return R.drawable.fighter_hulk;
        if (hero.contains("Pantera")) return R.drawable.fighter_black_panther;
        if (hero.contains("Estranho")) return R.drawable.fighter_doctor_strange;
        if (hero.contains("Viúva")) return R.drawable.fighter_black_widow;
        if (hero.contains("Escarlate")) return R.drawable.fighter_scarlet_witch;
        if (hero.contains("Capitã Marvel")) return R.drawable.fighter_captain_marvel;
        if (hero.contains("Doom")) return R.drawable.fighter_doctor_doom;
        if (hero.contains("Thanos")) return R.drawable.fighter_thanos;
        return 0;
    }

    static boolean available(String hero) { return app != null && resource(hero) != 0; }

    private static Bitmap bitmap(String hero) {
        int id = resource(hero);
        if (app == null || id == 0) return null;
        Bitmap ready = cache.get(id);
        if (ready != null) return ready;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inSampleSize = 2;
        Bitmap source = BitmapFactory.decodeResource(app.getResources(), id, options);
        if (source == null) return null;
        Bitmap cutout = removeBackdrop(source, id == R.drawable.fighter_doctor_doom);
        source.recycle();
        cache.put(id, cutout);
        return cutout;
    }

    private static Bitmap removeBackdrop(Bitmap source, boolean doom) {
        int width = source.getWidth(), height = source.getHeight(), size = width * height;
        int[] pixels = new int[size];
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        int key = pixels[Math.min(height - 1, 20) * width + width / 2];
        int edge = pixels[0];
        boolean[] visited = new boolean[size];
        int[] queue = new int[size];
        int head = 0, tail = 0;
        for (int x = 0; x < width; x++) {
            int top = x, bottom = (height - 1) * width + x;
            if (background(pixels[top], key, edge, doom) && !visited[top]) {
                visited[top] = true; queue[tail++] = top;
            }
            if (background(pixels[bottom], key, edge, doom) && !visited[bottom]) {
                visited[bottom] = true; queue[tail++] = bottom;
            }
        }
        for (int y = 0; y < height; y++) {
            int left = y * width, right = left + width - 1;
            if (background(pixels[left], key, edge, doom) && !visited[left]) {
                visited[left] = true; queue[tail++] = left;
            }
            if (background(pixels[right], key, edge, doom) && !visited[right]) {
                visited[right] = true; queue[tail++] = right;
            }
        }
        while (head < tail) {
            int index = queue[head++];
            pixels[index] = 0;
            int x = index % width;
            for (int side = 0; side < 4; side++) {
                int next = side == 0 ? index - width : side == 1 ? index + width
                        : side == 2 ? (x > 0 ? index - 1 : -1)
                        : (x < width - 1 ? index + 1 : -1);
                if (next >= 0 && next < size && !visited[next]
                        && background(pixels[next], key, edge, doom)) {
                    visited[next] = true; queue[tail++] = next;
                }
            }
        }
        // Small background stars in the supplied Doom illustration are separate from the hero.
        // Keep accessories such as the shield and hammer, which occupy larger components.
        java.util.Arrays.fill(visited, false);
        for (int start = 0; start < size; start++) {
            if (visited[start] || (pixels[start] >>> 24) <= 16) continue;
            head = tail = 0;
            visited[start] = true;
            queue[tail++] = start;
            while (head < tail) {
                int index = queue[head++], x = index % width;
                for (int side = 0; side < 4; side++) {
                    int next = side == 0 ? index - width : side == 1 ? index + width
                            : side == 2 ? (x > 0 ? index - 1 : -1)
                            : (x < width - 1 ? index + 1 : -1);
                    if (next >= 0 && next < size && !visited[next]
                            && (pixels[next] >>> 24) > 16) {
                        visited[next] = true;
                        queue[tail++] = next;
                    }
                }
            }
            if (tail < 32) for (int i = 0; i < tail; i++) pixels[queue[i]] = 0;
        }
        int minX = width, minY = height, maxX = 0, maxY = 0;
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
            if ((pixels[y * width + x] >>> 24) > 16) {
                minX = Math.min(minX, x); minY = Math.min(minY, y);
                maxX = Math.max(maxX, x); maxY = Math.max(maxY, y);
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        if (minX <= maxX && minY <= maxY) {
            if (minX == 0 && minY == 0 && maxX == width - 1 && maxY == height - 1)
                return result;
            Bitmap cropped = Bitmap.createBitmap(result, minX, minY,
                    maxX - minX + 1, maxY - minY + 1);
            result.recycle();
            return cropped;
        }
        return result;
    }

    private static boolean background(int pixel, int key, int edge, boolean doom) {
        int r = (pixel >> 16) & 255, g = (pixel >> 8) & 255, b = pixel & 255;
        if (doom) return r < 65 && g < 84 && b < 125 && b > r;
        return distance(pixel, key) < 48 || distance(pixel, edge) < 32;
    }

    private static int distance(int a, int b) {
        int dr = ((a >> 16) & 255) - ((b >> 16) & 255);
        int dg = ((a >> 8) & 255) - ((b >> 8) & 255);
        int db = (a & 255) - (b & 255);
        return Math.abs(dr) + Math.abs(dg) + Math.abs(db);
    }

    static boolean drawPortrait(Canvas canvas, int width, int height, String hero,
                                boolean mirror) {
        Bitmap bitmap = bitmap(hero);
        if (bitmap == null) return false;
        float margin = Math.max(6f, Math.min(width, height) * .045f);
        float fit = Math.min((width - margin * 2) / bitmap.getWidth(),
                (height - margin * 2) / bitmap.getHeight());
        float w = bitmap.getWidth() * fit, h = bitmap.getHeight() * fit;
        canvas.save();
        if (mirror && !hero.contains("Doom")) canvas.scale(-1, 1, width / 2f, height / 2f);
        float left = (width - w) / 2f, top = (height - h) / 2f;
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new RectF(left, top, left + w, top + h), paint);
        canvas.restore();
        return true;
    }

    static boolean drawBust(Canvas canvas,float cx,float cy,float side,String hero) {
        Bitmap bitmap=bitmap(hero);
        if(bitmap==null)return false;
        int sourceHeight=Math.max(1,Math.round(bitmap.getHeight()*.54f));
        float imageAspect=(float)bitmap.getWidth()/sourceHeight;
        float drawHeight=side*1.12f;
        float drawWidth=drawHeight*imageAspect;
        if(drawWidth<side) {
            drawWidth=side;
            drawHeight=drawWidth/imageAspect;
        }
        canvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),sourceHeight),
                new RectF(cx-drawWidth/2f,cy-drawHeight*.52f,
                        cx+drawWidth/2f,cy+drawHeight*.48f),paint);
        return true;
    }

    static boolean drawBattle(Canvas canvas, float x, float feet, float scale,
                              String hero, boolean mirror, int pose, float phase) {
        Bitmap bitmap = bitmap(hero);
        if (bitmap == null) return false;
        float height = 190f * scale;
        float width = height * bitmap.getWidth() / bitmap.getHeight();
        canvas.save();
        canvas.translate(x, feet);
        if (mirror && !hero.contains("Doom")) canvas.scale(-1f, 1f);
        if (pose == FighterArtView.POSE_KO) canvas.rotate(-68f, 0, -height * .38f);
        else if (pose == FighterArtView.POSE_HIT) canvas.rotate(-7f, 0, -height * .4f);
        else if (pose == FighterArtView.POSE_PUNCH || pose == FighterArtView.POSE_KICK
                || pose == FighterArtView.POSE_BLAST)
            canvas.translate(7f * (float) Math.sin(Math.PI * Math.min(1f, phase)), 0);
        else if (pose == FighterArtView.POSE_WALK)
            canvas.translate(0, -2f * Math.abs((float) Math.sin(phase * 11f)));
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new RectF(-width / 2f, -height, width / 2f, 0), paint);
        canvas.restore();
        return true;
    }

    private FighterSprites() { }
}
