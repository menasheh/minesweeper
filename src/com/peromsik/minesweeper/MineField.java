package com.peromsik.minesweeper;

import java.util.Random;

public class MineField {
    private static final int EMPTY = 0;
    private static final int MINE = 1;

    private static final int CATEGORY_LABELS = 0;
    private static final int FLAG = 1;
    private static final int QUESTION_MARK = 2;
    private static final int ALREADY_CLICKED = 3;

    private final int CATEGORY_NEIGHBOR_COUNT = 1;

    private final int CATEGORY_ENDGAME = 2;
    private final int MINE_CLICKED = 0;
    // Mine is 1, above
    private final int FLAG_MISTAKE = 2;

    private int width;
    private int height;
    private int mines;
    private int moves;
    private int openedTiles;
    private boolean gameOver;

    // First 2 dimensional array represents the actual board. Second represents what the user sees.
    private int[][][] board;

    private MineFrame gameFrame;
    private JMineFieldTile[][] visualBoard;

    MineField(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.mines = mines;
        gameOver = false;
        board = new int[width][height][2];

        Random rand = new Random();

        for (int i = 0; i < mines; i++) {
            int x;
            int y;
            do {
                x = rand.nextInt(width);
                y = rand.nextInt(height);
            } while (board[x][y][0] != EMPTY);
            board[x][y][0] = MINE;

        }

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setGameFrame(MineFrame frame){
        gameFrame = frame;
    }

    public void setVisualBoard(JMineFieldTile[][] board) {
        visualBoard = board;
    }

    public void click(int x, int y) {
        if (!(board[x][y][1] == ALREADY_CLICKED || gameOver)) {
            if(!Thread.currentThread().getStackTrace()[2].getMethodName().equals("click")){
                ++moves; // Count moves only if they come from the JButton and are not recursive calls.
            }
            if (board[x][y][0] == MINE) {
                visualBoard[x][y].updateGraphic(CATEGORY_ENDGAME, MINE_CLICKED);
                board[x][y][1] = ALREADY_CLICKED;
                endgame();
            } else {
                int neighbors = getNeighbors(x, y);
                visualBoard[x][y].updateGraphic(CATEGORY_NEIGHBOR_COUNT, neighbors);
                board[x][y][1] = ALREADY_CLICKED;
                openedTiles++;
                if (neighbors == 0) {
                    for (int i = x - 1; i <= x + 1; i++) {
                        for (int j = y - 1; j <= y + 1; j++) {
                            if (i >= 0 && i < width && j >= 0 && j < height) {
                                if (!(x == i && y == j)) {
                                    click(i, j);
                                }
                            }
                        }
                    }
                }
                if (width * height - openedTiles == mines) {
                    wingame();
                }
            }
        }
    }

    public void clickRight(int x, int y) {
        if (!(board[x][y][1] == ALREADY_CLICKED || gameOver)) {
            switch (board[x][y][1]) {
                case EMPTY:
                    board[x][y][1] = FLAG;
                    break;
                case FLAG:
                    board[x][y][1] = QUESTION_MARK;
                    break;
                case QUESTION_MARK:
                    board[x][y][1] = EMPTY;
                    break;
            }
            visualBoard[x][y].updateGraphic(CATEGORY_LABELS, board[x][y][1]);
        }
    }

    public int getNeighbors(int x, int y) {
        int neighbors = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < width && j >= 0 && j < height) {
                    neighbors += board[i][j][0];
                }
            }
        }
        return neighbors;
    }

    private void endgame() {
        gameOver = true;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!(board[i][j][1] == ALREADY_CLICKED)) {
                    if (board[i][j][0] == MINE) {
                        if (!(board[i][j][1] == FLAG)) {
                            visualBoard[i][j].updateGraphic(CATEGORY_ENDGAME, MINE);
                        }
                    } else {
                        if (board[i][j][1] == FLAG) {
                            visualBoard[i][j].updateGraphic(CATEGORY_ENDGAME, FLAG_MISTAKE);
                        }
                    }
                }
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gameFrame.showLoseDialog(width, height, mines, moves);
    }

    private void wingame() {
        gameOver = true;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!(board[i][j][1] == ALREADY_CLICKED)) {
                    if (board[i][j][0] == MINE) {
                        if (!(board[i][j][1] == FLAG)) {
                            visualBoard[i][j].updateGraphic(CATEGORY_LABELS, FLAG);
                        }
                    }
                }
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gameFrame.showWinDialog(width, height, mines, moves);
    }
}
