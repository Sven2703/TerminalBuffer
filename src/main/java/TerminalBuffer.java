import java.util.LinkedList;
import java.util.List;

public class TerminalBuffer {
    private int width, height, maxScrollback;
    private int foregroundColor, backgroundColor;
    private int cursorPositionColumn, cursorPositionRow;
    private Cell[][] screen, scrollback;
    private boolean bold, italic, underline;

    public static void main(String[] args) {

    }

    public TerminalBuffer(int width, int height, int maxScrollback) {
        this.width = width;
        this.height = height;
        this.maxScrollback = maxScrollback;
        if(height < 1 || width < 1 || maxScrollback < 0)
            throw new RuntimeException("Buffer has no screen or negative scrollback");
        screen = new Cell[height][width];
        scrollback = new Cell[maxScrollback][width];
    }

    public void clearScreen() {
        screen = new Cell[height][width];
    }

    public void clearScreenAndScrollback() {
        screen = new Cell[height][width];
        scrollback = new Cell[maxScrollback][width];
    }

    /**
     * Writes a text overriding the text that was previously there, moves the cursor.
     * @param text The text to write
     */
    public void writeText(String text) {
        char[] charArray = text.toCharArray();
        for(char c : charArray) {
            writeCharacter(cursorPositionRow, cursorPositionColumn, c);
            if(cursorPositionRow == height - 1 && cursorPositionColumn == width - 1) {
                insertEmptyLine();
                if(height == 1)
                    setCursorPositionColumn(0);
                else
                    moveCursorRight(1);
            } else
                moveCursorRight(1);
        }
    }

    /**
     * Inserts text and moves not empty cells further back, moves the cursor
     * @param text The text to insert
     */
    public void insertText(String text) {
        List<Cell> append = new LinkedList<>();
        if (!lineIsEmpty(cursorPositionRow, cursorPositionColumn)) {
            for(int j = cursorPositionColumn; j < width; j++) {
                append.add(screen[cursorPositionRow][j]);
            }
        }
        char[] charArray = text.toCharArray();
        for(char c : charArray) {
            writeCharacter(cursorPositionRow, cursorPositionColumn, c);
            if(cursorPositionRow == height - 1 && cursorPositionColumn == width - 1) {
                insertEmptyLine();
                if(height == 1)
                    setCursorPositionColumn(0);
                else
                    moveCursorRight(1);
            } else
                moveCursorRight(1);
        }
        while(!append.isEmpty()) {
            if(screen[cursorPositionRow][cursorPositionColumn] != null)
                append.add(screen[cursorPositionRow][cursorPositionColumn]);
            screen[cursorPositionRow][cursorPositionColumn] = append.get(0);
            append.remove(0);
            if(cursorPositionRow == height - 1 && cursorPositionColumn == width - 1) {
                insertEmptyLine();
                if(height == 1)
                    setCursorPositionColumn(0);
                else
                    moveCursorRight(1);
            } else
                moveCursorRight(1);
        }
    }

    private void insertEmptyLineAt(int row) {
        if (!lineIsEmpty(row, 0)) {
            int moveDownTo = row - 1;
            for(int i = row; i < height; i++) {
                if(lineIsEmpty(i, 0)) {
                    moveDownTo = i;
                    break;
                }
            }
            if(moveDownTo < row) {
                moveCursorUp(1);
                for(int i = 1; i < maxScrollback; i++) {
                    System.arraycopy(scrollback[i], 0, scrollback[i - 1], 0, width);
                }
                System.arraycopy(scrollback[maxScrollback - 1], 0, screen[0], 0, width);
                for(int i = 1; i < row; i++) {
                    System.arraycopy(screen[i], 0, screen[i - 1], 0, width);
                }
                for(int j = 0; j < width; j++) {
                    writeEmptyCell(height - 1, j);
                }
            } else {
                for(int i = moveDownTo; i > row; i--) {
                    System.arraycopy(screen[i - 1], 0, screen[i], 0, width);
                }
            }
            for(int j = 0; j < width; j++) {
                writeEmptyCell(row, j);
            }
        }
    }

