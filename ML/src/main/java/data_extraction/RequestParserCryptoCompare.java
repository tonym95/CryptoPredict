package data_extraction;

import com.google.gson.Gson;
import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RequestParserCryptoCompare extends RequestHandlerCoinMarketCap {

    public static List<Coin> parseAllCoinsRequest(List<CoinSymbol> coinSymbols, String coinsInfo) {
        Gson gson = new Gson();
        List<Coin> coins = new ArrayList<>();

        ResponseCryptoCompare response = gson.fromJson(coinsInfo, ResponseCryptoCompare.class);

        for(CoinSymbol coinSymbol : coinSymbols) {
            if(response.getData().containsKey(coinSymbol.getSymbol())) {
                coins.add(response.getData().get(coinSymbol.getSymbol()));
            }
        }

        return coins;

    }

    public static void parseHistoricalDataRequest(Coin c, String coinsInfo) {
        Gson gson = new Gson();
        Response response = gson.fromJson(coinsInfo, Response.class);


        for(int i = 0; i < response.getData().length; i++) {
            if(response.getData()[i].getClose() != 0) {
                c.getPriceByDay().put(response.getData()[i].getTime(), response.getData()[i].getClose());
                c.getVolumeByDay().put(response.getData()[i].getTime(), response.getData()[i].getVolumeTo());
            }
        }
    }
}
