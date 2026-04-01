package com.sportsmanager.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Takımların temel yapısını tanımlar.
 * Kadro yönetimi, antrenman ve sezon istatistiklerini barındırır.
 */
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
        this.id = UUID.randomUUID();
        this.name = name;
        this.squad = new ArrayList<>();
        this.coaches = new ArrayList<>();
    }

    // ── Abstract hook'lar ─────────────────────────────────────────────────────

    /**
     * Verilen kadronun bu spor için geçerli bir maç kadrosu oluşturup
     * oluşturmadığını doğrular.
     * Geçersizse IllegalArgumentException fırlatır.
     */
    public abstract void validateLineup(List<AbstractPlayer> lineup);

    /**
     * Bu spor için izin verilen maksimum kadro büyüklüğü.
     * Futbol için 23, Voleybol için 12 gibi.
     */
    public abstract int getMaxSquadSize();

    // ── Kadro yönetimi ────────────────────────────────────────────────────────

    /**
     * Kadroya oyuncu ekler.
     * Aynı oyuncu zaten kadroda varsa veya kadro doluysa hata fırlatır.
     */
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

    /**
     * Oyuncuyu kadrodan çıkarır.
     * @return oyuncu bulunup çıkarıldıysa true
     */
    public boolean removePlayer(AbstractPlayer player) {
        return squad.remove(player);
    }

    /**
     * Tüm kadroyu döndürür (sakat olanlar dahil).
     */
    public List<AbstractPlayer> getSquad() {
        return Collections.unmodifiableList(squad);
    }

    /**
     * Sadece sakat olmayan, oynayabilecek oyuncuları döndürür.
     */
    public List<AbstractPlayer> getAvailablePlayers() {
        List<AbstractPlayer> available = new ArrayList<>();
        for (AbstractPlayer p : squad) {
            if (!p.isInjured()) {
                available.add(p);
            }
        }
        return Collections.unmodifiableList(available);
    }

    /**
     * Sakat oyuncuların listesini döndürür.
     */
    public List<AbstractPlayer> getInjuredPlayers() {
        List<AbstractPlayer> injured = new ArrayList<>();
        for (AbstractPlayer p : squad) {
            if (p.isInjured()) {
                injured.add(p);
            }
        }
        return Collections.unmodifiableList(injured);
    }

    // ── Koç yönetimi ──────────────────────────────────────────────────────────

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

    // ── Antrenman ─────────────────────────────────────────────────────────────

    /**
     * Takıma antrenman yaptırır.
     *
     * Koç varsa: koçun conductTraining() metodu çağrılır (specialty çarpanları devreye girer).
     * Koç yoksa: oyuncular bağımsız antrenman yapar, yoğunluk %80'e düşer.
     *
     * Sakat oyuncular her iki durumda da atlanır.
     *
     * @param intensity antrenman yoğunluğu (0.0 ile 1.0 arası)
     */
    public void runTrainingSession(double intensity) {
        double clampedIntensity = Math.max(0.0, Math.min(1.0, intensity));
        List<AbstractPlayer> trainees = getAvailablePlayers();

        if (trainees.isEmpty()) {
            return;
        }

        if (!coaches.isEmpty()) {
            // Koç var: koçun conductTraining'ini çağır
            coaches.get(0).conductTraining(this);
        } else {
            // Koç yok: bağımsız antrenman, %80 yoğunluk cezası
            double reduced = clampedIntensity * 0.80;
            for (AbstractPlayer p : trainees) {
                p.train(reduced);
            }
        }
    }

    // ── Sezon istatistikleri ──────────────────────────────────────────────────

    /**
     * Bir maç sonucunu kaydeder ve istatistikleri günceller.
     *
     * @param won       bu takım kazandıysa true
     * @param drew      beraberlik olduysa true (won true ise bu parametre göz ardı edilir)
     * @param scored    bu takımın attığı gol/puan
     * @param conceded  bu takımın yediği gol/puan
     */
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

    /**
     * Tüm sezon istatistiklerini sıfırlar.
     * Yeni sezon başında çağrılmalıdır.
     */
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

    // ── Taktik ────────────────────────────────────────────────────────────────

    public AbstractTactic getCurrentTactic() {
        return currentTactic;
    }

    public void setCurrentTactic(AbstractTactic tactic) {
        this.currentTactic = tactic;
    }

    // ── Temel getter'lar ──────────────────────────────────────────────────────

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
