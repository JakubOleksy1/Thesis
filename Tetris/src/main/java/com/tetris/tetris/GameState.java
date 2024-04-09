package com.tetris.tetris;

import android.util.SparseArray;

class GameState {

    boolean status;
    private boolean give_prize = false;
    int score;
    boolean pause;
    BasicBlock[][] board;
    Tetramino falling;
    boolean difficultMode;
    private int rows;
    private int columns;
    private Integer ctr;
    private SparseArray<Tetramino> tetraminos;

    GameState(int rows, int columns, TetraminoType fallingTetraminoType) {

        this.rows = rows;
        this.columns = columns;
        this.pause = false;
        ctr = 0;
        score = 0;
        this.status = true;
        difficultMode = false;

        board = new BasicBlock[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                board[row][column] = new BasicBlock(row, column);
            }
        }

        tetraminos = new SparseArray<>();

        falling = new Tetramino(fallingTetraminoType, this.ctr);

        tetraminos.put(this.ctr, falling);
    }

    private BasicBlock getCoordinateBlock(Coordinate coordinate) {

        return this.board[coordinate.y][coordinate.x];
    }

    private boolean isConflicting(Coordinate coordinate) {

        if (coordinate.x < 0 || coordinate.x >= this.columns || coordinate.y < 0 || coordinate.y >= this.rows)
            return true;

        return this.getCoordinateBlock(coordinate).state == BasicBlockState.ON_TETRAMINO;

    }

    private boolean canTetraminoDisplace(Tetramino tetramino, Coordinate displacement) {

        for (BasicBlock block : tetramino.blocks) {
            if (block.state == BasicBlockState.ON_TETRAMINO) {
                Coordinate shifted = Coordinate.add(block.coordinate, displacement);
                if (isConflicting(shifted)) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean moveFallingTetraminoDown() {

        if (canTetraminoDisplace(falling, new Coordinate(1, 0))) {
            falling.moveDown();
            return true;
        } else {
            return false;
        }

    }

    boolean moveFallingTetraminoLeft() {

        if (canTetraminoDisplace(falling, new Coordinate(0, -1))) {
            falling.moveLeft();
            return true;
        } else {
            return false;
        }
    }

    boolean moveFallingTetraminoRight() {

        if (canTetraminoDisplace(falling, new Coordinate(0, 1))) {
            falling.moveRight();
            return true;
        } else {
            return false;
        }
    }

    boolean rotateFallingTetraminoAntiClock() {
        if (falling.type == TetraminoType.SQUARE_SHAPED) {
            return true;
        } else {
            for (BasicBlock block : falling.blocks) {
                if (block.state == BasicBlockState.ON_EMPTY)
                    continue;

                BasicBlock referenceBlock = falling.blocks[0];
                Coordinate baseCoordinate = Coordinate.sub(block.coordinate, referenceBlock.coordinate);
                if (isConflicting(Coordinate.add(Coordinate.rotateAntiClock(baseCoordinate), referenceBlock.coordinate))) {
                    return false;
                }
            }
            falling.performClockWiseRotation();
            return true;
        }
    }

    void paintTetramino(Tetramino tetramino) {
        for (BasicBlock block : tetramino.blocks) {
            if (block.state == BasicBlockState.ON_EMPTY)
                continue;
            this.getCoordinateBlock(block.coordinate).set(block);
        }
    }

    void pushNewTetramino(TetraminoType tetraminoType) {
        this.ctr++;

        falling = new Tetramino(tetraminoType, this.ctr);
        this.tetraminos.put(this.ctr, falling);
        for (BasicBlock block : falling.blocks) {
            if (this.getCoordinateBlock(block.coordinate).state == BasicBlockState.ON_TETRAMINO)
                this.status = false;
        }
    }

    void incrementScore() {

        this.score++;
        if(score == 5)
        {
            give_prize = true;

        }
        isGivePoints();
    }

    public boolean isGivePrize() {
        return give_prize;
    }
    public  int isGivePoints() {
        return score;
    }

    void lineRemove() {
        boolean removeLines;
        do {
            removeLines = false;
            for (int row = this.rows - 1; row >= 0; row--) {
                boolean rowIsALine = true;
                for (int column = 0; column < this.columns; column++) {
                    if (this.board[row][column].state != BasicBlockState.ON_TETRAMINO) {
                        rowIsALine = false;
                        break;
                    }
                }
                if (!rowIsALine) {
                    continue;
                }

                for (int column = 0; column < this.columns; column++) {
                    Tetramino tetramino = this.tetraminos.get((this.board[row][column].tetraId));

                    BasicBlock blockToClear = this.board[row][column];
                    blockToClear.setEmptyBlock(blockToClear.coordinate);

                    if (tetramino == null) {
                        continue;
                    }

                    for (BasicBlock block : tetramino.blocks) {
                        if (block.state == BasicBlockState.ON_EMPTY) {
                            continue;
                        }

                        if (block.coordinate.y == row && block.coordinate.x == column) {
                            block.state = BasicBlockState.ON_EMPTY;

                            this.ctr++;
                            Tetramino upperTetramino = tetramino.copy(this.ctr);
                            this.tetraminos.put(this.ctr, upperTetramino);
                            for (BasicBlock upperBlock : upperTetramino.blocks) {
                                if (upperBlock.coordinate.y >= block.coordinate.y) {
                                    upperBlock.state = BasicBlockState.ON_EMPTY;
                                } else {
                                    this.getCoordinateBlock(upperBlock.coordinate).tetraId = upperBlock.tetraId;
                                }
                            }

                            this.ctr++;
                            Tetramino lowerTetramino = tetramino.copy(this.ctr);
                            this.tetraminos.put(this.ctr, lowerTetramino);
                            for (BasicBlock lowerBlock : lowerTetramino.blocks) {
                                if (lowerBlock.coordinate.y <= block.coordinate.y) {
                                    lowerBlock.state = BasicBlockState.ON_EMPTY;
                                } else {
                                    this.getCoordinateBlock(lowerBlock.coordinate).tetraId = lowerBlock.tetraId;
                                }
                            }

                            this.tetraminos.remove(block.tetraId);
                            break;
                        }

                    }
                }
                this.adjustTheMatrix();
                this.incrementScore();
                removeLines = true;
                break;
            }
        } while (removeLines);
    }

    private void adjustTheMatrix() {
        for (int row = this.rows - 1; row >= 0; row--) {
            for (int column = 0; column < this.columns; column++) {
                Tetramino T = (this.tetraminos).get((this.board[row][column].tetraId));

                if (T != null)
                    this.shiftTillBottom(T);
            }
        }
    }

    private void shiftTillBottom(Tetramino tetramino) {
        boolean shiftTillBottom;
        do {
            boolean shouldShiftDown = true;
            shiftTillBottom = false;

            for (BasicBlock block : tetramino.blocks) {
                if (block.state == BasicBlockState.ON_EMPTY)
                    continue;

                Coordinate newCoordinate = Coordinate.add(block.coordinate, new Coordinate(1, 0));

                if (isTetraPresent(newCoordinate, tetramino))
                    continue;

                if (isConflicting(newCoordinate))
                    shouldShiftDown = false;
            }

            if (shouldShiftDown) {
                for (BasicBlock block : tetramino.blocks) {
                    if (block.state == BasicBlockState.ON_EMPTY)
                        continue;

                    this.getCoordinateBlock(block.coordinate).setEmptyBlock(block.coordinate);


                    block.coordinate.y++;
                }

                for (BasicBlock block : tetramino.blocks) {
                    if (block.state == BasicBlockState.ON_EMPTY)
                        continue;

                    this.getCoordinateBlock(block.coordinate).set(block);

                }
                shiftTillBottom = true;
            }
        } while (shiftTillBottom);
    }

    private boolean isTetraPresent(Coordinate coordinate, Tetramino tetramino) {
        for (BasicBlock block : tetramino.blocks) {
            if (block.state == BasicBlockState.ON_EMPTY)
                continue;

            if (Coordinate.isEqual(block.coordinate, coordinate))
                return true;

        }

        return false;
    }
}
