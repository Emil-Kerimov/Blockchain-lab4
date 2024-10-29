package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InvestigatingDoubleSpendAttack {

    public static int calculateMinConfirmations(double q, double threshold) {
        double p = 1.0 - q;
        int z = 0;

        while (true) {
            double lambda = z * (q / p);
            double sum = 1.0;
            for (int k = 0; k <= z; k++) {
                double poisson = Math.exp(-lambda);
                for (int i = 1; i <= k; i++) {
                    poisson *= lambda / i;
                }
                sum -= poisson * (1 - Math.pow(q / p, z - k));
            }
            if (Math.abs(sum) < threshold) {
                return z;
            }
            z++;
        }
    }

    public static void main(String[] args) {
        double[] thresholds = {1e-3, 1e-4, 1e-5};
        double step = 0.05;
        List<Double> qValues = new ArrayList<>();
        List<List<Integer>> results = new ArrayList<>();

        for (double q = 0.1; q <= 0.45; q += step) {
            qValues.add(q);
        }

        for (double threshold : thresholds) {
            List<Integer> confirmations = new ArrayList<>();
            for (double q : qValues) {
                int z = calculateMinConfirmations(q, threshold);
                confirmations.add(z);
                System.out.printf("Для q=%.2f і порогу=%.1e, мін кількість блоків підтвердження z=%d%n", q, threshold, z);
            }
            results.add(confirmations);
        }
        plotGraph(qValues, results, thresholds);
    }

    public static void plotGraph(List<Double> qValues, List<List<Integer>> results, double[] thresholds) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (int i = 0; i < thresholds.length; i++) {
            XYSeries series = new XYSeries("Поріг " + thresholds[i]);
            List<Integer> confirmations = results.get(i);

            for (int j = 0; j < qValues.size(); j++) {
                series.add(qValues.get(j), confirmations.get(j));
            }
            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Графік залежності",
                "Частка зловмисників",
                "Кількість блоків підтвердження ",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        JFrame frame = new JFrame("Дослідження атаки подвійної витрати");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart), BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
