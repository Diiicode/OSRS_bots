package com.runemate.woodcutting;

import com.runemate.game.api.client.paint.PaintListener;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingBot;

import java.awt.*;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class Woodcutter extends LoopingBot implements PaintListener {

    private static final String[] AXE_NAMES = {
            "Bronze axe","Iron axe","Steel axe","Black axe","Mithril axe",
            "Adamant axe","Rune axe","Dragon axe","Infernal axe","Crystal axe","3rd age axe"
    };

    // Cut *general* trees in a small spot
    private static final String[] TARGET_TREES = {"Tree","Oak"};

    // Draynor bank booth tile
    private static final Coordinate DRAYNOR_BANK = new Coordinate(3093, 3245, 0);

    // Small chop radius around where you start
    private static final int CHOP_RADIUS = 10;

    // Simple fixed waypoint route from willows side → bank (and back).
    // These hug the open path, avoiding stalls/fences so Bresenham doesn't wander.
    private static final Coordinate[] TO_BANK = {
            new Coordinate(3086, 3231, 0), // south trees edge
            new Coordinate(3089, 3237, 0), // up the lane
            new Coordinate(3093, 3241, 0), // bank front
            DRAYNOR_BANK
    };

    private enum State { FIND_TREE, MOVE_TO_TREE, CHOPPING, BANKING, RETURNING }
    private State state = State.FIND_TREE;

    private String status = "Starting";
    private long startMs;

    private Coordinate chopCenter;      // center of our small spot (set at start)
    private GameObject currentTree;
    private Coordinate returnTo;        // exact tile saved when inv becomes full

    // waypoint indices (we pick the next one ahead of us)
    private int bankWpIdx = 0;
    private int backWpIdx = TO_BANK.length - 1;

    @Override
    public void onStart(String... args) {
        setLoopDelay(350, 600);
        getEventDispatcher().addListener(this);
        startMs = System.currentTimeMillis();

        Player me = Players.getLocal();
        if (me != null) chopCenter = me.getPosition(); // start spot
        status = "Ready";
        log("Chop center: " + chopCenter + " (r=" + CHOP_RADIUS + ")");
    }

    @Override
    public void onLoop() {
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
                }
            } else {
                log("No axe found in inventory. Stopping.");
                stop();
                return;
            }
        }

        if (Inventory.isFull() && state != State.BANKING) {
            Player me = Players.getLocal();
            if (me != null) returnTo = me.getPosition();  // remember exact tile
            state = State.BANKING;
            // seed waypoint from the *nearest* forward node
            bankWpIdx = nearestForwardIndex(TO_BANK, Players.getLocal() != null ? Players.getLocal().getPosition() : null, 0);
            log("Inventory full → BANKING (returnTo=" + returnTo + ")");
        }

        switch (state) {
            case FIND_TREE -> doFindTree();
            case MOVE_TO_TREE -> doMoveToTree();
            case CHOPPING -> doChop();
            case BANKING -> doBank();
            case RETURNING -> doReturn();
        }
    }

    // ---------- CHOP FLOW ----------

    private void doFindTree() {
        status = "Finding tree";
        Area work = (chopCenter != null) ? new Area.Circular(chopCenter, CHOP_RADIUS) : null;

        currentTree = GameObjects.newQuery()
                .names(TARGET_TREES)
                .actions("Chop down","Chop")
                .within(work)
                .results()
                .nearest();

        if (isTreeValid(currentTree)) {
            state = State.MOVE_TO_TREE;
            return;
        }

        // Drift back toward the exact center of our spot to keep us contained
        if (work != null) stepTo(work.getCenter());
        else stepTo(chopCenter);
    }

    private void doMoveToTree() {
        if (!isTreeValid(currentTree)) { state = State.FIND_TREE; return; }

        Player me = Players.getLocal();
        Coordinate mePos = (me != null) ? me.getPosition() : null;
        if (mePos == null) return;

        if (mePos.distanceTo(currentTree) > 5) {
            status = "Approaching tree";
            stepTo(currentTree.getPosition());
            return;
        }

        status = "Clicking tree";
        if (currentTree.interact("Chop down") || currentTree.interact("Chop")) {
            if (!Execution.delayUntil(this::isAnimating, 1000, 3500)) {
                status = "Reposition";
                nudgeToward(currentTree.getPosition());
            } else state = State.CHOPPING;
        } else {
            state = State.FIND_TREE;
        }
    }

    private void doChop() {
        if (Inventory.isFull()) { state = State.BANKING; return; }
        if (!isAnimating()) { state = State.FIND_TREE; return; }
        status = "Chopping";
    }

    // ---------- BANK FLOW (WAYPOINTS + DEPOSIT INVENTORY) ----------

    private void doBank() {
        status = "Banking";

        // Walk through fixed waypoints to the bank
        if (!atBank()) {
            stepTo(TO_BANK[bankWpIdx]);
            // advance index when close to current waypoint
            if (near(Players.getLocal() != null ? Players.getLocal().getPosition() : null, TO_BANK[bankWpIdx], 2) && bankWpIdx < TO_BANK.length - 1) {
                bankWpIdx++;
            }
            return;
        }

        // At the bank: open and dump inventory in one action
        if (!Bank.isOpen()) {
            Bank.open();
            Execution.delayUntil(Bank::isOpen, 800, 2500);
            return;
        }

        // One-shot deposit: inventory (axe is equipped already)
        Bank.depositInventory();
        Execution.delay(300, 500);

        // Close and start back route
        if (!Inventory.isFull()) {
            Bank.close();
            Execution.delayUntil(() -> !Bank.isOpen(), 400, 1200);
            state = State.RETURNING;
            // seed reverse waypoint from nearest behind node
            backWpIdx = nearestBackwardIndex(TO_BANK, Players.getLocal() != null ? Players.getLocal().getPosition() : null, TO_BANK.length - 1);
            log("Banked → RETURNING (backWpIdx=" + backWpIdx + ")");
        }
    }

    private void doReturn() {
        // Walk back along reverse waypoints to the exact fill-up tile
        if (!near(Players.getLocal() != null ? Players.getLocal().getPosition() : null, returnTo, 3)) {
            // step along reverse route first, then final tile
            if (backWpIdx > 0) {
                stepTo(TO_BANK[backWpIdx]);
                if (near(Players.getLocal() != null ? Players.getLocal().getPosition() : null, TO_BANK[backWpIdx], 2)) {
                    backWpIdx--;
                }
            } else {
                stepTo(returnTo != null ? returnTo : chopCenter);
            }
            status = "Returning";
            return;
        }

        // Re-arm our small spot around the last working tile and resume
        chopCenter = (returnTo != null) ? returnTo : chopCenter;
        returnTo = null;
        state = State.FIND_TREE;
        status = "Back at spot";
        log("Back at chop spot: " + chopCenter);
    }

    // ---------- helpers ----------

    private boolean hasAxeEquipped() {
        return Equipment.newQuery().names(AXE_NAMES).results().first() != null;
    }

    private boolean isTreeValid(GameObject tree) {
        return tree != null && tree.isValid() && inChopArea(tree.getPosition());
    }

    private boolean inChopArea(Coordinate c) {
        return chopCenter != null && c != null && c.distanceTo(chopCenter) <= CHOP_RADIUS;
    }

    private boolean isAnimating() {
        Player me = Players.getLocal();
        return me != null && me.getAnimationId() != -1;
    }

    private boolean atBank() {
        Player me = Players.getLocal();
        Coordinate pos = (me != null) ? me.getPosition() : null;
        return near(pos, DRAYNOR_BANK, 5);
    }

    private static boolean near(Coordinate a, Coordinate b, int dist) {
        return a != null && b != null && a.distanceTo(b) <= dist;
    }

    private void stepTo(Coordinate target) {
        if (target == null) return;
        var path = BresenhamPath.buildTo(target);
        if (path != null) path.step();
        Execution.delay(200, 350);
    }

    private void nudgeToward(Coordinate target) {
        Player me = Players.getLocal();
        Coordinate pos = (me != null) ? me.getPosition() : null;
        if (pos == null || target == null) return;
        int dx = Integer.compare(target.getX(), pos.getX());
        int dy = Integer.compare(target.getY(), pos.getY());
        stepTo(new Coordinate(pos.getX() + dx, pos.getY() + dy, pos.getPlane()));
    }

    private static int nearestForwardIndex(Coordinate[] route, Coordinate me, int startIdx) {
        if (me == null) return startIdx;
        int best = startIdx;
        double bestD = Double.MAX_VALUE;
        for (int i = startIdx; i < route.length; i++) {
            double d = me.distanceTo(route[i]);
            if (d < bestD) { bestD = d; best = i; }
        }
        return best;
    }

    private static int nearestBackwardIndex(Coordinate[] route, Coordinate me, int startIdx) {
        if (me == null) return startIdx;
        int best = startIdx;
        double bestD = Double.MAX_VALUE;
        for (int i = startIdx; i >= 0; i--) {
            double d = me.distanceTo(route[i]);
            if (d < bestD) { bestD = d; best = i; }
        }
        return best;
    }

    // ---- paint ----
    @Override
    public void onPaint(Graphics2D g) {
        long rt = System.currentTimeMillis() - startMs;
        int x = 10, y = 20, w = 280, h = 76;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        int yy = y + 18;
        g.drawString("Dicode Woodcutter v1.0", x + 10, yy); yy += 16;
        g.drawString("State: " + state + " | " + status, x + 10, yy); yy += 16;
        g.drawString(String.format("Runtime: %02d:%02d:%02d", (rt/3600000), (rt/60000)%60, (rt/1000)%60), x + 10, yy);
    }

    private void log(String m) { System.out.println("[Woodcutter] " + m); }
}


