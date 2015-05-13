import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.TerminalAppearance;

import java.io.IOException;

public class BufferView {
    private FileBuffer fBuffer;
    //largura e altura da janela
    private int width, height;
    // linha início da janela
    private int startRow;
    private Screen screen;
    private ScreenWriter sWriter;
    private boolean delete = false;

    private BufferView() {

        startScreen();
        sWriter = new ScreenWriter(screen);
        readInput();
    }

    private void readInput() {
        while (true) {
            screen.clear();
            Key k = screen.readInput();
            if (k != null) {
                switch (k.getKind()) {
                    case Escape:
                        screen.stopScreen();
                        return;
                    case ArrowUp:
                        break;
                    case ArrowDown:
                        break;
                    case ArrowLeft:
                        break;
                    case ArrowRight:
                        break;
                    case Tab:
                        break;
                    case End:
                        break;
                    case Home:
                        break;
                    case PageUp:
                        break;
                    case Backspace:
                        break;
                    case Delete:
                        break;
                    case NormalKey:
                        if (!delete && !k.isCtrlPressed()) {
                            fBuffer.insertChar(k.getCharacter());
                        }
                        if (k.isCtrlPressed()) {
                            if (k.getCharacter() == 's') {

                                try {
                                    fBuffer.save();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (k.getCharacter() == 'x') {
                                return;
                            }
                            if (k.getCharacter() == 'c') {
                                fBuffer.copy();
                            }
                        }
                        delete = false;
                        break;
                    case Enter:
                        fBuffer.insertChar('\n');
                        break;
                    default:
                        break;
                }
            }
            screen.refresh();
        }
    }

    private void startScreen() {
        Terminal term = TerminalFacade.createSwingTerminal(TerminalAppearance.DEFAULT_APPEARANCE, width, height);
        screen = new Screen(term);
        screen.startScreen();
    }
}