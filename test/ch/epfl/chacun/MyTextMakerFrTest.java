package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class MyTextMakerFrTest {
    @Test
    void animalWorks() {
        Map<Animal.Kind, Integer> animals = new HashMap<>();
        animals.put(Animal.Kind.TIGER, 3);
        animals.put(Animal.Kind.MAMMOTH, 10);
        animals.put(Animal.Kind.DEER, 1);
        animals.put(Animal.Kind.AUROCHS, 5);

        System.out.println(TextMakerFr.animal(animals));
    }
}