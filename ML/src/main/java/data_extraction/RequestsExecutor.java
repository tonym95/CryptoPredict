package data_extraction;

import database.CoinService;
import model.Coin;
import model.CoinSymbol;

import java.util.List;

public class RequestsExecutor extends RequestParserCoinMarketCap {

    public static void main(String args[]) {

        CoinService coinService = new CoinService();
        String coinInfo = "";

        coinInfo = RequestHandlerCoinMarketCap.getTopCoinsSymbolRequest();
        List<CoinSymbol> symbols = RequestParserCoinMarketCap.parseTopCoinsSymbolsRequest(coinInfo);

        coinInfo = RequestHandlerCryptoCompare.getAllCoinsRequest();
        List<Coin> coins = RequestParserCryptoCompare.parseAllCoinsRequest(symbols, coinInfo);

        for(Coin c : coins) {
            coinInfo = RequestHandlerCryptoCompare.getHistoricalDataRequest(c.getSymbol());
            RequestParserCryptoCompare.parseHistoricalDataRequest(c, coinInfo);
        }

        for(Coin c : coins) {
                coinService.addCoin(c);
        }

        }

}
