package co.zaven.radialbarchartsample;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import co.zaven.radialbarchart.charts.RadialBarChartView;
import co.zaven.radialbarchart.entities.ChartDictionary;
import co.zaven.radialbarchart.models.RadialBarChartModel;

public class RadialBarChartActivity extends AppCompatActivity {

    RadialBarChartView radialBarChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radial_bar_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radialBarChartView = (RadialBarChartView) findViewById(R.id.chart);
        setupChartData();
    }

    private void setupChartData() {
        Resources res = getResources();
        radialBarChartView = (RadialBarChartView) findViewById(R.id.chart);
        ArrayList<RadialBarChartModel> data = new ArrayList<>();
        data.add(new RadialBarChartModel("bad", 10, 2, res.getColor(co.zaven.radialbarchart.R.color.chart_color_bad)));
        data.add(new RadialBarChartModel("not bad", 30, 2, res.getColor(co.zaven.radialbarchart.R.color.chart_color_not_bad)));
        data.add(new RadialBarChartModel("almost good", 50, 2, res.getColor(co.zaven.radialbarchart.R.color.chart_color_almost_good)));
        data.add(new RadialBarChartModel("good", 70, 2, res.getColor(co.zaven.radialbarchart.R.color.chart_color_good)));
        data.add(new RadialBarChartModel("excellent", 100, 2, res.getColor(co.zaven.radialbarchart.R.color.chart_color_excellent)));
        radialBarChartView.setData(data);
    }


}
