package model;

public class Data {

    private String time;
    private Double close;
    private Double high;
    private Double low;
    private Double volumefrom;
    private Double volumeto;


    public Double getVolumeFrom() {
        return volumefrom;
    }

    public void setVolumeFrom(Double volumeFrom) {
        this.volumefrom = volumeFrom;
    }

    public Double getVolumeTo() {
        return volumeto;
    }

    public void setVolumeTo(Double volumeTo) {
        this.volumeto = volumeTo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }
}
