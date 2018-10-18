package lacina.geodata.logic;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProfileTest {
    private Profile profile;
    @Before
    public void setUp() throws Exception {
        profile = new Profile();
    }

    @Test
    public void setPoints() {
        profile.setPoints(Double.valueOf(10));
        assertEquals(profile.getPoints(), Double.valueOf(10));
    }


    @Test
    public void textPoints() {
        assertEquals(profile.textPoints(),String.format("%0" + 8 + "d",  Math.round(profile.getPoints())));
    }

    /*
      O objetivo desse caso de teste que é investigar se o cálculo da funcionalidade de adicionar pontos a um perfil em um aplicativo android está funcionando corretamente.

     */
    @Test
    public void addPoints() {
        profile.setPoints(10.0);
        profile.addPoints(3.0);
        assertEquals(Double.valueOf(12.5),profile.getPoints());
    }

    @Test
    public void getIsEnabled() {
        assertTrue(profile.getIsEnabled());
    }

    @Test
    public void setIsEnabled() {
        profile.setIsEnabled(false);
        assertTrue(!profile.getIsEnabled());
    }

    @Test
    public void setId() {
        profile.setId("10");
        assertEquals(profile.getId(),"10");
    }

    @Test
    public void getId() {
        assertEquals(profile.getId(),null);
    }

    @Test
    public void getInstance() {
        assertEquals(Profile.getInstance(), new Profile());
    }

    @Test
    public void instanceReady() {
        assertFalse(Profile.instanceReady());
    }

    @Test
    public void getCurrentTickets() {
        assertEquals(profile.getCurrentTickets(),new ArrayList<Long>());
    }


    @Test
    public void addCurrentTicket() {
    }
}