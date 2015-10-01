package pl.mareklangiewicz.myviews;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

/**
 * Created by Marek Langiewicz on 07.05.15.
 * some dummy tests..
 */
public class MyPieTest {
//    TODO SOMEDAY: some real tests

    int x;

    class Bla {
        public int a;
        public int b;
        public void print() {
            System.out.println(String.format("x = %d; a = %d; b = %d;", x, a, b));
        }
    }

    @Test
    public void testTest() throws Exception {
        assertThat(5, equalTo(5));
        assertThat(666, is(allOf(anything("whatewa"), anything(), either(is(667)).or(is(666)))));
        x = 2;
        Bla bla = this.new Bla();
        bla.a = 3;
        bla.b = 4;
        bla.print();
    }

    @Test
    public void testOnDraw() throws Exception {

    }

    @Test
    public void testInvalidate() throws Exception {

    }

    @Test
    public void testGetColor() throws Exception {

    }

    @Test
    public void testGetMinimum() throws Exception {

    }

    @Test
    public void testGetMaximum() throws Exception {

    }

    @Test
    public void testGetFrom() throws Exception {

    }

    @Test
    public void testGetTo() throws Exception {

    }

    @Test
    public void testGetArea() throws Exception {

    }

    @Test
    public void testSetColor() throws Exception {

    }

    @Test
    public void testSetMinimum() throws Exception {

    }

    @Test
    public void testSetMaximum() throws Exception {

    }

    @Test
    public void testSetFrom() throws Exception {

    }

    @Test
    public void testSetTo() throws Exception {

    }

    @Test
    public void testScale0d() throws Exception {

    }

    @Test
    public void testScale1d() throws Exception {

    }

    @Test
    public void testScale2d() throws Exception {

    }

    @Test
    public void testScale2d1() throws Exception {

    }
}