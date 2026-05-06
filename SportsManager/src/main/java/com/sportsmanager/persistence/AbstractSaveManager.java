package com.sportsmanager.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sportsmanager.core.model.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.Set;
import java.util.HashSet;

// save/load işlemlerinin temel yapısı burası
// dosya okuma yazma gibi ortak kısımlar burada, spora özel dönüşümler alt sınıflara bırakıldı
// yeni bi spor eklenince bundan extend edip abstract metodları doldurmak yeterli
public abstract class AbstractSaveManager {

    private final Path saveDirectory;
    private final ObjectMapper mapper;

    public AbstractSaveManager() {
        String appData = System.getenv("APPDATA");
        if (appData == null) {
            // windows değilse home klasörüne düşüyor
            appData = System.getProperty("user.home");
        }
        this.saveDirectory = Paths.get(appData, "AngryFM", "saves");
        this.mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public AbstractSaveManager(Path saveDirectory) {
        this.saveDirectory = saveDirectory;
        this.mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // bunları her spor kendi implemente edecek
    public abstract String getSportType();
    protected abstract AbstractLeague createLeague(String name);
    protected abstract AbstractTeam createTeam(String name);
    protected abstract GameState.PlayerData convertPlayer(AbstractPlayer player);
    protected abstract AbstractPlayer restorePlayer(GameState.PlayerData pd);
    protected abstract AbstractCoach restoreCoach(GameState.CoachData cd);
    protected abstract AbstractTactic restoreTactic(GameState.TacticData td);

    // dosya işlemleri - bunlar her sporda aynı olduğu için buraya yazdık

    public Path getSaveDirectory()
        { return saveDirectory; }

    // save isminde path traversal olmasın diye kontrol ediyoruz
    // .. / \ gibi şeyler varsa patlıyor
    private void validateSaveName(String saveName) {
        if (saveName == null || saveName.isBlank()) {
            throw new IllegalArgumentException("Save ismi boş olamaz.");
        }
        if (saveName.contains("..") || saveName.contains("/") || saveName.contains("\\")) {
            throw new IllegalArgumentException("Save isminde geçersiz karakter var: " + saveName);
        }
    }

    // state'i json olarak diske yazıyor
    public void save(GameState state, String saveName) throws IOException {
        validateSaveName(saveName);
        Files.createDirectories(saveDirectory);
        Path savePath = saveDirectory.resolve(saveName + ".json");
        mapper.writeValue(savePath.toFile(), state);
    }

    // diskten json okuyup GameState döndürüyor
    // bozuk json gelirse anlaşılır hata mesajı veriyor
    public GameState load(String saveName) throws IOException {
        validateSaveName(saveName);
        Path savePath = saveDirectory.resolve(saveName + ".json");
        if (!Files.exists(savePath)) {
            throw new IOException("Save dosyası bulunamadı: " + savePath);
        }
        try {
            return mapper.readValue(savePath.toFile(), GameState.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IOException("Save dosyası bozuk, okunamadı: " + saveName, e);
        }
    }

    public boolean delete(String saveName) throws IOException {
        validateSaveName(saveName);
        Path savePath = saveDirectory.resolve(saveName + ".json");
        return Files.deleteIfExists(savePath);
    }

    // klasördeki bütün save dosyalarını bulup isimlerini döndürüyor
    public List<String> listSaves() throws IOException {
        if (!Files.exists(saveDirectory)) {
            return Collections.emptyList();
        }
        List<String> saves = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(saveDirectory, "*.json")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                saves.add(fileName.substring(0, fileName.length() - 5));
            }
        }
        Collections.sort(saves);
        return saves;
    }

    // lig objesini GameState'e çeviriyor, kaydetme için kullanılıyor
    // takımları fikstürleri falan tek tek dönüştürüyor
    // aynı isimde takım varsa hata fırlatıyor çünkü restore sırasında karışıyor
    public GameState createState(AbstractLeague league, String userTeamName) {
        GameState state = new GameState();
        state.setSaveVersion(1);
        state.setSportType(getSportType());
        state.setLeagueName(league.getName());
        state.setCurrentWeek(league.getCurrentWeek());
        state.setUserTeamName(userTeamName);

        Set<String> seenNames = new HashSet<>();
        List<GameState.TeamData> teamDataList = new ArrayList<>();
        for (AbstractTeam team : league.getTeams()) {
            if (!seenNames.add(team.getName())) {
                throw new IllegalStateException("Aynı isimde iki takım var: " + team.getName());
            }
            teamDataList.add(convertTeam(team));
        }
        state.setTeams(teamDataList);

        List<GameState.FixtureData> fixtureDataList = new ArrayList<>();
        for (Fixture fixture : league.getFixtures()) {
            fixtureDataList.add(convertFixture(fixture));
        }
        state.setFixtures(fixtureDataList);

        return state;
    }

    // GameState'den ligi geri oluşturuyor, yükleme için kullanılıyor
    // önce takımları oluşturuyor sonra fikstürü generate edip oynanan maçları tekrar kaydediyor
    // generate deterministik olduğu için aynı takım sırasıyla aynı fikstür çıkıyor
    public AbstractLeague restoreLeague(GameState state) {
        Map<String, AbstractTeam> teamsByName = new LinkedHashMap<>();
        List<AbstractTeam> teamList = new ArrayList<>();

        for (GameState.TeamData td : state.getTeams()) {
            if (teamsByName.containsKey(td.getName())) {
                throw new IllegalStateException("Save dosyasında aynı isimde iki takım var: " + td.getName());
            }
            AbstractTeam team = restoreTeam(td);
            teamsByName.put(td.getName(), team);
            teamList.add(team);
        }

        AbstractLeague league = createLeague(state.getLeagueName());
        league.generateFixtures(teamList);

        // oynanan maçları sırayla geri kaydediyoruz
        for (GameState.FixtureData fd : state.getFixtures()) {
            if (fd.isPlayed()) {
                AbstractTeam home = teamsByName.get(fd.getHomeTeamName());
                AbstractTeam away = teamsByName.get(fd.getAwayTeamName());
                MatchResult result = new MatchResult(home, away, fd.getHomeScore(), fd.getAwayScore());
                league.recordResult(result);
            }
        }

        // haftayı doğru yere getiriyoruz — advanceWeek() yerine direkt set ediyoruz,
        // çünkü advanceWeek() injury decrement yapar ve save'den restore edilen
        // gamesRemaining değerleri fazladan azaltılırdı (double-decrement bug)
        league.setCurrentWeekDirect(state.getCurrentWeek());

        return league;
    }

    // ortak dönüşüm metodları
    // alt sınıflar isterse override edebilir ama genelde gerek olmuyor

    // takımı dto'ya çeviriyor, oyuncuları koçları taktikleri hepsini topluyor
    protected GameState.TeamData convertTeam(AbstractTeam team) {
        GameState.TeamData td = new GameState.TeamData();
        td.setName(team.getName());

        List<GameState.PlayerData> players = new ArrayList<>();
        for (AbstractPlayer player : team.getSquad()) {
            players.add(convertPlayer(player));
        }
        td.setPlayers(players);

        List<GameState.CoachData> coaches = new ArrayList<>();
        for (AbstractCoach coach : team.getCoaches()) {
            coaches.add(convertCoach(coach));
        }
        td.setCoaches(coaches);

        if (team.getCurrentTactic() != null) {
            td.setTactic(convertTactic(team.getCurrentTactic()));
        }

        // sezon istatistiklerini de kaydediyoruz
        td.setWins(team.getWins());
        td.setDraws(team.getDraws());
        td.setLosses(team.getLosses());
        td.setGoalsScored(team.getGoalsScored());
        td.setGoalsConceded(team.getGoalsConceded());

        return td;
    }

    // dto'dan takımı geri oluşturuyor
    protected AbstractTeam restoreTeam(GameState.TeamData td) {
        AbstractTeam team = createTeam(td.getName());

        for (GameState.PlayerData pd : td.getPlayers()) {
            team.addPlayer(restorePlayer(pd));
        }

        for (GameState.CoachData cd : td.getCoaches()) {
            team.addCoach(restoreCoach(cd));
        }

        if (td.getTactic() != null) {
            team.setCurrentTactic(restoreTactic(td.getTactic()));
        }

        // sezon istatistikleri, maçlar league.recordResult ile tekrar işlendiği için
        // burada dışarıdan set etmeye gerek yok, yoksa istatistikler ikiye katlanır.

        return team;
    }

    // koç dönüşümü, bütün sporlarda aynı fieldlar olduğu için buraya yazdık
    protected GameState.CoachData convertCoach(AbstractCoach coach) {
        GameState.CoachData cd = new GameState.CoachData();
        cd.setName(coach.getName());
        cd.setAge(coach.getAge());
        cd.setGender(coach.getGender().name());
        cd.setSpecialty(coach.getSpecialty().name());
        cd.setCoachingLevel(coach.getCoachingLevel());
        return cd;
    }

    // taktik dönüşümü, bu da bütün sporlarda ortak
    protected GameState.TacticData convertTactic(AbstractTactic tactic) {
        GameState.TacticData td = new GameState.TacticData();
        td.setName(tactic.getName());
        td.setAttackingWeight(tactic.getAttackingWeight());
        td.setPressureIntensity(tactic.getPressureIntensity());
        return td;
    }

    // fikstür dönüşümü
    protected GameState.FixtureData convertFixture(Fixture fixture) {
        GameState.FixtureData fd = new GameState.FixtureData();
        fd.setWeek(fixture.getWeek());
        fd.setHomeTeamName(fixture.getHomeTeam().getName());
        fd.setAwayTeamName(fixture.getAwayTeam().getName());
        fd.setPlayed(fixture.isPlayed());
        if (fixture.isPlayed() && fixture.getResult() != null) {
            fd.setHomeScore(fixture.getResult().getHomeScore());
            fd.setAwayScore(fixture.getResult().getAwayScore());
        }
        return fd;
    }

    // null gelirse 0 döndürüyor, yoksa unboxing sırasında patlıyor
    protected static double safe(Double value) {
        return value == null ? 0.0 : value;
    }
}
