package com.runemate.woodcutting;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingBot;

import java.awt.*;

@SuppressWarnings("deprecation")
public class Woodcutter extends LoopingBot implements PaintListener {

    private static final String[] AXE_NAMES = {
            "Bronze axe", "Iron axe", "Steel axe", "Black axe", "Mithril axe",
            "Adamant axe", "Rune axe", "Dragon axe", "Infernal axe", "Crystal axe", "3rd age axe"
    };
    private static final String[] LOG_NAMES = { "Willow logs" };
    private static final String[] TARGET_TREE = { "Willow tree", "Willow" };

    private static final Coordinate DRAYNOR_WILLOWS = new Coordinate(3087, 3231, 0);
    private static final Coordinate DRAYNOR_BANK = new Coordinate(3093, 3245, 0);
    private static final int WILLOW_RADIUS = 25;

    private enum State { FIND_TREE, MOVE_TO_TREE, CHOPPING, BANKING, RETURNING }
    private State state = State.FIND_TREE;

    private String status = "Starting";
    private long startMs;

    private GameObject currentTree;
    private Coordinate returnTo;

    @Override
    public void onStart(String... args) {
        setLoopDelay(350, 600);
        getEventDispatcher().addListener(this);
        startMs = System.currentTimeMillis();
        status = "Ready";
        log("Bot started.");
    }

    @Override
    public void onLoop() {
        log("Loop - State: " + state + " | Status: " + status);

        if (!hasAxeEquipped()) {
            var axe = Inventory.newQuery().names(AXE_NAMES).results().first();
            if (axe != null) {
                var def = axe.getDefinition();
                String axeName = def != null ? def.getName() : "Unknown Axe";
                log("Equipping axe: " + axeName);

                if (axe.interact("Wield") || axe.interact("Wear") || axe.interact("Equip")) {
                    status = "Equipping axe";
                    Execution.delay(400, 700);
                    return;
                } else {
                    log("Failed to equip axe.");
                }
            } else {
                log("No axe found in inventory.");
            }
        }

        if (Inventory.isFull() && state != State.BANKING) {
            Player me = Players.getLocal();
            if (me != null) returnTo = me.getPosition();
            state = State.BANKING;
            log("Inventory full, switching to BANKING state.");
        }

        switch (state) {
            case FIND_TREE -> doFindTree();
            case MOVE_TO_TREE -> doMoveToTree();
            case CHOPPING -> doChop();
            case BANKING -> doBank();
            case RETURNING -> doReturn();
        }
    }

    private void doFindTree() {
        status = "Finding willow";
        log("Searching for willow tree in radius...");
        currentTree = GameObjects.newQuery()
                .names(TARGET_TREE)
                .actions("Chop down", "Chop")
                .results()
                .nearest();

        if (isTreeValid(currentTree)) {
            log("Tree found at: " + currentTree.getPosition());
            state = State.MOVE_TO_TREE;
        } else {
            log("No valid tree found nearby. Walking to willow area...");
            stepTo(DRAYNOR_WILLOWS);
        }
    }

    private void doMoveToTree() {
        if (!isTreeValid(currentTree)) {
            log("Tree became invalid. Re-finding.");
            state = State.FIND_TREE;
            return;
        }

        Player me = Players.getLocal();
        Coordinate mePos = (me != null) ? me.getPosition() : null;
        if (mePos == null) return;

        if (mePos.distanceTo(currentTree) > 5) {
            status = "Approaching willow";
            log("Walking toward tree at " + currentTree.getPosition());
            stepTo(currentTree.getPosition());
            return;
        }

        status = "Clicking willow";
        log("Interacting with tree...");
        if (currentTree.interact("Chop down") || currentTree.interact("Chop")) {
            if (!Execution.delayUntil(this::isAnimating, 1000, 3500)) {
                status = "Repositioning (stuck)";
                log("Did not animate after clicking tree. Nudging.");
                nudgeToward(currentTree.getPosition());
            } else {
                state = State.CHOPPING;
                log("Started chopping.");
            }
        } else {
            log("Failed to click tree. Re-finding.");
            state = State.FIND_TREE;
        }
    }

