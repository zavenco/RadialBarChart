package co.zaven.radialbarchart.models;

/**
 * Model that describes every slice in radial bar chart.
 * <p/>
 * Created on 19.02.2016.
 *
 * @author Sławomir Onyszko
 */
public class RadialBarChartModel extends BaseModel {

    /**
     * This is text label of slice in radial bar chart.
     */
    private String label;

    /**
     * This is percentage value of slice.
     */
    private float percent;

    /**
     * This is the slice weight.
     * This value sets slice width.
     */
    private int sliceWeight;

    /**
     * This is slice color value.
     */
    private int color;

    /**
     * Constructor with parameters.
     *
     * @param label       the text label of slice in radial bar chart.
     * @param percent     the percentage value of slice.
     * @param sliceWeight the slice weight.
     */
    public RadialBarChartModel(String label, float percent, int sliceWeight) {
        this.label = label;
        this.percent = percent;
        this.sliceWeight = sliceWeight;
    }

    /**
     * Constructor with parameters.
     *
     * @param label       the text label of slice in radial bar chart.
     * @param percent     the percentage value of slice.
     * @param sliceWeight the slice weight.
     * @param color       the slice color value.
     */
    public RadialBarChartModel(String label, float percent, int sliceWeight, int color) {
        this.label = label;
        this.percent = percent;
        this.sliceWeight = sliceWeight;
        this.color = color;
    }

    /**
     * This method gets slice label.
     *
     * @return the slice text label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * This method sets slice label.
     *
     * @param label the slice text label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * This method gets percentage slice value.
     *
     * @return the percentage slice value.
     */
    public float getPercent() {
        return percent;
    }

    /**
     * This method sets percentage slice value.
     *
     * @param percent the percentage slice value.
     */
    public void setPercent(float percent) {
        this.percent = percent;
    }

    /**
     * This method gets slice weight.
     *
     * @return the slice weight.
     */
    public int getSliceWeight() {
        return sliceWeight;
    }

    /**
     * This method sets slice weight.
     *
     * @param sliceWeight the slice weight.
     */
    public void setSliceWeight(int sliceWeight) {
        this.sliceWeight = sliceWeight;
    }

    /**
     * This method gets slice color.
     *
     * @return the slice color.
     */
    public int getColor() {
        return color;
    }

    /**
     * This method sets slice color.
     *
     * @param color the slice color.
     */
    public void setColor(int color) {
        this.color = color;
    }

}
