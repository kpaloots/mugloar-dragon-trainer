package com.example.mugloar.model;

public class ShopResponse {
    private String shoppingSuccess;
    private int gold;
    private int lives;
    private int level;
    private int turn;

    public String getShoppingSuccess() {
        return shoppingSuccess;
    }

    public void setShoppingSuccess(String shoppingSuccess) {
        this.shoppingSuccess = shoppingSuccess;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
}

