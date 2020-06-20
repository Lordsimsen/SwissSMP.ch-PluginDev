package ch.swisssmp.knightstournament;

import java.util.*;

import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import ch.swisssmp.utils.Random;
import ch.swisssmp.utils.SwissSMPUtils;
import ch.swisssmp.utils.SwissSMPler;

public class Tournament implements Listener {

    private static final Random random = new Random();

    private static final HashMap<UUID, Tournament> tournaments = new HashMap<UUID, Tournament>();
    private static final Stack<Scoreboard> unusedScoreboards = new Stack<>();

    private final KnightsArena arena;
    private final SwissSMPler master;
    private final List<Player> registeredPlayers = new ArrayList<Player>();
    private TournamentParticipant[] participants;
    private TournamentBracket bracket;

    private Scoreboard scoreboard;
    private Objective objective;
    private Team teamRed;
    private Team teamBlue;

    private Duel runningDuel = null;

    private boolean running = false;
    private boolean finished = false;

    private Tournament(KnightsArena arena, Player master) {
        this.arena = arena;
        this.master = SwissSMPler.get(master);
    }

    private void initialize() {
        tournaments.put(master.getUniqueId(), this);

        this.scoreboard = unusedScoreboards.size() > 0
                ? unusedScoreboards.pop()
                : Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("player_list", "dummy", ChatColor.DARK_RED + "Ritterspiel Punkte", RenderType.INTEGER);

        Team teamRed = this.scoreboard.registerNewTeam("red");
        teamRed.setColor(ChatColor.RED);
        this.teamRed = teamRed;
        Team teamBlue = this.scoreboard.registerNewTeam("blue");
        teamBlue.setColor(ChatColor.BLUE);
        this.teamBlue = teamBlue;

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        arena.runTournament(this);
        Bukkit.getPluginManager().registerEvents(this, KnightsTournamentPlugin.plugin);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast In Kürze startet ein §cRitterspiel!");
    }

    public static Tournament initialize(KnightsArena arena, Player master) {
        if (arena.getTournament() != null) {
            master.sendMessage(KnightsTournamentPlugin.prefix + " In dieser Arena läuft bereits ein Turnier.");
            return null;
        }
        Tournament result = new Tournament(arena, master);
        result.initialize();
        return result;
    }

