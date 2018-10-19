package feature_extraction;

import database.CoinService;
import model.Coin;
import model.FeatureVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static utils.Constants.SPLITTING_COEFFICIENT;

public class DatabaseRetriever {

    public static void main(String args[]) {

        CoinService coinService = new CoinService();

        List<Coin> coinsFromDb = coinService.findCoins();

        FeatureExtractor featureExtractor = new FeatureExtractor();

        int fileCount = 0;

        for(Coin c : coinsFromDb) {
            List<Coin> topCoins = featureExtractor.computeTopCoins(coinsFromDb);
            List<FeatureVector> featureVectors = featureExtractor.extractFeaturesForCoin(c, topCoins);

            for(Iterator<FeatureVector> iterator = featureVectors.iterator(); iterator.hasNext();) {
                FeatureVector featureVector = iterator.next();

                if(featureVector.getCoinPrice() == 0.0) {
                    iterator.remove();
                }
            }

            List<FeatureVector> trainingVectors = new ArrayList<>();
            List<FeatureVector> testingVectors = new ArrayList<>();

            int exampleCount = 0;
            if(featureVectors.size() > 0) {
                int trainingExamplesNo = (int)((SPLITTING_COEFFICIENT / 100.0) * featureVectors.size());
                for(FeatureVector vector : featureVectors) {
                    if (exampleCount < trainingExamplesNo) {
                        trainingVectors.add(vector);
                    } else {
                        testingVectors.add(vector);
                    }
                    exampleCount++;
                }
                }
                if(trainingVectors.size() >= 7 && testingVectors.size() >= 7) {
                    featureExtractor.computeMinMaxValues(trainingVectors);
                    featureExtractor.normalizeFeatures(trainingVectors, 7);
                    featureExtractor.normalizeFeatures(testingVectors, 7);
                    featureExtractor.writeToCSVFile(fileCount + "_train.csv", trainingVectors, false);
                    featureExtractor.writeToCSVFile(fileCount + "_test.csv", testingVectors, false);
                    fileCount++;
                }
            }


        }

}
