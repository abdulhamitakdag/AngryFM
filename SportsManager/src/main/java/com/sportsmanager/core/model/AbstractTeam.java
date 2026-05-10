package com.sportsmanager.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// takımların temel yapısı - kadro, antrenman, sezon istatistikleri burada
public abstract class AbstractTeam {

    private final UUID id;
    private final String name;
    private final List<AbstractPlayer> squad;
    private final List<AbstractCoach> coaches;
    private AbstractCoach activeCoach;
    private AbstractTactic currentTactic;
    private final List<AbstractPlayer> startingLineup = new ArrayList<>();

    // Sezon istatistikleri
    private int wins;
    private int draws;
    private int losses;
    private int goalsScored;
    private int goalsConceded;

    public AbstractTeam(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null or empty.");
        }
        this.id = UUID.randomUUID();
        this.name = name;
        this.squad = new ArrayList<>();
        this.coaches = new ArrayList<>();
    }

    // kadro geçerli mi kontrol eder, değilse hata fırlatır
    public abstract void validateLineup(List<AbstractPlayer> lineup);

    public abstract int getMaxSquadSize(); // futbol icin 23, voleybol icin 12 gibi

    // kadroya oyuncu ekler, zaten varsa veya kadro doluysa hata fırlatır
    public void addPlayer(AbstractPlayer player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        for (AbstractPlayer p : squad) {
            if (p.getId().equals(player.getId())) {
                throw new IllegalStateException("Player is already in the squad: " + player.getName());
            }
        }
        if (squad.size() >= getMaxSquadSize()) {
            throw new IllegalStateException(
                    "Squad is full (maximum " + getMaxSquadSize() + " players). " +
                            "Cannot add " + player.getName() + "."
            );
        }
        squad.add(player);
    }

    // oyuncuyu kadrodan çıkarır, bulunduysa true döner
    public boolean removePlayer(AbstractPlayer player) {
        return squad.remove(player);
    }

    // tüm kadro (sakatlar dahil)
    public List<AbstractPlayer> getSquad() {
        return Collections.unmodifiableList(squad);
    }

    // sakat olmayan, oynayabilecek oyuncular
    public List<AbstractPlayer> getAvailablePlayers() {
        List<AbstractPlayer> available = new ArrayList<>();
        for (AbstractPlayer p : squad) {
            if (!p.isInjured()) {
                available.add(p);
            }
        }
        return Collections.unmodifiableList(available);
    }

    // sakat oyuncuların listesi
    public List<AbstractPlayer> getInjuredPlayers() {
        List<AbstractPlayer> injured = new ArrayList<>();
        for (AbstractPlayer p : squad) {
            if (p.isInjured()) {
                injured.add(p);
            }
        }
        return Collections.unmodifiableList(injured);
    }

    // koç yönetimi

    public void addCoach(AbstractCoach coach) {
        if (coach == null) {
            throw new IllegalArgumentException("Coach cannot be null.");
        }
        coaches.add(coach);
    }

    public boolean removeCoach(AbstractCoach coach) {
        return coaches.remove(coach);
    }

    public List<AbstractCoach> getCoaches() {
        return Collections.unmodifiableList(coaches);
    }

    public AbstractCoach getActiveCoach() {
        if (activeCoach != null) return activeCoach;
        if (coaches.isEmpty()) return null;
        return coaches.get(0);
    }

    public void setActiveCoach(AbstractCoach coach) {
        if (coach != null && !coaches.contains(coach)) {
            throw new IllegalArgumentException("Coach is not in this team's staff: " + coach.getName());
        }
        this.activeCoach = coach;
    }

    // takıma antrenman yaptırır
    // koç varsa koçun metodunu çağırır, yoksa oyuncular kendi başına antrenman yapar (%80 yoğunlukla)
    // sakatlar atlanır
    public void runTrainingSession(double intensity) {
        double clampedIntensity = Math.max(0.0, Math.min(1.0, intensity));
        List<AbstractPlayer> trainees = getAvailablePlayers();

        if (trainees.isEmpty()) {
            return;
        }

        AbstractCoach active = getActiveCoach();
        if (active != null) {
            active.conductTraining(this, clampedIntensity);
        } else {
            // koç yoksa %80 yoğunlukla kendi başına antrenman
            double reduced = clampedIntensity * 0.80;
            for (AbstractPlayer p : trainees) {
                p.train(reduced);
            }
        }
    }

    // maç sonucunu kaydeder ve istatistikleri günceller
    public void recordResult(boolean won, boolean drew, int scored, int conceded) {
        if (won) {
            wins++;
        } else if (drew) {
            draws++;
        } else {
            losses++;
        }
        goalsScored    += scored;
        goalsConceded  += conceded;
    }

    // sezon istatistiklerini sıfırlar, yeni sezon başında çağrılmalı
    public void resetSeasonStats() {
        wins          = 0;
        draws         = 0;
        losses        = 0;
        goalsScored   = 0;
        goalsConceded = 0;
    }

    // save/load için istatistikleri dışarıdan set edebilmeyi sağlıyor
    public void restoreSeasonStats(int wins, int draws, int losses, int goalsScored, int goalsConceded) {
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalsScored = goalsScored;
        this.goalsConceded = goalsConceded;
    }

    public int getWins()          { return wins; }
    public int getDraws()         { return draws; }
    public int getLosses()        { return losses; }
    public int getGoalsScored()   { return goalsScored; }
    public int getGoalsConceded() { return goalsConceded; }
    public int getGoalDifference(){ return goalsScored - goalsConceded; }
    public int getMatchesPlayed() { return wins + draws + losses; }

    public int getAvgOvr(){

        int avgOvr = 0;

        for (AbstractPlayer p : squad) {
            if (p.getAttributes() != null) {
                avgOvr += p.getAttributes().getOverallRating();
            }
        }
        if (!squad.isEmpty())return avgOvr/squad.size();
        return 0;
    }

    public int getAvgOvr(int playersOnField){
        if (squad.isEmpty()) return 0;
        List<AbstractPlayer> sorted = new ArrayList<>(squad);
        sorted.sort((a, b) -> Integer.compare(ovrOf(b), ovrOf(a)));
        int n = Math.min(playersOnField, sorted.size());
        int total = 0;
        for (int i = 0; i < n; i++) {
            total += ovrOf(sorted.get(i));
        }
        return n == 0 ? 0 : total / n;
    }

    public int getAvailableAvgOvr(int playersOnField){
        List<AbstractPlayer> available = getAvailablePlayers();
        if (available.isEmpty()) return 0;
        List<AbstractPlayer> sorted = new ArrayList<>(available);
        sorted.sort((a, b) -> Integer.compare(ovrOf(b), ovrOf(a)));
        int n = Math.min(playersOnField, sorted.size());
        int total = 0;
        for (int i = 0; i < n; i++) {
            total += ovrOf(sorted.get(i));
        }
        return n == 0 ? 0 : total / n;
    }

    private static int ovrOf(AbstractPlayer p) {
        return p.getAttributes() != null ? p.getAttributes().getOverallRating() : 0;
    }


    public void autoSetLineup(int playersOnField) {
        startingLineup.clear();
        List<AbstractPlayer> availablePlayers = new ArrayList<>(getAvailablePlayers());
        availablePlayers.sort((a, b) -> Integer.compare(ovrOf(b), ovrOf(a)));

        List<AbstractPlayer> newStarting = new ArrayList<>();

        if (currentTactic != null) {
            Map<String, Integer> positionNeeds = currentTactic.getRequiredPositionNames();
            for (Map.Entry<String, Integer> entry : positionNeeds.entrySet()) {
                String reqPos = entry.getKey();
                int count = entry.getValue();
                for (int i = 0; i < count; i++) {
                    AbstractPlayer found = null;
                    for (AbstractPlayer p : availablePlayers) {
                        if (p.getPosition().equals(reqPos)) { found = p; break; }
                    }
                    if (found != null) {
                        newStarting.add(found);
                        availablePlayers.remove(found);
                    }
                }
            }
        }

        while (newStarting.size() < playersOnField && !availablePlayers.isEmpty()) {
            newStarting.add(availablePlayers.remove(0));
        }

        startingLineup.addAll(newStarting);
    }


    public List<AbstractPlayer> getStartingLineup() {
        List<AbstractPlayer> healthy = new ArrayList<>();
        for (AbstractPlayer p : startingLineup) {
            if (!p.isInjured()) healthy.add(p);
        }
        return Collections.unmodifiableList(healthy);
    }

    private void purgeInjuredFromLineup() {
        startingLineup.removeIf(AbstractPlayer::isInjured);
    }

    public List<AbstractPlayer> getBench() {
        List<AbstractPlayer> starting = getStartingLineup();
        List<AbstractPlayer> bench = new ArrayList<>();
        for (AbstractPlayer p : getAvailablePlayers()) {
            if (!starting.contains(p)) bench.add(p);
        }
        return Collections.unmodifiableList(bench);
    }

    public boolean swapStartingWithBench(AbstractPlayer out, AbstractPlayer in) {
        purgeInjuredFromLineup();
        if (in == null || in.isInjured() || startingLineup.contains(in)) return false;
        if (out == null) {
            startingLineup.add(in);
            return true;
        }
        if (!startingLineup.contains(out)) return false;
        int idx = startingLineup.indexOf(out);
        startingLineup.set(idx, in);
        return true;
    }

    public int getStartingAvgOvr() {
        List<AbstractPlayer> active = getStartingLineup();
        if (active.isEmpty()) return 0;
        int total = 0;
        for (AbstractPlayer p : active) total += ovrOf(p);
        return total / active.size();
    }

    // oyuncu adından starting lineup'a ekler (save/load için)
    public void restoreStartingLineup(List<String> names) {
        startingLineup.clear();
        for (String name : names) {
            for (AbstractPlayer p : squad) {
                if (p.getName().equals(name)) {
                    startingLineup.add(p);
                    break;
                }
            }
        }
    }

    // taktik

    public AbstractTactic getCurrentTactic() {
        return currentTactic;
    }

    public void setCurrentTactic(AbstractTactic tactic) {
        this.currentTactic = tactic;
    }

    // getter'lar

    public UUID getId()   { return id; }
    public String getName(){ return name; }
    public int getCurrentSquadSize() { return squad.size(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof AbstractTeam)) return false;
        AbstractTeam that = (AbstractTeam) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name + " [G:" + wins + " B:" + draws + " M:" + losses + "]";
    }
}
