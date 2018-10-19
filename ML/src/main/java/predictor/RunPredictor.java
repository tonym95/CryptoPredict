package predictor;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.RmsProp;

public class RunPredictor extends LSTMPredictor {

    public static void main(String args[]) {

        LSTMPredictor classifier = new LSTMPredictor();

        DataSetIterator trainIterator = classifier.computeDataSetFromFile("dataset_change_version", 1, true);
        DataSetIterator testIterator = classifier.computeDataSetFromFile("dataset_change_version", 1, false);
        classifier.computeDataSetsFromIterators(trainIterator, testIterator);
        MultiLayerNetwork model = classifier.buildModel(0.001, new Adam(), 180);
        classifier.trainAndTestModel(model, 100);
        classifier.saveModel(model, "defaultModel.zip");
    }
}
