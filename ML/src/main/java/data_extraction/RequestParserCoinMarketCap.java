package data_extraction;

import com.google.gson.Gson;
import model.CoinSymbol;

import java.util.Arrays;
import java.util.List;

public class RequestParserCoinMarketCap {

    public static List<CoinSymbol> parseTopCoinsSymbolsRequest(String coinsInfo) {
        Gson gson = new Gson();
        CoinSymbol[] coinSymbols = gson.fromJson(coinsInfo, CoinSymbol[].class);

        return Arrays.asList(coinSymbols);
    }

}