    public SwissSMPler getMaster() {
        return this.master;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Team getTeamRed() {
        return teamRed;
    }

    public Team getTeamBlue() {
        return teamBlue;
    }

    public Optional<TournamentParticipant> getParticipant(Player player) {
        return participants != null
                ? Arrays.stream(participants).filter(p -> p.getPlayerUUID().equals(player.getUniqueId())).findAny()
                : Optional.empty();
    }

    public boolean isRunning() {
        return running;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onHit(EntityDamageByLanceAttackEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player damageDealer = event.getDamager();
        Player damagee = (Player) event.getEntity();

        if (!this.registeredPlayers.contains(damageDealer) && !this.registeredPlayers.contains(damagee)) return;
        if (runningDuel == null) {
            event.setCancelled(true);
            return;
        }
        TournamentParticipant participantOne = this.runningDuel.getParticipant(damageDealer);
        TournamentParticipant participantTwo = this.runningDuel.getParticipant(damagee);
        if (participantOne == null || participantTwo == null) {
            event.setCancelled(true);
            return;
        }
        this.runningDuel.onHit(event);
        if (event.isCancelled()) return;
        event.setChargeEnds(true);
        ItemMeta lanceMeta = event.getLance().getItemMeta();
        Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.getInstance(), () -> {
            event.getLance().setItemMeta(lanceMeta);
        }, 1L);
    }


    /**
     * Cancel event if either entity is a participant
     *
     * @param event
     */
    @EventHandler
    private void onEntityDamagesEntity(EntityDamageByEntityEvent event) {

        // skip if it is damage by lance
        if (event instanceof EntityDamageByLanceAttackEvent) return;

        if ((event.getEntity() instanceof Player)) {
            Player damagee = (Player) event.getEntity();
            if (this.registeredPlayers.contains(damagee)) {
                event.setCancelled(true);
            }
        }

        if ((event.getDamager() instanceof Player)) {
            Player damageDealer = (Player) event.getDamager();
            if (this.registeredPlayers.contains(damageDealer)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onEntityDismount(EntityDismountEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        Player player = (Player) entity;
        if (!this.registeredPlayers.contains(player)) return;
        if (this.runningDuel == null) return;
        if (this.runningDuel.isParticipating(player) && !this.runningDuel.isDecided())
            this.runningDuel.win(this.runningDuel.getOpponent(player), this.runningDuel.getParticipant(player));
    }

    @EventHandler
    private void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (this.runningDuel == null) return;
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        if (!this.runningDuel.isParticipating(player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (this.runningDuel != null) {
            if (runningDuel.isParticipating(event.getPlayer())) {
                runningDuel.win(runningDuel.getOpponent(event.getPlayer()), runningDuel.getParticipant(event.getPlayer()));
            }
        }
        this.registeredPlayers.remove(event.getPlayer());
    }

    @EventHandler
    private void onRegionExit(RegionLeaveEvent event) {
        String regionId = event.getRegion().getId();
        if (!regionId.equalsIgnoreCase(arena.getArenaRegion())) {
            return;
        }

        Player player = event.getPlayer();
        if (!this.registeredPlayers.contains(player)) {
            return;
        }

        this.leave(player);
    }

    public void announce(String title, String subtitle) {
        for (Entity entity : this.master.getWorld().getNearbyEntities(this.master.getLocation(), 50, 50, 50)) {
            if (entity instanceof Player) {
                SwissSMPler.get((Player) entity).sendTitle(title, subtitle);
            }
        }
    }

    public void broadcast(String message) {
        for (Entity entity : this.master.getWorld().getNearbyEntities(this.master.getLocation(), 50, 50, 50)) {
            if (entity instanceof Player) {
                SwissSMPler.get((Player) entity).sendMessage(message);
            }
        }
    }


    public void join(Player player) {
        if (player == null) return;
        SwissSMPler swissSMPler = SwissSMPler.get(player);
        if (this.registeredPlayers.contains(player)) {
            swissSMPler.sendActionBar("§cBereits angemeldet.");
            return;
        }
        if (running) {
            swissSMPler.sendActionBar("§cTurnier bereits gestartet.");
            return;
        }
        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof Horse)) {
            swissSMPler.sendActionBar("§cDu brauchst ein Pferd.");
            return;
        }
        this.registeredPlayers.add(player);

        Score score = objective.getScore(player.getName());
        if (!score.isScoreSet()) {
            score.setScore(0);
        }

        player.setScoreboard(scoreboard);

        swissSMPler.sendActionBar("§aZum Turnier angemeldet.");

        master.sendActionBar("§E" + this.registeredPlayers.size() + " Teilnehmer");
    }

    public void leave(Player player) {
        if (player == null) return;

        if (!this.registeredPlayers.contains(player)) return;
        this.registeredPlayers.remove(player);

        if (this.runningDuel != null && this.runningDuel.isParticipating(player)) {
            this.runningDuel.win(this.runningDuel.getOpponent(player), this.runningDuel.getParticipant(player));
        }

        this.getParticipant(player).ifPresent(TournamentParticipant::setOut);

        teamRed.removeEntry(player.getName());
        teamBlue.removeEntry(player.getName());
        scoreboard.resetScores(player.getName());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        SwissSMPler swissSMPler = SwissSMPler.get(player);
        swissSMPler.sendActionBar("§ERitterturnier verlassen.");

        master.sendActionBar("§E" + this.registeredPlayers.size() + " Teilnehmer");
    }

    private boolean prepareTournament() {
        int participantsCount = this.registeredPlayers.size();
        if (this.registeredPlayers.size() < 2) {
            this.master.sendActionBar("§cNicht genügend Teilnehmer.");
            return false;
        }
        int power = participantsCount == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(participantsCount - 1);
        int maxParticipants = (int) Math.pow(2, power);
        this.master.sendMessage("[§4Ritterspiele§r] Initiiere Turnier mit " + maxParticipants + " Plätzen.");
        participants = new TournamentParticipant[maxParticipants];
        Player player;
        for (int i = 0; i < participants.length; i++) {
            if (i < this.registeredPlayers.size()) {
                player = this.registeredPlayers.get(i);
            } else {
                player = null;
            }
            participants[i] = new TournamentParticipant(player);
        }
        return true;
    }

    public boolean start() {
        if (running) return false;
        if (!this.prepareTournament()) return false;
        this.running = true;
        this.bracket = new TournamentBracket(this, this.participants);
        Duel firstDuel = this.bracket.getNextDuel();
        this.runningDuel = firstDuel;
        Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.plugin, firstDuel::prepare, 200L);
        this.announce("Turnier startet", this.registeredPlayers.size() + " Teilnehmer");
        this.arena.playBeginSound();
        for (Player player : this.registeredPlayers) {
            Bukkit.dispatchCommand(SwissSMPUtils.getPluginSender(), "advancement grant " + player.getName() + " only swisssmp:events/knights_tournament/participate_in_tournament");
        }
        return true;
    }

    public void concludeDuel(Duel duel, TournamentParticipant lastWinner, long timeout) {
        if (this.runningDuel == duel) this.runningDuel = null;

        Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.getInstance(), () -> {
            proceed(lastWinner);
        }, timeout);
    }

    public void addScore(TournamentParticipant participant) {
        // give +1 score
        Player player = participant != null ? participant.getPlayer() : null;
        if (player != null) {
            Score score = objective.getScore(player.getName());
            score.setScore(score.getScore() + 1);
        }
    }

    private void proceed(TournamentParticipant lastWinner) {
        // attempt to get the next duel
        this.runningDuel = this.bracket.getNextDuel();
        if (this.runningDuel != null) {
            // run the next duel
            this.runningDuel.prepare();
            return;
        }

        // no more duels left, complete the tournament
        announceWinner(lastWinner);
        Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.plugin, this::complete, 60L);
    }

