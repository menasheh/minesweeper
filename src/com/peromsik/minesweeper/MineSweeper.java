package com.peromsik.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MineSweeper {
    public static void main(String[] args) {
        newGame();
    }

    public static void newGame() {
        MineField mineField = new MineField(20, 10, 25);
        MineFrame game = new MineFrame(mineField);
        mineField.setGameFrame(game);
    }

}

class MineFrame extends JFrame {
    private final int TILE_SIZE = 16;

    private JMineFieldTile[][] board;

    MineFrame(MineField field) {
        super("v1.0.0");
        setSize(field.getWidth() * TILE_SIZE, 30 + field.getHeight() * TILE_SIZE);

        setLayout(new GridBagLayout()); // field.getHeight(), field.getWidth()
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.5;
        c.weighty = 0.5;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false); // Prevent disarranging tiles by resizing

        board = new JMineFieldTile[field.getWidth()][field.getHeight()];

        for (int i = 0; i < field.getWidth(); i++) {
            for (int j = 0; j < field.getHeight(); j++) {
                JMineFieldTile b = new JMineFieldTile(field, i, j);
                c.gridx = i;
                c.gridy = j;
                board[i][j] = b;
                add(b, c);
            }
        }

        setLocationRelativeTo(null); // Start at monitor center
        setVisible(true);
        field.setVisualBoard(board);
    }

    public void showWinDialog(int width, int height, int mines, int moves) {
        int dialogResult = JOptionPane.showConfirmDialog(null, "You found all "
                + mines + " mines on a " + width + "x" + height + " board in " + moves + " move" + (moves > 1 ? "s" : "")
                + "!\n" + "Would you like to play again?", "Congratulations!", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            MineSweeper.newGame();
        }
        dispose();
    }

    public void showLoseDialog(int width, int height, int mines, int moves) {
        int dialogResult = JOptionPane.showConfirmDialog(this, "You hit one of "
                + mines + " mines on a " + width + "x" + height + " board in " + moves + " move" + (moves > 1 ? "s" : "")
                + ".\n" + "Would you like to play again?", "Uh Oh!", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            MineSweeper.newGame();
        }
        dispose();
    }

}

class JMineFieldTile extends JButton {

    private static final ImageIcon EMPTY = new ImageIcon("res/mines/blank.gif");
    private static final ImageIcon FLAG = new ImageIcon("res/mines/bombflagged.gif");
    private static final ImageIcon QUESTION = new ImageIcon("res/mines/bombquestion.gif");

    private static final ImageIcon[] OpenIcons = {
            new ImageIcon("res/mines/open0.gif"),
            new ImageIcon("res/mines/open1.gif"),
            new ImageIcon("res/mines/open2.gif"),
            new ImageIcon("res/mines/open3.gif"),
            new ImageIcon("res/mines/open4.gif"),
            new ImageIcon("res/mines/open5.gif"),
            new ImageIcon("res/mines/open6.gif"),
            new ImageIcon("res/mines/open7.gif"),
            new ImageIcon("res/mines/open8.gif")
    };

    private static final ImageIcon MINE = new ImageIcon("res/mines/bombrevealed.gif");
    private static final ImageIcon MINE_CLICKED = new ImageIcon("res/mines/bombdeath.gif");
    private static final ImageIcon FLAG_INCORRECT = new ImageIcon("res/mines/bombmisflagged.gif");

    private int gridX;
    private int gridY;

    JMineFieldTile(MineField field, int x, int y) {
        super(null, EMPTY);
        gridX = x;
        gridY = y;
        setSize(new Dimension(16, 16));

        setOpaque(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JMineFieldTile button = (JMineFieldTile) e.getSource();
                if (SwingUtilities.isRightMouseButton(e)) {
                    field.clickRight(gridX, gridY);
                } else {
                    field.click(button.gridX, button.gridY);
                }
            }
        });

    }

    public void updateGraphic(int category, int index) {
        switch (category) {
            case 0: // Player-Placed Labels
                switch (index) {
                    case 0:
                    default:
                        setIcon(EMPTY);
                        break;
                    case 1:
                        setIcon(FLAG);
                        break;
                    case 2:
                        setIcon(QUESTION);
                        break;
                }
                break;
            case 1: // Clicked Open Spaces
                setIcon(OpenIcons[index]);
                break;
            case 2: // Post-Game stuff
                switch (index) {
                    case 0:
                        setIcon(MINE_CLICKED);
                        break;
                    case 1:
                        setIcon(MINE);
                        break;
                    case 2:
                        setIcon(FLAG_INCORRECT);
                        break;
                }
                break;
        }
    }

}