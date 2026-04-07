package com.sportsmanager.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// takımların temel yapısı - kadro, antrenman, sezon istatistikleri burada
public abstract class AbstractTeam {

    private final UUID id;
    private final String name;
    private final List<AbstractPlayer> squad;
    private final List<AbstractCoach> coaches;
    private AbstractTactic currentTactic;

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
            throw new IllegalArgumentException("Oyuncu null olamaz.");
        }
        for (AbstractPlayer p : squad) {
            if (p.getId().equals(player.getId())) {
                throw new IllegalStateException("Bu oyuncu zaten kadroda: " + player.getName());
            }
        }
        if (squad.size() >= getMaxSquadSize()) {
            throw new IllegalStateException(
                    "Kadro dolu (maksimum " + getMaxSquadSize() + " oyuncu). " +
                            player.getName() + " eklenemiyor."
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
            throw new IllegalArgumentException("Koç null olamaz.");
        }
        coaches.add(coach);
    }

    public boolean removeCoach(AbstractCoach coach) {
        return coaches.remove(coach);
    }

    public List<AbstractCoach> getCoaches() {
        return Collections.unmodifiableList(coaches);
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

        if (!coaches.isEmpty()) {
            coaches.get(0).conductTraining(this); // koç varsa onun antrenmanını çağır
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

    public int getWins()          { return wins; }
    public int getDraws()         { return draws; }
    public int getLosses()        { return losses; }
    public int getGoalsScored()   { return goalsScored; }
    public int getGoalsConceded() { return goalsConceded; }
    public int getGoalDifference(){ return goalsScored - goalsConceded; }
    public int getMatchesPlayed() { return wins + draws + losses; }

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
