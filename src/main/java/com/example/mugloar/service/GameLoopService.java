package com.example.mugloar.service;

import com.example.mugloar.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class GameLoopService {

    private final GameService gameService;
    private final Set<String> boughtUpgrades = new HashSet<>();
    private final List<String> upgradePriority = List.of(
            "cs", // Claw Sharpening
            "iron", // Iron Plating
            "tricks", // Book of Tricks
            "wingpot", // Potion of Stronger Wings
            "ch", // Claw Honing
            "mtrix", // Book of Megatricks
            "wingpotmax" // Potion of Awesome Wings
    );

    private final int goldReserveThreshold = 150;

    public GameLoopService(GameService gameService) {
        this.gameService = gameService;
    }

    public void runUntil1000Points() throws InterruptedException {
        int maxScore = 0;
        int gameCount = 0;

        while (maxScore < 1000) {
            gameCount++;
            boughtUpgrades.clear();

            GameResponse game = gameService.startNewGame();
            System.out.println("\nStarting game #" + gameCount + " | ID: " + game.getGameId());

            AtomicReference<Reputation> rep = new AtomicReference<>(
                    gameService.investigateReputation(game.getGameId()));
            System.out.println("Reputation → People: " + rep.get().getPeople() + ", State: " + rep.get().getState()
                    + ", Underworld: " + rep.get().getUnderworld());

            AtomicInteger consecutiveFails = new AtomicInteger(0);

            while (game.getLives() > 0) {
                Message[] messages = gameService.fetchMessages(game.getGameId());

                boolean safeMode = consecutiveFails.get() >= 2;

                Message best = Arrays.stream(messages)
                        .filter(m -> {
                            String p = m.getProbability();
                            int score = priorityValue(p);
                            int lives = game.getLives();
                            String msg = m.getMessage().toLowerCase();
                            int reward = Integer.parseInt(m.getReward().toString());

                            if (score == 0)
                                return false;

                            if ((msg.contains("super awesome diamond") || reward >= 150)
                                    && (msg.contains("diamond") || msg.contains("steal") || msg.contains("escort"))) {
                                return false;
                            }

                            if (safeMode)
                                return score >= 6;

                            if (lives == 3)
                                return score >= 1;
                            if (lives == 2)
                                return score >= 2;
                            return score >= 4;
                        })
                        .sorted((m1, m2) -> {
                            return Comparator
                                    .comparingInt((Message m) -> priorityValue(m.getProbability())
                                            + reputationBonus(m.getMessage(), rep.get()))
                                    .thenComparingInt(m -> Integer.parseInt(m.getReward().toString()))
                                    .reversed()
                                    .compare(m1, m2);
                        })
                        .findFirst()
                        .orElse(null);

                if (best == null) {
                    System.out.println("No safe messages found. Ending game.");
                    break;
                }

                System.out.println("Solving: " + best.getMessage() + " | Reward: " + best.getReward()
                        + " | Probability: " + best.getProbability());

                SolveResponse solve = gameService.solveAd(game.getGameId(), best.getAdId());
                System.out.println("✔️ Success: " + solve.isSuccess() + " | Score: " + solve.getScore() + " | Message: "
                        + solve.getMessage());

                game.setScore(solve.getScore());
                game.setGold(solve.getGold());
                game.setLives(solve.getLives());
                game.setTurn(solve.getTurn());

                if (solve.isSuccess()) {
                    consecutiveFails.set(0);
                } else {
                    int failProb = priorityValue(best.getProbability());
                    if (failProb >= 5) {
                        consecutiveFails.incrementAndGet();
                    }
                }

                rep.set(gameService.investigateReputation(game.getGameId()));
                System.out.println("Updated Reputation → People: " + rep.get().getPeople()
                        + ", State: " + rep.get().getState()
                        + ", Underworld: " + rep.get().getUnderworld());

                try {
                    ShopItem[] items = gameService.fetchShopItems(game.getGameId());

                    Optional<ShopItem> healing = Arrays.stream(items)
                            .filter(i -> i.getCost() <= game.getGold())
                            .filter(i -> i.getName().toLowerCase().contains("potion")
                                    || i.getName().toLowerCase().contains("life"))
                            .findFirst();

                    if (game.getGold() >= 50 && game.getLives() <= 1 && healing.isPresent()) {
                        ShopItem item = healing.get();
                        System.out.println("Buying (priority): " + item.getName() + " (Cost: " + item.getCost() + ")");
                        ShopResponse shop = gameService.buyItem(game.getGameId(), item.getId());
                        System.out.println(
                                "Shop result: " + shop.getShoppingSuccess() + " | Gold left: " + shop.getGold());

                        game.setGold(shop.getGold());
                        game.setLives(shop.getLives());
                        game.setTurn(shop.getTurn());
                    } else {
                        Arrays.stream(items)
                                .filter(i -> i.getCost() <= game.getGold() - goldReserveThreshold)
                                .filter(i -> !i.getName().toLowerCase().contains("potion")
                                        && !i.getName().toLowerCase().contains("life"))
                                .sorted(Comparator.comparingInt(i -> upgradePriority.indexOf(i.getId())))
                                .filter(i -> upgradePriority.contains(i.getId()))
                                .filter(i -> !boughtUpgrades.contains(i.getId()))
                                .findFirst()
                                .ifPresent(item -> {
                                    System.out.println(
                                            "Buying (upgrade): " + item.getName() + " (Cost: " + item.getCost() + ")");
                                    ShopResponse shop = gameService.buyItem(game.getGameId(), item.getId());
                                    System.out.println("Shop result: " + shop.getShoppingSuccess() + " | Gold left: "
                                            + shop.getGold());

                                    game.setGold(shop.getGold());
                                    game.setLives(shop.getLives());
                                    game.setTurn(shop.getTurn());
                                    boughtUpgrades.add(item.getId());
                                });
                    }
                } catch (Exception e) {
                    System.out.println("Shop error: " + e.getMessage());
                }

                Thread.sleep(300);
            }

            System.out.println("🏁 Game over. Score: " + game.getScore());
            if (game.getScore() > maxScore) {
                maxScore = game.getScore();
            }
        }

        System.out.println("\nFinished after " + gameCount + " games. Highest score: " + maxScore);
    }

    public int priorityValue(String probability) {
        if (probability == null)
            return 0;
        switch (probability.toLowerCase()) {
            case "piece of cake":
                return 7;
            case "walk in the park":
                return 6;
            case "sure thing":
                return 5;
            case "hmmm....":
                return 4;
            case "risky":
                return 3;
            case "gamble":
                return 2;
            case "playing with fire":
                return 1;
            case "impossible":
            case "suicide mission":
            default:
                return 0;
        }
    }

    public int reputationBonus(String message, Reputation rep) {
        String lower = message.toLowerCase();
        int bonus = 0;

        if (rep.getPeople() < -1 && (lower.contains("escort") || lower.contains("advertise") ||
                lower.contains("sell") || lower.contains("fix") || lower.contains("transport"))) {
            bonus -= 3;
        }

        if (rep.getState() < -1 && lower.contains("defend")) {
            bonus -= 3;
        }

        if (rep.getUnderworld() < -1 && (lower.contains("steal") || lower.contains("share profit"))) {
            bonus -= 3;
        }

        return bonus;
    }

}