    /**
     * Inserts a new empty line at the bottom and moves every
     * existing line towards the top and the first line to the scrollback.
     */
    public void insertEmptyLine() {
        if(maxScrollback > 0) {
            for (int i = 1; i < maxScrollback; i++) {
                System.arraycopy(scrollback[i], 0, scrollback[i - 1], 0, width);
            }
            System.arraycopy(screen[0], 0, scrollback[maxScrollback - 1], 0, width);
        }
        if(height > 1) {
            moveCursorUp(1);
            for (int i = 1; i < height; i++) {
                System.arraycopy(screen[i], 0, screen[i - 1], 0, width);
            }
        }
        for(int j = 0; j < width; j++) {
            writeEmptyCell(height - 1, j);
        }
    }

    /**
     * Empties the rest of the line.
     */
    public void fillLine() {
        for(int i = cursorPositionColumn; i < width; i++) {
            writeEmptyCell(cursorPositionRow, i);
        }
    }

    /**
     * Fills the rest of the line with a character.
     * @param character to fill the line with
     */
    public void fillLine(char character) {
        for(int i = cursorPositionColumn; i < width; i++) {
            writeCharacter(cursorPositionRow, i, character);
        }
    }

    private boolean lineIsEmpty(int row, int start) {
        if(row >= 0 && row < height && start < width && start >= 0) {
            for(int j = start; j < width; j++) {
                if(screen[row][j] != null)
                    return false;
            }
        }
        return true;
    }

    private void writeEmptyCell(int row, int column) {
        screen[row][column] = null;
    }

    private void writeCharacter(int row, int column, char character) {
        screen[row][column] = new Cell(foregroundColor, backgroundColor, character, bold, italic, underline);
    }

    public void moveCursorUp(int NCells) {
        if(NCells >= 0) {
            if (NCells <= cursorPositionRow)
                cursorPositionRow -= NCells;
            else
                cursorPositionRow = 0;
        } else
            moveCursorDown(-NCells);
    }

    public void moveCursorDown(int NCells) {
        if(NCells >= 0) {
            if (NCells + cursorPositionRow < height)
                cursorPositionRow += NCells;
            else
                cursorPositionRow = height - 1;
        } else
            moveCursorUp(-NCells);
    }

    /**
     * Moves the Cursor to the right if it reaches the end of the screen it starts on the left in the next line
     * @param NCells number of cells to move the cursor
     */
    public void moveCursorRight(int NCells) {
        if(NCells >= 0) {
            if (NCells + cursorPositionColumn < width)
                cursorPositionColumn += NCells;
            else if (cursorPositionRow + 1 < height) {
                moveCursorDown(1);
                NCells -= (width - cursorPositionColumn);
                cursorPositionColumn = 0;
                moveCursorRight(NCells);
            } else
                //Cursor is in the bottom right
                cursorPositionColumn = width - 1;
        } else
            moveCursorLeft(-NCells);
    }

    /**
     * Moves the Cursor to the left if it reaches the end of the screen it starts on the right in the next line above
     * @param NCells number of cells to move the cursor
     */
    public void moveCursorLeft(int NCells) {
        if(NCells >= 0) {
            if (NCells <= cursorPositionColumn)
                cursorPositionColumn -= NCells;
            else if (cursorPositionRow > 0) {
                moveCursorUp(1);
                NCells -= cursorPositionColumn;
                cursorPositionColumn = width - 1;
                moveCursorLeft(NCells);
            } else
                //Cursor is in the top left
                cursorPositionColumn = 0;
        } else
            moveCursorRight(-NCells);
    }

