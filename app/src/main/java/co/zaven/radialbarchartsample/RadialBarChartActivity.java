package co.zaven.radialbarchartsample;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import co.zaven.radialbarchart.charts.RadialBarChartView;
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
        data.add(new RadialBarChartModel("Blue", 45, 2, res.getColor(co.zaven.radialbarchart.R.color.md_blue_400)));
        data.add(new RadialBarChartModel("Red", 75, 2, res.getColor(co.zaven.radialbarchart.R.color.md_red_400)));
        data.add(new RadialBarChartModel("Orange", 55, 2, res.getColor(co.zaven.radialbarchart.R.color.md_orange_400)));
        data.add(new RadialBarChartModel("Yellow", 30, 2, res.getColor(co.zaven.radialbarchart.R.color.md_yellow_400)));
        data.add(new RadialBarChartModel("Teal", 90, 2, res.getColor(co.zaven.radialbarchart.R.color.md_teal_400)));
        radialBarChartView.setData(data);
    }


}
