package ui;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import data_extraction.RequestHandlerCryptoCompare;
import data_extraction.RequestParserCryptoCompare;
import database.CoinService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import feature_extraction.FeatureExtractor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Coin;
import model.FeatureVector;
import org.controlsfx.control.textfield.CustomTextField;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import predictor.LSTMPredictor;
import utils.PlotterUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SceneController {

    @FXML
    public ImageView logoImageView;

    @FXML
    public HBox mainHbox;

    @FXML
    public VBox toolbarVBox;

    @FXML
    public VBox updateDataVBox;

    @FXML
    public Label updateLabel;

    @FXML
    public Label dataLabel;


    @FXML
    private ToggleButton pickCoinButton;

    @FXML
    private ToggleButton updateDataButton;

    @FXML
    private ToggleButton predictCoinButton;

    @FXML
    public HBox pickCoinHBox;

    @FXML
    private CustomTextField searchTextField;

    @FXML
    private Label selectedCoinNameLabel;

    @FXML
    private ImageView selectedCoinImageView;

    @FXML
    private JFXListView<String> searchListView;


    @FXML
    public VBox predictVBox;

    @FXML
    public LineChart lineChart;

    @FXML
    public Label mseValueLabel;

    @FXML
    public Label rmseValueLabel;

    @FXML
    public Label maeValueLabel;

    @FXML
    public Label r2ValueLabel;


    @FXML
    private URL location;

    private String selectedCoin = "";

    private Boolean updated = false;

    private List<Coin> coins;

    public SceneController() {}

    @FXML
    private void initialize() {
        List<String> coinNames = new ArrayList<>();
        coins = retrieveCoins();

        for(int i = 0; i < 566; i++) {
            coinNames.add(coins.get(i).getName());
        }

        pickCoinButton.setSelected(true);
        pickCoinHBox.setVisible(true);
        pickCoinHBox.setManaged(true);
        updateDataVBox.setVisible(false);
        updateDataVBox.setManaged(false);
        predictVBox.setVisible(false);
        predictVBox.setManaged(false);

        pickCoinButton.setStyle("-fx-background-color: #707070;");
        updateDataButton.setStyle("-fx-background-color: #303030");
        predictCoinButton.setStyle("-fx-background-color: #303030");


        Image image = new Image("file:crypto_logo.png",
                100, 100, false, true);

        System.err.println(image.getException());

        logoImageView.setImage(image);
        lineChart.setCreateSymbols(false);

        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
        searchTextField.setRight(icon);

        selectedCoinImageView.setVisible(false);
        selectedCoinNameLabel.setVisible(false);

        ObservableList<String> items = FXCollections.observableArrayList(coinNames);
        searchListView.setItems(items);

        searchListView.setCellFactory(param -> new JFXListCell<String>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    for (Coin c : coins) {
                        if (name.equals(c.getName())) {
                            imageView.setImage(requestImageFromUrl("https://www.cryptocompare.com" + c.getImageUrl(), true));
                            break;
                        }
                    }
                    setText(name);
                    setGraphic(imageView);
                }
            }
        });

        searchListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedCoin = newValue;
            if(selectedCoin != null) {
                selectedCoinNameLabel.setText(selectedCoin + " selected!");
                selectedCoinNameLabel.setVisible(true);

                for (Coin c : coins) {
                    if (c.getName().equals(selectedCoin)) {
                        selectedCoinImageView.setImage(requestImageFromUrl("https://www.cryptocompare.com" + c.getImageUrl(), false));
                        selectedCoinImageView.setVisible(true);
                        break;
                    }
                }
                updated = false;
            }
            System.out.println("Selected item: " + newValue);
        });
    }

    private List<Coin> retrieveCoins() {
        CoinService coinService = new CoinService();
        return coinService.findCoins();
    }

    private Image requestImageFromUrl(String url, boolean resize) {
        URLConnection conn = null;
        try {
            conn = new URL(url).openConnection();
            conn.setRequestProperty("User-Agent", "Wget/1.13.4 (linux-gnu)");
            try(InputStream stream = conn.getInputStream()) {
                if(resize) {
                    return new Image(stream, 16, 16, false, true);
                } else {
                    return new Image(stream, 200, 200, false, true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @FXML
    private void filterList(KeyEvent keyEvent) {

        FilteredList<String> filteredData = new FilteredList<>(searchListView.getItems(), s -> true);

        searchTextField.textProperty().addListener(obs->{
            String filter = searchTextField.getText();
            if(filter == null || filter.length() == 0) {
                filteredData.setPredicate(s -> true);
            }
            else {
                filteredData.setPredicate(s -> s.contains(filter));
            }
        });

        searchListView.setItems(filteredData);

    }

    @FXML
    private void searchBarTextClear(MouseEvent mouseEvent) {
        searchTextField.clear();
    }

    @FXML
    public void pickCoin(MouseEvent mouseEvent) {
        pickCoinButton.setSelected(true);
        pickCoinButton.setStyle("-fx-background-color: #707070;");
        updateDataButton.setStyle("-fx-background-color: #303030");
        updateDataButton.setSelected(false);
        predictCoinButton.setSelected(false);
        predictCoinButton.setStyle("-fx-background-color: #303030");

        pickCoinHBox.setVisible(true);
        pickCoinHBox.setManaged(true);
        updateDataVBox.setVisible(false);
        updateDataVBox.setManaged(false);
        predictVBox.setVisible(false);
        predictVBox.setManaged(false);

        updateLabel.setText("");
        dataLabel.setText("");
      //  pickCoinButton.set

    }

    @FXML
    public void updateData(MouseEvent mouseEvent) {

        if(selectedCoin.equals("")) {
            displayCryptoNotSelectedAlert(mouseEvent);
        } else {

            updateDataButton.setSelected(true);
            updateDataButton.setStyle("-fx-background-color: #707070;");
            pickCoinButton.setSelected(false);
            pickCoinButton.setStyle("-fx-background-color: #303030");
            predictCoinButton.setSelected(false);
            predictCoinButton.setStyle("-fx-background-color: #303030");

            FeatureExtractor featureExtractor = new FeatureExtractor();

            updateLabel.setText("Data is not actualized!");
            dataLabel.setText("");

            List<Coin> topCoins = featureExtractor.computeTopCoins(coins);
            List<FeatureVector> updatedFeatureVectors;

            if (!updated) {
                for (Coin c : coins) {
                    if (c.getName().equals(selectedCoin)) {

                        Date lastDate = new Date(Long.parseLong(((SortedMap<String, Double>) c.getPriceByDay()).lastKey()));
                        LocalDate lastLocalDate = lastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        if (lastLocalDate.getDayOfYear() != LocalDate.now().getDayOfYear()) {
                            List<FeatureVector> currentFeatureVectors = featureExtractor.extractFeaturesForCoin(c, topCoins);
                            featureExtractor.computeMinMaxValues(currentFeatureVectors);
                            currentFeatureVectors.clear();
                            for (Coin coin : topCoins) {
                                updateCoinData(coin);
                            }
                            updateCoinData(c);
                            updatedFeatureVectors = featureExtractor.extractFeaturesForCoin(c, topCoins);
                            topCoins.clear();
                            featureExtractor.normalizeFeatures(updatedFeatureVectors, 7);
                            updatedFeatureVectors.remove(0);
                            File file = new File("dataset_change_version");
                            featureExtractor.writeToCSVFile("dataset_change_version/" +
                                    coins.indexOf(c) + "_test.csv", updatedFeatureVectors, true);
                            updateLabel.setText("Data has been actualized to:");
                            dataLabel.setText(String.valueOf(LocalDate.now()) + "!");
                            updated = true;
                        } else {
                            updated = true;
                        }
                    }
                }
            } else {
                updateLabel.setText("Data has been already actualized!");
                dataLabel.setText("");
            }

            pickCoinHBox.setVisible(false);
            pickCoinHBox.setManaged(false);
            updateDataVBox.setVisible(true);
            updateDataVBox.setManaged(true);
            predictVBox.setVisible(false);
            predictVBox.setManaged(false);
        }
    }

    private void updateCoinData(Coin c) {
        String lastDateString = ((SortedMap<String, Double>) c.getPriceByDay()).lastKey();

        String coinInfo = RequestHandlerCryptoCompare.getHistoricalDataRequest(c.getSymbol());
        RequestParserCryptoCompare.parseHistoricalDataRequest(c, coinInfo);

        Date lastDate = new Date(Long.parseLong(lastDateString) * 1000);

        LocalDate lastLocalDate = lastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


        List<String> keysToRemove = new ArrayList<>();

        Iterator<Map.Entry<String, Double>> it = c.getPriceByDay().entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Double> entry = it.next();
            Date currentDate = new Date(Long.parseLong(entry.getKey()) * 1000);
            LocalDate localCurrentDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if(localCurrentDate.isBefore(lastLocalDate)) {
                keysToRemove.add(entry.getKey());
                it.remove();
            }
        }

        for(String key : keysToRemove) {
            c.getVolumeByDay().remove(key);
        }

    }

    @FXML
    public void predict(MouseEvent mouseEvent) {
        LSTMPredictor predictor = new LSTMPredictor();
        PlotterUtils plotterUtils = new PlotterUtils();
        lineChart.getData().clear();
        updateLabel.setText("");
        dataLabel.setText("");
        if(selectedCoin.equals("")) {
            displayCryptoNotSelectedAlert(mouseEvent);
        } else {
            Coin coin = null;
            for(Coin c : coins) {
                if(c.getName().equals(selectedCoin)) {
                    coin = c;
                }
            }

            int coinIndex = coins.indexOf(coin);
            DataSetIterator trainIterator = predictor.computeDataSetFromFile("dataset_change_version", coinIndex, true);
            DataSetIterator testIterator = predictor.computeDataSetFromFile("dataset_change_version", coinIndex, false);
            predictor.computeDataSetsFromIterators(trainIterator, testIterator);
            MultiLayerNetwork model = predictor.loadModel("defaultModel.zip");
            INDArray predictions = predictor.testModel(model);
            INDArray testLabels = predictor.getTestData().getLabels();

            XYChart.Series<Integer, Double> predictionsSeries = new XYChart.Series<>();
            predictionsSeries.setName("predictions");

            plotterUtils.addSeriesToChartUI(predictionsSeries, predictions);

            XYChart.Series<Integer, Double> labelSeries = new XYChart.Series<>();
            labelSeries.setName("real values");

            plotterUtils.addSeriesToChartUI(labelSeries, testLabels);

            lineChart.getData().addAll(predictionsSeries, labelSeries);

            DecimalFormat df = new DecimalFormat("#.#####");
            df.setRoundingMode(RoundingMode.CEILING);

            mseValueLabel.setText(String.valueOf(df.format(predictor.getEval().averageMeanSquaredError())));
            rmseValueLabel.setText(String.valueOf(df.format(predictor.getEval().averagerootMeanSquaredError())));
            maeValueLabel.setText(String.valueOf(df.format(predictor.getEval().averageMeanAbsoluteError())));
            r2ValueLabel.setText(String.valueOf(df.format(predictor.getEval().averagecorrelationR2())));


            pickCoinButton.setSelected(false);
            updateDataButton.setSelected(false);
            predictCoinButton.setSelected(true);

            predictCoinButton.setStyle("-fx-background-color: #707070;");
            updateDataButton.setStyle("-fx-background-color: #303030");
            pickCoinButton.setStyle("-fx-background-color: #303030");

            pickCoinHBox.setVisible(false);
            pickCoinHBox.setManaged(false);
            updateDataVBox.setVisible(false);
            updateDataVBox.setManaged(false);
            predictVBox.setVisible(true);
            predictVBox.setManaged(true);

            //classifier.trainAndTestModel(model, 200);
        }
    }

    private void displayCryptoNotSelectedAlert(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Coin not selected!");
        alert.setContentText("Please select which cryptocurrency you want to analyze!");

        alert.showAndWait();

        Event.fireEvent(pickCoinButton, mouseEvent);
    }

}
