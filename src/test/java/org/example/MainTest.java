package org.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

    private final String[] resourcesBadTyped = new String[]{
            "fun-names",
            "param-names",
            "undeclared-fun",
            "var-fun-name",
            "var-fun-name2",
            "wrong-args",
    };

    @Test
    void badTyped() throws URISyntaxException, IOException {
        for (String name : resourcesBadTyped) {
            String str = Files.readString(Paths.get(MainTest.class.getResource("../../bad-typed/" + name).toURI()));
            assertThrows(RuntimeException.class, () -> Main.execute(str));
        }
    }
}