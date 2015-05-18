import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.TerminalAppearance;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BufferView {
    //largura e altura da janela
    private final int width = 100, height = 30;
    private FileBuffer fBuffer;
    // linha início da janela
    private int startRow;
    private Screen screen;
    private ScreenWriter sWriter;
    private boolean delete = false;
    private ArrayList<String> visualText;


    private BufferView() {
        startScreen();
        while (true) {
            screen.clear();
            Key k = screen.readInput();
            if (k != null) {
                switch (k.getKind()) {
                    case Escape:
                        screen.stopScreen();
                        return;
                    case ArrowUp:
                        fBuffer.movePrevLine();
                        break;
                    case ArrowDown:
                        fBuffer.moveNextLine();
                        break;
                    case ArrowLeft:
                        fBuffer.movePrev();
                        break;
                    case ArrowRight:
                        fBuffer.moveNext();
                        break;
                    case Tab:
                        fBuffer.insertChar('\t');
                        break;
                    case End:
                        fBuffer.moveEnd();
                        break;
                    case Home:
                        fBuffer.moveHome();
                        break;
                    case PageUp:
                        break;
                    case Backspace:
                        fBuffer.deleteChar();
                        break;
                    case Delete:
                        fBuffer.deleteCharInFront();
                        delete = true;
                        break;
                    case Enter:
                        fBuffer.insertChar('\n');
                        break;
                    case NormalKey:
                        if (!delete && !k.isCtrlPressed()) {
                            fBuffer.insertChar(k.getCharacter());
                        }
                        if (k.isCtrlPressed()) {
                            if (k.getCharacter() == 'm') {
                                System.out.println("Marked");
                                setMark();
                            }
                            //save
                            if (k.getCharacter() == 's') {
                                fBuffer.save();
                            }
                            if (k.isAltPressed()) {
                                //saveAS
                                if (k.getCharacter() == 'w') {
                                    Path path = Paths.get("C:\\Users\\Vitor Afonso\\workspace\\Trabalho_2_Parte_2\\src\\new_file2.txt");
                                    fBuffer.saveAs(path);
                                }
                            }

                            //cut
                            if (k.getCharacter() == 'x') {
                                fBuffer.cut();
                            }
                            //copy
                            if (k.getCharacter() == 'c' && fBuffer.isMarked()) {
                                fBuffer.copy();
                            }
                            //undo
                            if (k.getCharacter() == 'z') {
                                fBuffer.undo();
                            }
                            //paste
                            if (k.getCharacter() == 'v' && fBuffer.clipBoard != null) {
                                fBuffer.paste();
                            }


                        }
                        delete = false;
                        break;


                }
            }
            phisicToLogic();
            drawText();
            updateCursor();
            screen.refresh();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        BufferView bf = new BufferView();
    }

    private void setMark() {
        int column = screen.getCursorPosition().getColumn();
        int line = screen.getCursorPosition().getRow();
        System.out.println(column + " " + line);
        fBuffer.setMark(column, line);

    }

    private void phisicToLogic() {
        visualText.clear();
        int columns = screen.getTerminalSize().getColumns();
        for (StringBuilder sb : fBuffer.getAllLines()) {
            if (sb.length() >= columns) {
                splitString(sb.toString(), columns);
            } else visualText.add(sb.toString());
        }
    }

    private void splitString(String string, int length) {
        String chunck = "";
        Matcher matcher = Pattern.compile(".{0," + length + "}").matcher(string);
        while (matcher.find()) {
            chunck = string.substring(matcher.start(), matcher.end());
            if (!chunck.equals(""))
                visualText.add(chunck);
        }

    }

    private void startScreen() {
        fBuffer = new FileBuffer();
        Terminal term = TerminalFacade.createSwingTerminal(TerminalAppearance.DEFAULT_APPEARANCE, width, height);
        screen = new Screen(term);
        sWriter = new ScreenWriter(screen);
        visualText = new ArrayList<>();
        screen.startScreen();
        Path path = Paths.get("C:\\Users\\Vitor Afonso\\workspace\\Trabalho_2_Parte_2\\src\\new_file.txt");
        fBuffer.open(path);

    }

    private void drawText() {
        if (fBuffer.isMarked()) {
            sWriter.setForegroundColor(Terminal.Color.BLACK);
            sWriter.setBackgroundColor(Terminal.Color.WHITE);
        }
        for (int i = 0; i < visualText.size(); i++) {
            sWriter.drawString(0, i, visualText.get(i));
            updateCursor();
        }
    }

    private void updateCursor() {
        int column = fBuffer.getCursor().getColumn();
        int line = fBuffer.getCursor().getLine();
//        System.out.println("col: " + column + "\nline: " + line);
        screen.setCursorPosition(column, line);
    }
}