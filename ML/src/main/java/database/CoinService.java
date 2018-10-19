package database;

import database.HibernateUtil;
import model.Coin;
import org.hibernate.*;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class CoinService {


    public void addCoin(Coin coin){
        int coinId = -1;
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();
            coinId = (Integer) session.save(coin);
            coin.setCoinIdDatabase(coinId);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
        }
    }

    public Coin updateCoin(Coin coin) {
        Transaction tx = null;
        Coin newCoin = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();
            newCoin = session.load(Coin.class, coin.getCoinIdDatabase());

            if (coin.getSymbol() != null) {
                newCoin.setSymbol(coin.getSymbol());
            }

            if (coin.getId() != null) {
                newCoin.setId(coin.getId());
            }

            if (coin.getName() != null) {
                newCoin.setName(coin.getName());
            }

            session.update(newCoin);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
        }
        return newCoin;
    }

    public Coin getCoin(String coinId) {
        Transaction tx = null;
        Coin coin = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();
            coin = session.load(Coin.class, Integer.parseInt(coinId));
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
        }
        return coin;
    }

    @SuppressWarnings("unchecked")
    public List<Coin> findCoins() {
        Transaction tx = null;
        List<Coin> coins = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();
            coins = (List<Coin>) session.createQuery("FROM Coin").list();
            for (Coin coin : coins) {
                System.out.println(coin.getSymbol());
                //Hibernate.initialize(coin.getPriceByDay());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return coins;
    }
}
