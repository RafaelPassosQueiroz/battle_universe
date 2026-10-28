package com.example.projetomarvel;

import org.junit.Test;

public class BattleEngineTest {
    @Test public void meleeMovementAndFacing(){BattleEngineChecks.basics();}
    @Test public void commonPowers(){BattleEngineChecks.common();}
    @Test public void specialLifetimesAndPassives(){BattleEngineChecks.specialRules();}
    @Test public void reactionWindowsAndAir(){BattleEngineChecks.beamsAndStrikes();}
    @Test public void exactTemporalSnapshot(){BattleEngineChecks.rewind();}
    @Test public void scarletAllyLifecycle(){BattleEngineChecks.scarletAllies();}
    @Test public void allSixCustomPowers(){BattleEngineChecks.customs();}
    @Test public void interactionsAndBotDirections(){BattleEngineChecks.interactions();}
    @Test public void aiAndArenaInvariants(){BattleEngineChecks.simulations();}
}
