package data_extraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RequestHandlerCoinMarketCap {

    public static String getTopCoinsSymbolRequest() {

        StringBuilder coinsInfo = new StringBuilder();
        int coinNumber = 100;
        String coinNumberInRequest = "";
        do {
            try {
                URL cryptoCompare = new URL("https://api.coinmarketcap.com/v1/ticker/" + coinNumberInRequest);
                URLConnection cryptoConnection = cryptoCompare.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(cryptoConnection.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    String newLine = line;
                    if(coinNumber > 100) {
                        if(line.contains("[")) {
                            newLine = line.replace("[", "");
                        } else if(line.contains("]") && coinNumber != 800) {
                            newLine = line.replace("]", ",");
                        }
                    } else  if(line.contains("]")) {
                        newLine = line.replace("]", ",");
                    }
                    coinsInfo.append(newLine);
                }
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            coinNumberInRequest = "/?start=" + coinNumber;
            coinNumber += 100;
        } while(coinNumber < 900);



        return coinsInfo.toString();
    }
}
