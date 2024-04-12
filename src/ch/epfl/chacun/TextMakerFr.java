package ch.epfl.chacun;

import java.util.*;

import static ch.epfl.chacun.Animal.Kind.*;
import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Generates all the French text necessary for the ChaCuN graphical interface.
 *
 * @author Sam Lee (375535)
 * @author Gehna Yadav (379155)
 */
public final class TextMakerFr implements TextMaker {
    Map<String, PlayerColor> nameColorMap;

    public TextMakerFr(Map<String, PlayerColor> nameColorMap) {
        this.nameColorMap = Map.copyOf(nameColorMap);
    }

    @Override
    public String playerName(PlayerColor playerColor) {
        checkArgument(PlayerColor.ALL.contains(playerColor));

        return nameColorMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(playerColor))
                .map(Map.Entry::getKey)
                .findFirst()
                .get();
    }

    @Override
    public String points(int points) {
        return "points";
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        String name = playerName(player);
        return STR."\{name} a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
    }


    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        String mushroom = mushroomGroupCount > 0 ? "." : STR." et de \{mushroomGroupCount} groupe de champignons.";
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points} points en tantqu'\{occupant(scorers)} d'une forêt composée de \{tileCount} tuiles\{mushroom}";
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        String mushroom = fishCount > 0 ? "." : STR." et contenant \{fishCount} poissons";
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points} points en tant qu'\{occupant(scorers)} d'une rivière composée de \{tileCount} tuiles\{fishCount}";
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        return STR."\{scorer} a remporté \{points} points en plaçant la fosse à pieux dans un pré dans lequel elle est entourée de \{animal(animals)}.";
    }

    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        return STR."\{scorer} a remporté \{points} points en plaçant la pirogue dans un réseau hydrographique contenant \{lakeCount} lacs";
    }

    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return null;
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        return null;
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return null;
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        return null;
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        return null;
    }

    @Override
    public String clickToOccupy() {
        return null;
    }

    @Override
    public String clickToUnoccupy() {
        return null;
    }

    private List<String> playerNames(Set<PlayerColor> scorers) {
        List<PlayerColor> sortedScorers = new ArrayList<>(scorers);
        Collections.sort(sortedScorers);

        List<String> playerNames = new ArrayList<>();
        sortedScorers.forEach(scorer -> playerNames.add(playerName(scorer)));
        return playerNames;
    }

    private String name(Set<PlayerColor> scorers) {
        List<String> names = playerNames(scorers);

        if (names.size() == 1)
            return names.get(0);
        else if (names.size() == 2)
            return STR."\{names.get(0)} et \{names.get(1)}";
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(names.get(0));
            for (int i = 1; i < names.size() - 1; i++) {
                sb.append(", ").append(names.get(i));
            }
            sb.append(" et ").append(names.get(names.size() - 1));
            return sb.toString();
        }
    }

    private String verb(Set<PlayerColor> scorers) {
        List<String> names = playerNames(scorers);
        return names.size() == 1 ? "a" : "ont";
    }

    private String occupant(Set<PlayerColor> scorers) {
        List<String> names = playerNames(scorers);
        return names.size() == 1 ? "occupant·e majoritaire" : "occupant·es majoritaires";
    }

    private String animal(Map<Animal.Kind, Integer> animals) {
        List<String> kinds = List.of("mammouth", "aurochs", "cerf");
        List<Integer> counts = List.of(animals.getOrDefault(MAMMOTH, 0), // TODO 0 displayed?
                animals.getOrDefault(AUROCHS, 0),
                animals.getOrDefault(DEER, 0));

        // get the indexes of non-zero animal
        List<Integer> index = new ArrayList<>();
        for (var count : counts) {
            if (count != 0) index.add(count);
        }

        if (index.isEmpty())
            return null;
        else if (index.size() == 1)
            return STR."\{counts.get(index.get(0))} \{kinds.get(index.get(0))}";
        else if (index.size() == 2)
            return STR."\{counts.get(index.get(0))} \{kinds.get(index.get(0))} et \{counts.get(index.get(1))} \{kinds.get(index.get(1))}";
        else {
            return STR."\{counts.get(index.get(0))} \{kinds.get(index.get(0))}, \{counts.get(index.get(1))} \{kinds.get(index.get(1))} et \{counts.get(index.get(2))} \{kinds.get(index.get(2))}";
        }
    }
}
