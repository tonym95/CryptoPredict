package predictor;

import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.learning.config.*;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import utils.PlotterUtils;

import java.io.File;
import java.io.IOException;

import static utils.Constants.*;

public class LSTMPredictor {

    private DataSet trainingData;
    private DataSet testData;
    private RegressionEvaluation eval = new RegressionEvaluation(1);


    public DataSetIterator computeDataSetFromFile(String filePath, int coinNo, boolean train) {

        File dataSetDir = new File(filePath);

        SequenceRecordReader recordReader = new CSVSequenceRecordReader(0, ",");

        try {
            if(train) {
                recordReader.initialize(new NumberedFileInputSplit(dataSetDir.getAbsolutePath() + "/%d_train.csv",
                        coinNo, coinNo));
            } else {
                recordReader.initialize(new NumberedFileInputSplit(dataSetDir.getAbsolutePath() + "/%d_test.csv",
                        coinNo, coinNo));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return new SequenceRecordReaderDataSetIterator(recordReader, BATCH_SIZE, -1,
                12, true);
    }

    public void computeDataSetsFromIterators(DataSetIterator trainIterator, DataSetIterator testIterator) {
        trainingData = trainIterator.next();
        testData = testIterator.next();

    }

    public MultiLayerNetwork buildModel(double learningRate, IUpdater updater, int hiddenNeuronsNo) {
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .learningRate(learningRate)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(1.0)
                .updater(updater)
                .weightInit(WeightInit.XAVIER_UNIFORM)
                .list()
                .layer(0, new GravesLSTM.Builder()
                        .nIn(NUM_INPUTS)
                        .nOut(hiddenNeuronsNo)
                        .dropOut(0.2)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new GravesLSTM.Builder()
                        .nIn(hiddenNeuronsNo)
                        .nOut(hiddenNeuronsNo)
                       .dropOut(0.2)
                        .activation(Activation.TANH)
                        .build())
                .layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(hiddenNeuronsNo)
                        .nOut(1)
                        .build()).backpropType(BackpropType.TruncatedBPTT)
                .tBPTTBackwardLength(TBBPT_LENGTH).tBPTTForwardLength(TBBPT_LENGTH)
                .build();

        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();

        uiServer.attach(statsStorage);



        MultiLayerNetwork model = new MultiLayerNetwork(config);

        model.setListeners(new StatsListener(statsStorage));
        model.init();

        return model;
    }


    public void trainAndTestModel(MultiLayerNetwork model, int epochs) {


        for(int i = 0; i < epochs; i++) {
                model.fit(trainingData);


            INDArray output = model.rnnTimeStep(testData.getFeatures());
            INDArray labels = testData.getLabels();

            eval.evalTimeSeries(labels, output);

            System.out.println(eval.stats());

            model.rnnClearPreviousState();

        }

        INDArray predicted = model.rnnTimeStep(testData.getFeatures());

        PlotterUtils plotter = new PlotterUtils();

        plotter.plotData(testData.getLabels(), predicted);
    }

    public INDArray testModel(MultiLayerNetwork model) {
        INDArray predicted = model.rnnTimeStep(testData.getFeatures());
        eval.evalTimeSeries(testData.getLabels(), predicted);
        return predicted;
    }

    public void saveModel(MultiLayerNetwork network, String fileName) {
        File modelLocationFile = new File(fileName);
        boolean saveUpdater = true;
        try {
            ModelSerializer.writeModel(network, fileName, saveUpdater);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MultiLayerNetwork loadModel(String fileName) {
        File modelLocationFile = new File(fileName);
        try {
            return ModelSerializer.restoreMultiLayerNetwork(modelLocationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public DataSet getTestData() {
        return testData;
    }

    public RegressionEvaluation getEval() {
        return eval;
    }
}
