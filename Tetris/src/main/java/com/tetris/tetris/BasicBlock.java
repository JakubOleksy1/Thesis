package com.tetris.tetris;

enum BasicBlockState {
    ON_EMPTY,
    ON_TETRAMINO
}

class BasicBlock {
    int colour;
    int tetraId;
    Coordinate coordinate;
    BasicBlockState state;

    BasicBlock(int row, int column) {
        this.colour = -1;
        this.tetraId = -1;
        this.coordinate = new Coordinate(row, column);
        this.state = BasicBlockState.ON_EMPTY;
    }

    BasicBlock(int colour, int tetraId, Coordinate coordinate, BasicBlockState state) {
        this.colour = colour;
        this.tetraId = tetraId;
        this.coordinate = coordinate;
        this.state = state;

    }

    BasicBlock copy() {

        return new BasicBlock(colour, tetraId, coordinate, state);
    }

    void set(BasicBlock B) {
        this.colour = B.colour;
        this.tetraId = B.tetraId;
        this.coordinate.y = B.coordinate.y;
        this.coordinate.x = B.coordinate.x;
        this.state = B.state;

    }

    void setEmptyBlock(Coordinate coordinate) {
        this.colour = -1;
        this.tetraId = -1;
        this.coordinate.x = coordinate.x;
        this.coordinate.y = coordinate.y;
        this.state = BasicBlockState.ON_EMPTY;

    }
}

