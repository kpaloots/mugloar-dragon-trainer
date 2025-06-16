package com.example.service;

import com.example.mugloar.model.Reputation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.mugloar.service.GameLoopService;

import static org.junit.jupiter.api.Assertions.*;

public class GameLoopServiceTest {

    private GameLoopService gameLoopService;

    @BeforeEach
    public void setup() {
        gameLoopService = new GameLoopService(null);
    }

    @Test
    public void testPriorityValue() {
        assertEquals(7, gameLoopService.priorityValue("piece of cake"));
        assertEquals(6, gameLoopService.priorityValue("walk in the park"));
        assertEquals(5, gameLoopService.priorityValue("sure thing"));
        assertEquals(4, gameLoopService.priorityValue("hmmm...."));
        assertEquals(3, gameLoopService.priorityValue("risky"));
        assertEquals(2, gameLoopService.priorityValue("gamble"));
        assertEquals(1, gameLoopService.priorityValue("playing with fire"));
        assertEquals(0, gameLoopService.priorityValue("suicide mission"));
        assertEquals(0, gameLoopService.priorityValue("unknown"));
        assertEquals(0, gameLoopService.priorityValue(null));
    }

    @Test
    public void testReputationBonusPeopleLowEscortMessage() {
        Reputation rep = new Reputation();
        rep.setPeople(-2);
        rep.setState(1);
        rep.setUnderworld(1);

        int bonus = gameLoopService.reputationBonus("Escort John to the village", rep);
        assertEquals(-3, bonus);
    }

    @Test
    public void testReputationBonusStateLowDefendMessage() {
        Reputation rep = new Reputation();
        rep.setPeople(1);
        rep.setState(-3);
        rep.setUnderworld(0);

        int bonus = gameLoopService.reputationBonus("Help defending the village", rep);
        assertEquals(-3, bonus);
    }

    @Test
    public void testReputationBonusUnderworldLowStealMessage() {
        Reputation rep = new Reputation();
        rep.setPeople(0);
        rep.setState(0);
        rep.setUnderworld(-4);

        int bonus = gameLoopService.reputationBonus("Steal gold from the convoy", rep);
        assertEquals(-3, bonus);
    }

    @Test
    public void testReputationBonusNoPenalties() {
        Reputation rep = new Reputation();
        rep.setPeople(2);
        rep.setState(2);
        rep.setUnderworld(2);

        int bonus = gameLoopService.reputationBonus("Escort someone", rep);
        assertEquals(0, bonus);
    }
}
