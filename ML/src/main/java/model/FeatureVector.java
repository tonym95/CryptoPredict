package model;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FeatureVector {

    private Double coinPrice;
    private Double coinVolume;
    private List<Double> topCoinsPrice;
    private Double label;

    public FeatureVector() {
        topCoinsPrice = new ArrayList<>();
    }

    public void setCoinPrice(Double coinPrice) {
        this.coinPrice = coinPrice;
    }

    public void setCoinVolume(Double coinVolume) {
        this.coinVolume = coinVolume;
    }

    public List<Double> getTopCoinsPrice() {
        return topCoinsPrice;
    }

    public void setLabel(Double label) {
        this.label = label;
    }

    public Double getCoinPrice() {
        return coinPrice;
    }

    public Double getCoinVolume() {
        return coinVolume;
    }

    public Double getLabel() {
        return label;
    }
}
