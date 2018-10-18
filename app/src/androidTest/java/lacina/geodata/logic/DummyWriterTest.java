package lacina.geodata.logic;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class DummyWriterTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void write() throws IOException {
        DummyWriter.write("teste");
    }
}