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
    private final Map<PlayerColor, String> nameColorMap;

    /**
     * Constructor of TextMakerFr.
     *
     * @param nameColorMap a table associating their name with the colors of the players
     */
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
        return (points == 0 || points == 1) ? STR."\{points} point" : STR."\{points} points";
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor scorer) {
        return STR."\{playerName(scorer)} a fermé une forêt contenant un menhir et peut donc placer"
                + " une tuile menhir.";
    }

    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)}"
                + STR." en tant qu'\{occupant(scorers)} d'une forêt composée de \{tileCount} tuiles"
                + STR."\{mushroom(mushroomGroupCount)}.";
    }


    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)}"
                + STR." en tant qu'\{occupant(scorers)} d'une rivière composée de \{tileCount} tuiles"
                + STR."\{fish(" et contenant ", fishCount)}.";
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        return STR."\{playerName(scorer)} a remporté \{points(points)} en plaçant la fosse à pieux"
                + STR." dans un pré dans lequel elle est entourée de \{animal(animals)}.";
    }

    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        return STR."\{playerName(scorer)} a remporté \{points(points)} en plaçant la pirogue"
                + STR." dans un réseau hydrographique contenant \{lake(lakeCount)}.";
    }

    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)}"
                + STR." en tant qu'\{occupant(scorers)} d'un pré contenant \{animal(animals)}.";
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)}"
                + STR." en tant qu'\{occupant(scorers)} d'un réseau hydrographique"
                + STR."\{fish(" contenant ", fishCount)}.";
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)}"
                + STR." en tant qu'\{occupant(scorers)} d'un pré"
                + STR." contenant la grande fosse à pieux entourée de \{animal(animals)}.";
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        return STR."\{name(scorers)} \{verb(scorers)} remporté \{points(points)}"
                + STR." en tant qu'\{occupant(scorers)} d'un réseau hydrographique"
                + STR." contenant le radeau et \{lake(lakeCount)}.";
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        return STR."\{name(winners)} \{verb(winners)} remporté la partie avec \{points(points)} !";
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
            for (int i = 1; i < names.size() - 1; i++)
                sb.append(", ").append(names.get(i));
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
        return names.size() == 1 ? "occupant·e majoritaire" : "occupant·e·s majoritaires";
    }

    private String mushroom(int mushroomGroupCount) {
        String text;
        if (mushroomGroupCount == 0) text = "";
        else if (mushroomGroupCount == 1) text = " et de 1 groupe de champignons";
        else text = STR." et de \{mushroomGroupCount} groupes de champignons";

        return text;
    }

    private String fish(String prefix, int fishCount) {
        String text;
        if (fishCount == 0) text = "";
        else if (fishCount == 1) text = STR."\{prefix}1 poisson";
        else text = STR."\{prefix}\{fishCount} poissons";

        return text;
    }

   private String lake(int lakeCount) {
        return lakeCount == 1 ? "1 lac" : STR."\{lakeCount} lacs";
   }

    private String animal(Map<Animal.Kind, Integer> animals) {
        List<String> dict = List.of("mammouth", "auroch", "cerf");

        List<Animal.Kind> kinds = animals.keySet().stream().filter(a -> a != TIGER).sorted().toList();
        List<Integer> counts = new ArrayList<>();
        kinds.forEach(k -> counts.add(animals.get(k)));

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < kinds.size(); i++) {
            sb.append(STR."\{counts.get(i)} \{dict.get(kinds.get(i).ordinal())}");
            if (counts.get(i) > 1) sb.append("s");

            if (i == kinds.size() - 2) sb.append(" et ");
            else if (i < kinds.size() - 2) sb.append(", ");
        }

        return sb.toString();
    }
}
