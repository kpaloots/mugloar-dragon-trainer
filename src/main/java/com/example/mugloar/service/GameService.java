package com.example.mugloar.service;

import com.example.mugloar.model.Message;
import com.example.mugloar.model.GameResponse;
import com.example.mugloar.model.Reputation;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.mugloar.model.SolveResponse;
import com.example.mugloar.model.ShopItem;
import com.example.mugloar.model.ShopResponse;

@Service
public class GameService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String START_GAME_URL = "https://dragonsofmugloar.com/api/v2/game/start";

    public GameResponse startNewGame() {
        return restTemplate.postForObject(START_GAME_URL, null, GameResponse.class);
    }

    public Reputation investigateReputation(String gameId) {
        String url = "https://dragonsofmugloar.com/api/v2/" + gameId + "/investigate/reputation";
        return restTemplate.postForObject(url, null, Reputation.class);
    }

    public Message[] fetchMessages(String gameId) {
        String url = "https://dragonsofmugloar.com/api/v2/" + gameId + "/messages";
        return restTemplate.getForObject(url, Message[].class);
    }

    public SolveResponse solveAd(String gameId, String adId) {
        String url = "https://dragonsofmugloar.com/api/v2/" + gameId + "/solve/" + adId;
        return restTemplate.postForObject(url, null, SolveResponse.class);
    }

    public ShopItem[] fetchShopItems(String gameId) {
        String url = "https://dragonsofmugloar.com/api/v2/" + gameId + "/shop";
        return restTemplate.getForObject(url, ShopItem[].class);
    }

    public ShopResponse buyItem(String gameId, String itemId) {
        String url = "https://dragonsofmugloar.com/api/v2/" + gameId + "/shop/buy/" + itemId;
        return restTemplate.postForObject(url, null, ShopResponse.class);
    }
}
