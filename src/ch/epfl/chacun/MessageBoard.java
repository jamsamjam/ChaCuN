package ch.epfl.chacun;
import java.util.*;

import static ch.epfl.chacun.Preconditions.checkArgument;

public record MessageBoard(TextMaker textMaker, List<Message> messages) {

    public MessageBoard {
        messages = List.copyOf(messages);
    }

    Map<PlayerColor, Integer> points() {
        Map<PlayerColor, Integer> pointsMap = new HashMap<>();
        for (Message message : messages) {
            for (PlayerColor scorer : message.scorers()) {
                pointsMap.put(scorer, pointsMap.getOrDefault(scorer, 0) + message.points());
            }
        }
        return pointsMap;
    }

    MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        if (forest.isOccupied()) {
            Set<PlayerColor> scorers = forest.majorityOccupants();
            int points = forest.tileIds().size() * 2;
            int mushroomGroupCount = Area.mushroomGroupCount(forest);
            return this.withMessage(textMaker.playersScoredForest(scorers, points, mushroomGroupCount, forest.tileIds().size()));
        }
        return this;
    }

    MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        if (Area.hasMenhir(forest)) {
            return this.withMessage(textMaker.playerClosedForestWithMenhir(player));
        }
        return this;
    }

    MessageBoard withScoredRiver(Area<Zone.River> river){
        return null;
    }

    MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow){
        Set<Animal> cancelledAnimals = new HashSet<>();
        // Logic to calculate points, determine animals, and generate message
        //int points = calculatePointsForHuntingTrap(adjacentMeadow, cancelledAnimals);
        //if (points > 0) {
            //return this.withMessage(textMaker.playerScoredHuntingTrap(scorer, points, animalsMap(adjacentMeadow, cancelledAnimals)));
       // }
        return this;    }

    MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem){
        return null;
    }

    MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals){
        return null;
    }

    MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem){
        return null;
    }

    MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals){
        return null;
    }


    MessageBoard withScoredRaft(Area<Zone.Water> riverSystem){
        return null;
    }

    MessageBoard withWinners(Set<PlayerColor> winners, int points){
        return null;
    }

    private MessageBoard withMessage(String messageText) {
        List<Message> updatedMessages = new ArrayList<>(messages);
        //updatedMessages.add(new Message(messageText));
        return new MessageBoard(textMaker, updatedMessages);
    }

    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
        public Message {
            checkArgument(text!=null);
            checkArgument(points>=0);
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }
    }

}
