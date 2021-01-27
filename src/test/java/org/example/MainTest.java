package org.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

    void testBadTyped(String name) throws URISyntaxException, IOException {
        String str = Files.readString(Paths.get(MainTest.class.getResource("../../bad-typed/" + name).toURI()));
        assertThrows(RuntimeException.class, () -> Main.execute(str));
    }

    @Test
    void testFunNamesBadTyped() throws URISyntaxException, IOException {
            String name = "fun-names";
            testBadTyped(name);
    }

    @Test
    void testParamNamesBadTyped() throws URISyntaxException, IOException {
            String name = "param-names";
            testBadTyped(name);
    }

    @Test
    void testUndeclaredFunBadTyped() throws URISyntaxException, IOException {
            String name = "undeclared-fun";
            testBadTyped(name);
    }

    @Test
    void testVarFunNameBadTyped() throws URISyntaxException, IOException {
            String name = "var-fun-name";
            testBadTyped(name);
    }

    @Test
    void testVarFunName2BadTyped() throws URISyntaxException, IOException {
            String name = "var-fun-name2";
            testBadTyped(name);
    }

    @Test
    void testWrongArgsBadTyped() throws URISyntaxException, IOException {
            String name = "wrong-args";
            testBadTyped(name);
    }
 
    void testWellTyped(String name) throws URISyntaxException, IOException {
        String str =
                Files.readString(Paths.get(MainTest.class.getResource("../../well-typed/" + name).toURI()));
        String expected =
                Files.readString(Paths.get(MainTest.class.getResource("../../well-typed/" + name + ".out")
                        .toURI())).trim();
        Main.execute(str);
        String out = Files.readString(Paths.get("out.txt")).trim();
        assertEquals(expected, out);
    }

    @Test
    void testConstWellTyped() throws URISyntaxException, IOException {
        String name = "const";
        testWellTyped(name);
    }

    @Test
    void testFactorialWellTyped() throws URISyntaxException, IOException {
        String name = "factorial";
        testWellTyped(name);
    }

    @Test
    void testMutualRecWellTyped() throws URISyntaxException, IOException {
        String name = "mutual-rec";
        testWellTyped(name);
    }

    @Test
    void testScope1WellTyped() throws URISyntaxException, IOException {
        String name = "scope1";
        testWellTyped(name);
    }

    @Test
    void testScope2WellTyped() throws URISyntaxException, IOException {
        String name = "scope2";
        testWellTyped(name);
    }

    @Test
    void testScope3WellTyped() throws URISyntaxException, IOException {
        String name = "scope3";
        testWellTyped(name);
    }

}
