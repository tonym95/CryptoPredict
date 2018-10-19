package model;

import org.hibernate.annotations.SortNatural;
import sun.net.www.protocol.http.AuthCache;

import javax.persistence.*;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Entity
@Table(name = "coin")
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer coinIdDatabase;
    private Integer Id;
    private String Name;
    private String Symbol;
    private String ImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @SortNatural
    private SortedMap<String, Double> priceByDay = new TreeMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @SortNatural
    private SortedMap<String, Double> volumeByDay = new TreeMap<>();

    public Coin() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coin coin = (Coin) o;

        if (getCoinIdDatabase() != null ? !getCoinIdDatabase().equals(coin.getCoinIdDatabase()) : coin.getCoinIdDatabase() != null)
            return false;
        if (getId() != null ? !getId().equals(coin.getId()) : coin.getId() != null) return false;
        if (getName() != null ? !getName().equals(coin.getName()) : coin.getName() != null) return false;
        return getSymbol() != null ? getSymbol().equals(coin.getSymbol()) : coin.getSymbol() == null;
    }

    @Override
    public int hashCode() {
        int result = getCoinIdDatabase() != null ? getCoinIdDatabase().hashCode() : 0;
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getSymbol() != null ? getSymbol().hashCode() : 0);
        return result;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String symbol) {
        this.Symbol = symbol;
    }

    public Map<String, Double> getPriceByDay() {
        return priceByDay;
    }

    public Integer getId() {
        return Id;
    }

    public Integer getCoinIdDatabase() {
        return coinIdDatabase;
    }

    public void setCoinIdDatabase(Integer coinIdDatabase) {
        this.coinIdDatabase = coinIdDatabase;
    }

    public void setId(Integer id) {
        this.Id = id;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }

    public Map<String, Double> getVolumeByDay() {
        return volumeByDay;
    }

    public void setPriceByDay(SortedMap<String, Double> priceByDay) {
        this.priceByDay = priceByDay;
    }

    public void setVolumeByDay(SortedMap<String, Double> volumeByDay) {
        this.volumeByDay = volumeByDay;
    }
}
