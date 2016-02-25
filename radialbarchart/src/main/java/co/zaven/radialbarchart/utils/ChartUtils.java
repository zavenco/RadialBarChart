package co.zaven.radialbarchart.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * This class contains helper methods.
 * <p/>
 * Created on 18.02.2016.
 *
 * @author Sławomir Onyszko
 */
public class ChartUtils {

    private ChartUtils() {
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method setting layer type to sowftware.
     *
     * @param view view which would be set.
     */
    public static void setLayerToSW(View view) {
        if (!view.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    /**
     * This method normalize specified vector.
     *
     * @param point {@link PointF}
     */
    public static void normalizeVector(PointF point) {
        final float abs = point.length();
        point.set(point.x / abs, point.y / abs);
    }

    /**
     * Calculates angle of touched point.
     *
     * @param x       x touch coordinate
     * @param y       y touch coordinate.
     * @param centerX x center coordinate.
     * @param centerY y center coordinate.
     * @return method returns calculated touch angle.
     */
    public static float pointToAngle(float x, float y, float centerX, float centerY) {
        double diffX = x - centerX;
        double diffY = y - centerY;
        // Pass -diffX to get clockwise degrees order.
        double radian = Math.atan2(-diffX, diffY);

        float angle = ((float) Math.toDegrees(radian) + 360) % 360;
        // Add 90 because atan2 returns 0 degrees at 6 o'clock.
        angle += 90f;
        return angle;
    }

    /**
     * Helper method for translating (x,y) scroll vectors into scalar rotation of the slice.
     *
     * @param dx The x component of the current scroll vector.
     * @param dy The y component of the current scroll vector.
     * @param x  The x position of the current touch, relative to the slice center.
     * @param y  The y position of the current touch, relative to the slice center.
     * @return The scalar representing the change in angular position for this scroll.
     */
    public static float vectorToScalarScroll(float dx, float dy, float x, float y) {
        // get the length of the vector
        float l = (float) Math.sqrt(dx * dx + dy * dy);

        // decide if the scalar should be negative or positive by finding
        // the dot product of the vector perpendicular to (x,y).
        float crossX = -y;
        float crossY = x;

        float dot = (crossX * dx + crossY * dy);
        float sign = Math.signum(dot);

        return l * sign;
    }

}
