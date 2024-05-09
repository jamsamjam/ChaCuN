package ch.epfl.chacun;

import java.util.*;

import static ch.epfl.chacun.Animal.Kind.*;
import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Generates all the French text necessary for the ChaCuN graphical interface.
 *
 * @author Sam Lee (375535)
 */
public final class TextMakerFr implements TextMaker {
    Map<PlayerColor, String> nameColorMap;

    public TextMakerFr(Map<PlayerColor, String> nameColorMap) {
        this.nameColorMap = Map.copyOf(nameColorMap);
    }

    @Override
    public String playerName(PlayerColor playerColor) {
        return nameColorMap.get(playerColor);
    }

    @Override
    public String points(int points) {
        checkArgument(points >= 0);
        return points == 0 || points == 1 ? STR."\{points} point" : STR."\{points} points";
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        String name = playerName(player);
        return STR."\{name} a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
    }


    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        String mushroom = mushroomGroupCount == 0 ? "." : STR." et de \{mushroomGroupCount} groupe de champignons.";
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)} en tantqu'\{occupant(scorers)} d'une forêt composée de \{tileCount} tuiles\{mushroom}";
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        String fish = fishCount == 0 ? "." : STR." et contenant \{fishCount} poissons";
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)} en tant qu'\{occupant(scorers)} d'une rivière composée de \{tileCount} tuiles\{fish}";
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
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)} en tant qu'\{occupant(scorers)} d'un pré contenant \{animal(animals)}.";
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)} en tant qu'\{occupant(scorers)} d'un réseau hydrographique contenant \{fishCount} poissons";
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)} en tant qu'\{occupant(scorers)} d'un pré contenant la grande fosse à pieux entourée de \{animal(animals)}";
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)} en tant qu'\{occupant(scorers)} d'un réseau hydrographique contenant le radeau et \{lakeCount} lac.";
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        return STR."\{name(winners)} \{verb(winners)} remporté la partie avec \{points(points)}!";
    }

    @Override
    public String clickToOccupy() {
        return "Cliquez sur le pion ou la hutte que vous désirez placer, ou ici pour ne pas en placer.";
    }

    @Override
    public String clickToUnoccupy() {
        return "Cliquez sur le pion que vous désirez reprendre, ou ici pour ne pas en reprendre.";
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
            sb.append(names.getFirst());
            for (int i = 1; i < names.size() - 1; i++) {
                sb.append(", ").append(names.get(i));
            }
            sb.append(" et ").append(names.getLast());
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
        for (int i = 0; i < counts.size(); i++) {
            if (counts.get(i) != 0) index.add(i);
        }

        StringBuilder sb = getStringBuilder(index, counts, kinds);

        return sb.toString();
    }

    private StringBuilder getStringBuilder(List<Integer> index, List<Integer> counts, List<String> kinds) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < index.size(); i++) {
            sb.append(STR."\{counts.get(index.get(i))} \{kinds.get(index.get(i))}");
            if ((counts.get(index.get(i)) > 1) && !kinds.get(index.get(i)).equals("aurochs")) sb.append("s");

            if (index.size() == 2 && i == 0)
                sb.append(" et ");
            else if (i < index.size() - 2)
                sb.append(", ");
            else if (i == index.size() - 2)
                sb.append(" et ");
        }
        return sb;
    }
}
