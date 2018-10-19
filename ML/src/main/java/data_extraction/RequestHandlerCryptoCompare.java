package data_extraction;

import model.Coin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class RequestHandlerCryptoCompare {


    public static String getAllCoinsRequest() {
        StringBuilder coinsInfo = new StringBuilder();
        BufferedReader in = null;
        try {
            URL cryptoCompare = new URL("https://www.cryptocompare.com/api/data/coinlist/");
            URLConnection cryptoConnection = cryptoCompare.openConnection();
            in = new BufferedReader(new InputStreamReader(cryptoConnection.getInputStream()));

            String line;

            while ((line = in.readLine()) != null) {
                coinsInfo.append(line);
            }

        } catch (IOException ex) {
        ex.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return coinsInfo.toString();
    }

    public static String getHistoricalDataRequest(String coinSymbol) {
        StringBuilder coinsInfo = new StringBuilder();
        try {
                URL cryptoCompare = new URL("https://min-api.cryptocompare.com/data/histoday?fsym=" + coinSymbol + "&tsym=USD&limit=2000&aggregate=1&e=CCCAGG");
                URLConnection cryptoConnection = cryptoCompare.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(cryptoConnection.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    coinsInfo.append(line);
                }
        }   catch (IOException e) {
                e.printStackTrace();
            }

            return coinsInfo.toString();
    }



}
