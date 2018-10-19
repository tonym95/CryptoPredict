package feature_extraction;

import model.Coin;
import model.FeatureVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class FeatureExtractor {

    private double minVolume = Double.MAX_VALUE;
    private double maxVolume = Double.MIN_VALUE;

    public List<FeatureVector> extractFeaturesForCoin(Coin c, List<Coin> topCoins) {
        List<FeatureVector> coinFeatureVectors = new ArrayList<>();
        int maxIndexZeros;


        List<Double> prices = Collections.list(Collections
                .enumeration(c.getPriceByDay().values()));

        List<Double> volumes = Collections.list(Collections
                .enumeration(c.getVolumeByDay().values()));

        List<LinkedList<Double>> topCoinPrices = new ArrayList<>();

        for(Iterator<Coin> iterator = topCoins.iterator(); iterator.hasNext();) {
            Coin currentCoin = iterator.next();
            if(c.equals(currentCoin)) {
                iterator.remove();
            }
        }

        if(topCoins.size() > 10) {
            topCoins.remove(10);
        }

        for(Coin coin : topCoins) {
            topCoinPrices.add(new LinkedList<Double>());
        }

        int stackIndex = 0;

        for(Coin coin : topCoins) {
            int zerosIndex = 0;
            maxIndexZeros = c.getPriceByDay().size() - coin.getPriceByDay().size();
            while(zerosIndex < maxIndexZeros) {
                topCoinPrices.get(stackIndex).add(0.0);
                zerosIndex++;
            }
                for (Map.Entry<String, Double> priceEntry : coin.getPriceByDay().entrySet()) {
                        topCoinPrices.get(stackIndex).add(priceEntry.getValue());
                }
                stackIndex++;
        }

        for(int i = 0; i < prices.size(); i++) {
            FeatureVector vector = new FeatureVector();
            vector.setCoinPrice(prices.get(i));
            vector.setCoinVolume(volumes.get(i));

            for(LinkedList<Double> priceList : topCoinPrices) {
                vector.getTopCoinsPrice().add(priceList.removeFirst());
            }

            coinFeatureVectors.add(vector);

        }

        return coinFeatureVectors;
    }


    public List<Coin> computeTopCoins(List<Coin> allCoins) {
        List<Coin> topCoins = new ArrayList<>();
        for(int i = 0; i < 11; i++) {
            topCoins.add(allCoins.get(i));
        }

        return topCoins;
    }

    public void computeMinMaxValues(List<FeatureVector> featureVectors) {

        for(FeatureVector vector : featureVectors) {
            if(minVolume > vector.getCoinVolume()) {
                minVolume = vector.getCoinVolume();
            }

            if(maxVolume < vector.getCoinVolume()) {
                maxVolume = vector.getCoinVolume();
            }
        }
    }

    public void normalizeFeatures(List<FeatureVector> featureVectors, int slidingWindowSize) {

        int slidingWindowIndex = 0;

        while(slidingWindowIndex < featureVectors.size()) {
            double[] basePrices = new double[featureVectors.get(0).getTopCoinsPrice().size() + 1];
            basePrices[0] = featureVectors.get(slidingWindowIndex).getCoinPrice();
            for(int index = 1; index <= featureVectors.get(0).getTopCoinsPrice().size(); index++) {
                basePrices[index] = featureVectors.get(slidingWindowIndex).getTopCoinsPrice().get(index-1);
            }
            for (int i = 0; i < slidingWindowSize; i++) {
                if(i+slidingWindowIndex >= featureVectors.size()) {
                    break;
                }
                featureVectors.get(i+slidingWindowIndex).setCoinPrice((featureVectors.get(i+slidingWindowIndex).getCoinPrice() /
                    basePrices[0]) - 1);
                for(int topCoinIndex = 0; topCoinIndex < featureVectors.get(i+slidingWindowIndex).getTopCoinsPrice().size();
                        topCoinIndex++) {
                    if(basePrices[topCoinIndex+1] > 0.0) {
                        featureVectors.get(i+slidingWindowIndex).getTopCoinsPrice().set(topCoinIndex,
                                (featureVectors.get(i+slidingWindowIndex).getTopCoinsPrice().get(topCoinIndex) / basePrices[topCoinIndex+1]) - 1);
                    }
                }

            }

            slidingWindowIndex += slidingWindowSize;
        }

        for(int i = 0; i < featureVectors.size() - 1; i++) {
            featureVectors.get(i).setLabel(featureVectors.get(i + 1).getCoinPrice());
            featureVectors.get(i).setCoinVolume((featureVectors.get(i).getCoinVolume() - minVolume) / (maxVolume - minVolume));
        }

    }

    public void writeToCSVFile(String filePath, List<FeatureVector> featureVectors, boolean append) {
        StringBuilder stringBuilder = new StringBuilder();
        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(new File(filePath), append))) {
            for (int i = 0; i < featureVectors.size() - 1; i++) {
                stringBuilder.append(featureVectors.get(i).getCoinPrice())
                        .append(",")
                        .append(featureVectors.get(i).getCoinVolume());
                for (Double topCoinsPrice : featureVectors.get(i).getTopCoinsPrice()) {
                    stringBuilder.append(",")
                            .append(topCoinsPrice);
                }
                stringBuilder.append(",")
                        .append(featureVectors.get(i).getLabel())
                        .append("\n");

            }
            printWriter.write(stringBuilder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