    public String getEntireScreenAndScrollback() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < maxScrollback; i++) {
            res.append(getLineScrollback(i));
        }
        res.append(getEntireScreen());
        return res.toString();
    }

    public String getEntireScreen() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < height; i++) {
            res.append(getLineScreen(i));
        }
        return res.toString();
    }

    public String getLineScreen(int row) {
        StringBuilder res = new StringBuilder();
        if (row >= 0 && row < height) {
            for (int j = 0; j < width; j++) {
                char character = getCharScreenAt(row, j);
                if(character != 0)
                    res.append(character);
            }
        }
        return res.toString();
    }

    public String getLineScrollback(int row) {
        StringBuilder res = new StringBuilder();
        if (row >= 0 && row < maxScrollback) {
            for (int j = 0; j < width; j++) {
                char character = getCharScrollbackAt(row, j);
                if(character != 0)
                    res.append(character);
            }
        }
        return res.toString();
    }

    public char getCharScreenAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < height) {
            if (this.screen[row][column] != null)
                return this.screen[row][column].getCharacter();
        }
        return 0;
    }

    public char getCharScrollbackAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < maxScrollback) {
            if (scrollback[row][column] != null)
                return scrollback[row][column].getCharacter();
        }
        return 0;
    }

    public void setForegroundColor(int foregroundColor) {
        if(foregroundColor >= 0 && foregroundColor <= 16)
            this.foregroundColor = foregroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        if(backgroundColor >= 0 && backgroundColor <= 16)
            this.backgroundColor = backgroundColor;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public void setCursorPositionColumn(int cursorPositionColumn) {
        if(cursorPositionColumn < 0)
            this.cursorPositionColumn = 0;
        else if(cursorPositionColumn >= width)
            this.cursorPositionColumn = width - 1;
        else
            this.cursorPositionColumn = cursorPositionColumn;
    }

    public void setCursorPositionRow(int cursorPositionRow) {
        if(cursorPositionRow < 0)
            this.cursorPositionRow = 0;
        else if(cursorPositionRow >= height)
            this.cursorPositionRow = height - 1;
        else
            this.cursorPositionRow = cursorPositionRow;
    }

    public int getCursorPositionColumn() {
        return cursorPositionColumn;
    }

    public int getCursorPositionRow() {
        return cursorPositionRow;
    }

    public int getBackgroundColorScreenAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < height) {
            if (this.screen[row][column] != null)
                return screen[row][column].getBackgroundColor();
        }
        return 0;
    }

    public int getForegroundColorScreenAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < height) {
            if (this.screen[row][column] != null)
                return screen[row][column].getForegroundColor();
        }
        return 0;
    }

    public boolean isItalicScreenAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < height) {
            if (this.screen[row][column] != null)
                return screen[row][column].isItalic();
        }
        return false;
    }

    public boolean isBoldScreenAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < height) {
            if (this.screen[row][column] != null)
                return screen[row][column].isBold();
        }
        return false;
    }

    public boolean isUnderlineScreenAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < height) {
            if (this.screen[row][column] != null)
                return screen[row][column].isUnderline();
        }
        return false;
    }

    public int getBackgroundColorScrollbackAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < maxScrollback) {
            if (scrollback[row][column] != null)
                return scrollback[row][column].getBackgroundColor();
        }
        return 0;
    }

    public int getForegroundColorScrollbackAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < maxScrollback) {
            if (scrollback[row][column] != null)
                return scrollback[row][column].getForegroundColor();
        }
        return 0;
    }

    public boolean isItalicScrollbackAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < maxScrollback) {
            if (scrollback[row][column] != null)
                return scrollback[row][column].isItalic();
        }
        return false;
    }

    public boolean isBoldScrollbackAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < maxScrollback) {
            if (scrollback[row][column] != null)
                return scrollback[row][column].isBold();
        }
        return false;
    }

    public boolean isUnderlineScrollbackAt(int row, int column) {
        if (column < width && column >= 0 && row >= 0 && row < maxScrollback) {
            if (scrollback[row][column] != null)
                return scrollback[row][column].isUnderline();
        }
        return false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxScrollback() {
        return maxScrollback;
    }

    private class Cell {
        private int foregroundColor, backgroundColor;
        private char character;
        private boolean bold, italic, underline;

        private Cell(int foregroundColor, int backgroundColor, char character, boolean bold, boolean italic, boolean underline) {
            this.foregroundColor = foregroundColor;
            this.backgroundColor = backgroundColor;
            this.character = character;
            this.bold = bold;
            this.italic = italic;
            this.underline = underline;
        }

        private char getCharacter() {
            return character;
        }

        public int getForegroundColor() {
            return foregroundColor;
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public boolean isBold() {
            return bold;
        }

        public boolean isItalic() {
            return italic;
        }

        public boolean isUnderline() {
            return underline;
        }
    }
}
