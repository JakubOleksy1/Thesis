package com.tetris.tetris;

class Coordinate {
    int y, x;

    Coordinate(int r, int c) {
        this.y = r;
        this.x = c;
    }

    static Coordinate add(Coordinate A, Coordinate B) {
        return new Coordinate(A.y + B.y, A.x + B.x);
    }

    static Coordinate sub(Coordinate A, Coordinate B) {
        return new Coordinate(A.y - B.y, A.x - B.x);
    }

    static Coordinate rotateAntiClock(Coordinate X) {

        return new Coordinate(-X.x, X.y);
    }

    static boolean isEqual(Coordinate A, Coordinate B) {

        return A.y == B.y && A.x == B.x;
    }

}
