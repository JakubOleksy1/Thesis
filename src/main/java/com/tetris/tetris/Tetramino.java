package com.tetris.tetris;

import java.util.Random;

enum TetraminoType {
    SQUARE_SHAPED,
    T_SHAPED,
    L_SHAPED,
    LINE_SHAPED,
    Z_SHAPED,
    INV_L_SHAPED,
    INV_Z_SHAPED;

    private static final TetraminoType[] VALUES = values();
    private static final int SIZE = VALUES.length;
    private static final Random RANDOM = new Random();

    public static TetraminoType getRandomTetramino() {
        return VALUES[RANDOM.nextInt(SIZE)];
    }
}

class Tetramino {
    BasicBlock[] blocks;
    TetraminoType type;

    Tetramino(TetraminoType type, int tetraId) {
        Coordinate[] coordinates;

        switch (type) {
            case SQUARE_SHAPED:
                coordinates = new Coordinate[]{
                        new Coordinate(0, 7),
                        new Coordinate(1, 7),
                        new Coordinate(1, 8),
                        new Coordinate(0, 8)
                };
                blocks = this.blocksGenerator(tetraId, 1, coordinates);
                break;
            case INV_L_SHAPED:
                coordinates = new Coordinate[]{
                        new Coordinate(0, 7),
                        new Coordinate(0, 8),
                        new Coordinate(1, 7),
                        new Coordinate(2, 7)
                };
                blocks = this.blocksGenerator(tetraId, 2, coordinates);
                break;
            case L_SHAPED:
                coordinates = new Coordinate[]{
                        new Coordinate(0, 8),
                        new Coordinate(0, 7),
                        new Coordinate(1, 8),
                        new Coordinate(2, 8)
                };
                blocks = this.blocksGenerator(tetraId, 3, coordinates);
                break;
            case T_SHAPED:
                coordinates = new Coordinate[]{
                        new Coordinate(1, 7),
                        new Coordinate(0, 7),
                        new Coordinate(1, 8),
                        new Coordinate(2, 7)
                };
                blocks = this.blocksGenerator(tetraId, 4, coordinates);
                break;
            case Z_SHAPED:
                coordinates = new Coordinate[]{
                        new Coordinate(1, 8),
                        new Coordinate(1, 7),
                        new Coordinate(0, 7),
                        new Coordinate(2, 8)
                };
                blocks = this.blocksGenerator(tetraId, 5, coordinates);
                break;
            case INV_Z_SHAPED:
                coordinates = new Coordinate[]{
                        new Coordinate(1, 8),
                        new Coordinate(0, 8),
                        new Coordinate(1, 7),
                        new Coordinate(2, 7)
                };
                blocks = this.blocksGenerator(tetraId, 6, coordinates);
                break;
            case LINE_SHAPED:
                coordinates = new Coordinate[]{
                        new Coordinate(0, 7),
                        new Coordinate(1, 7),
                        new Coordinate(2, 7),
                        new Coordinate(3, 7)
                };
                blocks = this.blocksGenerator(tetraId, 7, coordinates);
                break;
        }
    }

    private Tetramino(BasicBlock[] blocks) {

        this.blocks = blocks;
    }

    private BasicBlock[] blocksGenerator(int tetraId, int colour, Coordinate[] coordinates) {
        BasicBlock[] blocks = new BasicBlock[coordinates.length];
        for (int itr = 0; itr < coordinates.length; itr++) {
            blocks[itr] = new BasicBlock(colour, tetraId, coordinates[itr], BasicBlockState.ON_TETRAMINO);
        }
        return blocks;
    }

    Tetramino copy(int tetraId) {
        BasicBlock[] newBlocks = new BasicBlock[this.blocks.length];
        for (int itr = 0; itr < this.blocks.length; itr++) {
            newBlocks[itr] = this.blocks[itr].copy();
            newBlocks[itr].tetraId = tetraId;
        }
        return new Tetramino(newBlocks);
    }

    void moveDown() {

        for (BasicBlock block : blocks) {
            block.coordinate.y++;
        }
    }

    void moveLeft() {

        for (BasicBlock block : blocks) {
            block.coordinate.x--;
        }
    }

    void moveRight() {

        for (BasicBlock block : blocks) {
            block.coordinate.x++;
        }
    }

    void performClockWiseRotation() {

        BasicBlock referenceBlock = blocks[0];

        for (BasicBlock block : blocks) {
            Coordinate baseCoordinate = Coordinate.sub(block.coordinate, referenceBlock.coordinate);
            block.coordinate = Coordinate.add(Coordinate.rotateAntiClock(baseCoordinate), referenceBlock.coordinate);
        }
    }

}
