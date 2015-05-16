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
    private ArrayList<String> logicText;


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
                    case NormalKey:
                        if (!delete && !k.isCtrlPressed()) {
                            fBuffer.insertChar(k.getCharacter());
                        }
                        if (k.isCtrlPressed()) {
                            if (k.getCharacter() == 's') {
                                fBuffer.save();
                            }
                            if (k.isAltPressed()) {
                                if (k.getCharacter() == 'w') {
                                    Path path = Paths.get("C:\\Users\\Vitor Afonso\\workspace\\Trabalho_2_Parte_2\\src\\new_file2.txt");
                                    fBuffer.saveAs(path);
                                }
                            }
                            if (k.getCharacter() == 'x') {
                                fBuffer.cut();
                            }
                            if (k.getCharacter() == 'c') {
                                fBuffer.copy();
                            }
                            if (k.getCharacter() == 'v' && fBuffer.clipBoard != null) {
                                fBuffer.paste();
                            }
//                            if (k.getCharacter() == ' ') {
//                                fBuffer.setMark(screen.getCursorPosition().getColumn(), screen.getCursorPosition().getRow());
//                            }
                        }
                        delete = false;
                        break;
                    case Enter:
                        fBuffer.insertChar('\n');
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

    private void phisicToLogic() {
        logicText = new ArrayList<>();
        for (StringBuilder sb : fBuffer.getAllLines()) {
            if (sb.length() > 95) {
                splitString(sb.toString(), 95);
            } else {
                logicText.add(sb.toString());
            }
        }
    }

    private void splitString(String string, int length) {
        String chunck = "";
        Matcher matcher = Pattern.compile(".{0," + length + "}").matcher(string);
        while (matcher.find()) {
            chunck = string.substring(matcher.start(), matcher.end());
            logicText.add(chunck);
        }

    }

    private void startScreen() {
        fBuffer = new FileBuffer();
        Terminal term = TerminalFacade.createSwingTerminal(TerminalAppearance.DEFAULT_APPEARANCE, width, height);
        screen = new Screen(term);
        sWriter = new ScreenWriter(screen);
        screen.startScreen();
        Path path = Paths.get("C:\\Users\\Vitor Afonso\\workspace\\Trabalho_2_Parte_2\\src\\new_file.txt");
        fBuffer.open(path);

    }

    private void drawText() {

        for (int i = 0; i < logicText.size(); i++) {
            sWriter.drawString(0, i, logicText.get(i));
            updateCursor();
        }
    }

    private void updateCursor() {
        int column = fBuffer.getCursor().getColumn();
        int line = fBuffer.getCursor().getLine();
        System.out.println("col: " + column + "line: " + line);
        screen.setCursorPosition(column, line);
    }
}