    private void doChop() {
        if (Inventory.isFull()) {
            log("Inventory full while chopping. Going to bank.");
            state = State.BANKING;
            return;
        }
        if (!isAnimating()) {
            log("Not animating anymore. Tree may have despawned.");
            state = State.FIND_TREE;
            return;
        }
        status = "Chopping";
    }

    private void doBank() {
        status = "Banking";

        Player me = Players.getLocal();
        Coordinate mePos = (me != null) ? me.getPosition() : null;

        if (mePos == null || mePos.distanceTo(DRAYNOR_BANK) > 5) {
            log("Walking to bank...");
            stepTo(DRAYNOR_BANK);
            return;
        }

        if (!Bank.isOpen()) {
            log("Opening bank...");
            Bank.open();
            Execution.delayUntil(Bank::isOpen, 1000, 3000);
            return;
        }

        var logs = Inventory.newQuery().names(LOG_NAMES).results();
        if (!logs.isEmpty()) {
            logs.forEach(i -> i.interact("Deposit-All"));
            Execution.delay(300, 500);
            return;
        }

        Bank.close();
        Execution.delayUntil(() -> !Bank.isOpen(), 400, 1200);
        state = State.RETURNING;
        log("Finished banking. Returning to chop location.");
    }

    private void doReturn() {
        Coordinate target = (returnTo != null) ? returnTo : DRAYNOR_WILLOWS;
        status = "Returning to spot";

        Player me = Players.getLocal();
        Coordinate pos = (me != null) ? me.getPosition() : null;

        if (pos == null || pos.distanceTo(target) > 3) {
            log("Returning to: " + target);
            stepTo(target);
            return;
        }

        log("Returned to chop location.");
        returnTo = null;
        state = State.FIND_TREE;
    }

    private boolean hasAxeEquipped() {
        return Equipment.newQuery().names(AXE_NAMES).results().first() != null;
    }

    private boolean isTreeValid(GameObject tree) {
        return tree != null && tree.isValid() && inWillowArea(tree.getPosition());
    }

    private boolean inWillowArea(Coordinate c) {
        return c != null && c.distanceTo(DRAYNOR_WILLOWS) <= WILLOW_RADIUS;
    }

    private boolean isAnimating() {
        Player me = Players.getLocal();
        return me != null && me.getAnimationId() != -1;
    }

    private void stepTo(Coordinate target) {
        if (target == null) return;

        var path = BresenhamPath.buildTo(target);
        if (path != null) {
            boolean stepped = path.step();
            if (!stepped) {
                log("BresenhamPath failed to step.");
            }
        } else {
            log("BresenhamPath could not build path to " + target);
        }

        Execution.delay(250, 450);
    }

    private void nudgeToward(Coordinate target) {
        Player me = Players.getLocal();
        Coordinate pos = (me != null) ? me.getPosition() : null;
        if (pos == null || target == null) return;

        int dx = Integer.compare(target.getX(), pos.getX());
        int dy = Integer.compare(target.getY(), pos.getY());
        Coordinate oneStep = new Coordinate(pos.getX() + dx, pos.getY() + dy, pos.getPlane());
        stepTo(oneStep);
    }

    @Override
    public void onPaint(Graphics2D g) {
        long rt = System.currentTimeMillis() - startMs;
        int x = 10, y = 20, w = 280, h = 76;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        int yy = y + 18;
        g.drawString("Dicode Woodcutter v0.8", x + 10, yy); yy += 16;
        g.drawString("State: " + state + " | " + status, x + 10, yy); yy += 16;
        g.drawString("Runtime: " + format(rt), x + 10, yy);
    }

    private static String format(long ms) {
        long s = ms / 1000, m = s / 60, h = m / 60;
        return String.format("%02d:%02d:%02d", h, m % 60, s % 60);
    }

    private void log(String message) {
        System.out.println("[Woodcutter] " + message);
    }
}
