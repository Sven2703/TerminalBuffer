import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TerminalBufferTest {
    TerminalBuffer t1 = new TerminalBuffer(6, 3, 9);
    TerminalBuffer t2 = new TerminalBuffer(9, 6, 3);
    TerminalBuffer t3 = new TerminalBuffer(3, 9, 6);
    TerminalBuffer t4 = new TerminalBuffer(5, 7, 2);
    TerminalBuffer t5 = new TerminalBuffer(7, 2, 5);
    TerminalBuffer[] ts = new TerminalBuffer[] {t1, t2, t3, t4, t5};
    TerminalBuffer t6 = new TerminalBuffer(1, 10, 5);
    TerminalBuffer t7 = new TerminalBuffer(10, 1, 5);
    TerminalBuffer t8 = new TerminalBuffer(5, 5, 0);

    @Test
    public void testInsertLine() {
        t1.clearScreenAndScrollback();
        t1.setCursorPositionRow(0);
        t1.setCursorPositionColumn(0);
        String testLine = "abcdef" +
                "ghijkl" +
                "mnopqr" +
                "stuvwx" +
                "yz1234" +
                "567890";
        t1.insertText(testLine);
        assertEquals("yz1234", t1.getLineScreen(0));
        // because the last line was full, there was a new line added although there is no text on it
        assertEquals("", t1.getLineScreen(2));
        assertEquals("stuvwx", t1.getLineScrollback(8));
        assertEquals("yz1234567890", t1.getEntireScreen());
        assertEquals(testLine, t1.getEntireScreenAndScrollback());
        assertEquals('9', t1.getCharScreenAt(1, 4));
        assertEquals('w', t1.getCharScrollbackAt(8, 4));

        testLine = "abcdefghi" +
                "jklmnopqr" +
                "stuvwxyz12" +
                "34567890";
        t2.clearScreenAndScrollback();
        t2.setCursorPositionRow(0);
        t2.setCursorPositionColumn(0);
        t2.insertText(testLine);
        assertEquals(testLine, t2.getEntireScreen());
        assertEquals(testLine, t2. getEntireScreenAndScrollback());
        assertEquals(4, t2.getCursorPositionRow());
        assertEquals(0, t2.getCursorPositionColumn());
        assertEquals("234567890", t2.getLineScreen(3));

        t1.setBold(true);
        t1.setItalic(true);
        t1.setUnderline(true);
        t1.setBackgroundColor(5);
        t1.setForegroundColor(1);
        // color should be between 0 and 16, it should not change now
        t1.setBackgroundColor(18);
        t1.setCursorPositionRow(1);

        // does not override the previous text
        t1.insertText("edcba");
        assertEquals("abcdefghijklmnopqrstuvwxyz1234edcba567890", t1.getEntireScreenAndScrollback());
        assertTrue(t1.isBoldScreenAt(1, 2));
        assertTrue(t1.isUnderlineScreenAt(1, 4));
        assertTrue(t1.isItalicScreenAt(1, 0));
        assertEquals(5, t1.getBackgroundColorScreenAt(1, 3));
        assertEquals(1, t1.getForegroundColorScreenAt(1, 2));
        t1.clearScreen();
        assertEquals("", t1.getEntireScreen());

        t6.clearScreenAndScrollback();
        t6.setCursorPositionRow(0);
        t6.setCursorPositionColumn(0);
        t6.insertText("abcdefghi1234567890");
        assertEquals("fghi1234567890", t6.getEntireScreenAndScrollback());
        assertEquals("234567890", t6. getEntireScreen());
        // write goes to the next line because the last one was full
        assertEquals("", t6. getLineScreen(9));
        t6.clearScreenAndScrollback();
        assertEquals("", t6.getEntireScreenAndScrollback());

        t7.clearScreenAndScrollback();
        t7.setCursorPositionRow(0);
        t7.setCursorPositionColumn(0);
        t7.insertText("abcdefghi1234567890");
        assertEquals("abcdefghi1234567890", t7.getEntireScreenAndScrollback());
        assertEquals("234567890", t7. getEntireScreen());
        assertEquals("abcdefghi1", t7.getLineScrollback(4));

        t8.clearScreenAndScrollback();
        t8.setCursorPositionRow(0);
        t8.setCursorPositionColumn(0);
        String testLine1 = "abcde";
        String testLine2 = "fghij";
        String testLine3 = "klmno";
        String testLine4 = "pqrst";
        String testLine5 = "uvwxy";
        String testLine6 = "z1234";
        t8.insertText(testLine1 + testLine2 + testLine3 + testLine4 + testLine5 + testLine6);
        assertEquals(testLine3 + testLine4 + testLine5 + testLine6, t8.getEntireScreenAndScrollback());
    }

    @Test
    public void testWriteLine() {
        t1.clearScreenAndScrollback();
        t1.setCursorPositionRow(0);
        t1.setCursorPositionColumn(0);
        String testLine = "abcdef" +
                "ghijkl" +
                "mnopqr" +
                "stuvwx" +
                "yz1234" +
                "567890";
        t1.writeText(testLine);
        assertEquals("yz1234", t1.getLineScreen(0));
        // because the last line was full, there was a new line added although there is no text on it
        assertEquals("", t1.getLineScreen(2));
        assertEquals("stuvwx", t1.getLineScrollback(8));
        assertEquals("yz1234567890", t1.getEntireScreen());
        assertEquals(testLine, t1.getEntireScreenAndScrollback());

        testLine = "abcdefghi" +
                "jklmnopqr" +
                "stuvwxyz12" +
                "34567890";
        t2.clearScreenAndScrollback();
        t2.setCursorPositionRow(0);
        t2.setCursorPositionColumn(0);
        t2.writeText(testLine);
        assertEquals(testLine, t2.getEntireScreen());
        assertEquals(testLine, t2. getEntireScreenAndScrollback());
        assertEquals(4, t2.getCursorPositionRow());
        assertEquals(0, t2.getCursorPositionColumn());
        assertEquals("234567890", t2.getLineScreen(3));

        t1.setBold(true);
        t1.setItalic(true);
        t1.setUnderline(true);
        t1.setBackgroundColor(5);
        t1.setForegroundColor(1);
        // color should be between 0 and 16, it should not change now
        t1.setForegroundColor(18);
        t1.setCursorPositionRow(1);

        // overrides the old text
        t1.writeText("edcba");
        assertEquals("abcdefghijklmnopqrstuvwxyz1234edcba0", t1.getEntireScreenAndScrollback());
        assertTrue(t1.isBoldScreenAt(1, 2));
        assertTrue(t1.isUnderlineScreenAt(1, 4));
        assertTrue(t1.isItalicScreenAt(1, 0));
        assertEquals(5, t1.getBackgroundColorScreenAt(1, 3));
        assertEquals(1, t1.getForegroundColorScreenAt(1, 2));

        t6.clearScreenAndScrollback();
        t6.setCursorPositionRow(0);
        t6.setCursorPositionColumn(0);
        t6.writeText("abcdefghi1234567890");
        assertEquals("fghi1234567890", t6.getEntireScreenAndScrollback());
        assertEquals("234567890", t6. getEntireScreen());
        // write goes to the next line because the last one was full
        assertEquals("", t6. getLineScreen(9));

        t7.clearScreenAndScrollback();
        t7.setCursorPositionRow(0);
        t7.setCursorPositionColumn(0);
        t7.writeText("abcdefghi1234567890");
        assertEquals("abcdefghi1234567890", t7.getEntireScreenAndScrollback());
        assertEquals("234567890", t7. getEntireScreen());
        assertEquals("abcdefghi1", t7.getLineScrollback(4));

        t8.clearScreenAndScrollback();
        t8.setCursorPositionRow(0);
        t8.setCursorPositionColumn(0);
        String testLine1 = "abcde";
        String testLine2 = "fghij";
        String testLine3 = "klmno";
        String testLine4 = "pqrst";
        String testLine5 = "uvwxy";
        String testLine6 = "z1234";
        t8.writeText(testLine1 + testLine2 + testLine3 + testLine4 + testLine5 + testLine6);
        assertEquals(testLine3 + testLine4 + testLine5 + testLine6, t8.getEntireScreenAndScrollback());
    }



    @Test
    public void testCursor() {
        for(TerminalBuffer t : ts) {
            t.setCursorPositionColumn(0);
            t.setCursorPositionRow(0);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            // the cursor should stay in the top left corner:
            t.moveCursorLeft(5);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorUp(5);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorDown(-5);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorRight(0);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorLeft(0);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorUp(0);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorDown(0);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorRight(0);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.setCursorPositionRow(-5);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.setCursorPositionColumn(-5);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorUp(5);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            // move all cursors to their bottom left corner and back
            t.moveCursorUp(-20);
            assertEquals(t.getHeight() - 1, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorUp(15);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            // move all cursors to their bottom left corner and back
            t.moveCursorDown(30);
            assertEquals(t.getHeight() - 1, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorDown(-11);
            assertEquals(0, t.getCursorPositionRow());
            assertEquals(0, t.getCursorPositionColumn());

            t.moveCursorRight(5);
        }
        // t1, t2 and t5 should have moved five columns to the right
        assertEquals(0, t1.getCursorPositionRow());
        assertEquals(5, t1.getCursorPositionColumn());
        assertEquals(0, t2.getCursorPositionRow());
        assertEquals(5, t2.getCursorPositionColumn());
        assertEquals(0, t5.getCursorPositionRow());
        assertEquals(5, t5.getCursorPositionColumn());
        // t3 should have moved 2 to the right to the left of the next row and then 2 more to the right
        assertEquals(1, t3.getCursorPositionRow());
        assertEquals(2, t3.getCursorPositionColumn());
        // t4 should have moved 4 to the right and then to the beginning of the next row
        assertEquals(1, t4.getCursorPositionRow());
        assertEquals(0, t4.getCursorPositionColumn());

        for(TerminalBuffer t : ts) {
            // all cursors should be in their bottom right corner
            t.moveCursorRight(48);
        }
        isCursorBottomRight();
        for (TerminalBuffer t : ts) {
            t.moveCursorLeft(-5);
            isCursorBottomRight();
            t.moveCursorRight(10);
            isCursorBottomRight();
            t.moveCursorDown(7);
            isCursorBottomRight();
            t.moveCursorUp(-20);
            isCursorBottomRight();

            // set the cursor outside bounds to moves it to bound of the screen
            t.setCursorPositionColumn(-10);
            assertEquals(0, t.getCursorPositionColumn());

            t.setCursorPositionRow(-1);
            assertEquals(0, t.getCursorPositionRow());

            t.setCursorPositionColumn(10);
            t.setCursorPositionRow(15);
            isCursorBottomRight();
        }

    }

    private void isCursorBottomRight() {
        assertEquals(2, t1.getCursorPositionRow());
        assertEquals(5, t1.getCursorPositionColumn());
        assertEquals(5, t2.getCursorPositionRow());
        assertEquals(8, t2.getCursorPositionColumn());
        assertEquals(8, t3.getCursorPositionRow());
        assertEquals(2, t3.getCursorPositionColumn());
        assertEquals(6, t4.getCursorPositionRow());
        assertEquals(4, t4.getCursorPositionColumn());
        assertEquals(1, t5.getCursorPositionRow());
        assertEquals(6, t5.getCursorPositionColumn());
    }
}
