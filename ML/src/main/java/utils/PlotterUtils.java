package utils;

import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.swing.*;


public class PlotterUtils {

    private XYSeriesCollection seriesCollection;

    public PlotterUtils() {
        seriesCollection = new XYSeriesCollection();
    }

    public void plotData(INDArray labels, INDArray predictions) {
        addSeriesToGraph(labels, "Labels");
        addSeriesToGraph(predictions, "Predictions");

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Regression cryptocurrencies",
                "Example",
                "Price",
                seriesCollection,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel panel = new ChartPanel(chart);

        displayWindow(panel);
    }

    private void displayWindow(ChartPanel panel) {
        JFrame window = new JFrame();
        window.add(panel);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();

        window.setVisible(true);
    }

    private void addSeriesToGraph(INDArray series, String seriesTag) {

        final double[] yd = series.data().asDouble();

        final double[] xd = new double[yd.length];
        for(int i = 0; i < yd.length; i++) {
            xd[i] = i+1;
        }

        XYSeries seriesToPlot = new XYSeries(seriesTag);



        for(int i = 0; i < yd.length; i++) {
            seriesToPlot.add(xd[i], yd[i]);
        }

        seriesCollection.addSeries(seriesToPlot);
    }

    public void addSeriesToChartUI(XYChart.Series<Integer, Double> seriesForChart, INDArray series) {
        final double[] seriesData = series.data().asDouble();

        for(int i = 0; i < seriesData.length; i++) {
            seriesForChart.getData().add(new XYChart.Data<>(i+1, seriesData[i]));
        }
    }
}