    public void showScores() {
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void clearTeams() {
        for (String entry : teamRed.getEntries()) {
            teamRed.removeEntry(entry);
        }
        for (String entry : teamBlue.getEntries()) {
            teamBlue.removeEntry(entry);
        }
    }

    private void announceWinner(TournamentParticipant lastWinner) {
        if (lastWinner == null) {
            return;
        }
        Player player = Bukkit.getPlayer(lastWinner.getPlayerUUID());
        if (player == null) {
            return;
        }
        this.arena.playEndSound();
        this.announce(player.getDisplayName(), "hat das Turnier gewonnen!");
        if (this.registeredPlayers.size() >= 8) {
            Bukkit.dispatchCommand(SwissSMPUtils.getPluginSender(), "advancement grant " + player.getName() + " only swisssmp:events/knights_tournament/win_tournament");
        }
        Location location = player.getLocation();
        for (int i = 0; i < 10; i++) {
            Bukkit.getScheduler().runTaskLater(KnightsTournamentPlugin.plugin, () -> spawnFirework(location), i * 5L);
        }
    }

    private void spawnFirework(Location location) {
        Vector randomVector = random.insideUnitSphere().multiply(5f);
        Firework firework = (Firework) location.getWorld().spawnEntity(location.clone().add(randomVector.getX(), randomVector.getY() + 10, randomVector.getZ()), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        //Get the type
        int rt = random.nextInt(5) + 1;
        Type type = Type.BALL;
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;

        //Get our random colours   
        Color c1 = Color.RED;
        Color c2 = Color.WHITE;

        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(this.random.nextBoolean()).build();

        //Then apply the effect to the meta
        fireworkMeta.addEffect(effect);

        //Generate some random power and set it
        int rp = random.nextInt(2) + 1;
        fireworkMeta.setPower(rp);

        //Then apply this to our rocket
        firework.setFireworkMeta(fireworkMeta);
    }

    public void complete() {
        if (finished) return;
        finished = true;
        finish();
    }

    public void cancel() {
        if (finished) return;
        finished = true;
        if (this.runningDuel != null) {
            this.runningDuel.cancel();
        }
        finish();
    }

    private void finish() {
        this.announce("", "Turnier beendet.");
        this.broadcast("[§4Ritterspiele§r] §ETurnier beendet. Danke für deine Teilnahme!");
        teamRed.unregister();
        teamBlue.unregister();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        scoreboard.clearSlot(DisplaySlot.BELOW_NAME);

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        objective.unregister();

        tournaments.remove(this.master.getUniqueId());

        this.arena.runTournament(null);

        HandlerList.unregisterAll(this);

        unusedScoreboards.push(scoreboard);
    }

    public KnightsArena getArena() {
        return this.arena;
    }

    public static Tournament get(Player player) {
        return tournaments.get(player.getUniqueId());
    }
}
