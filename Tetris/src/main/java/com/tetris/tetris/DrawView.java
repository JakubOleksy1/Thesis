package com.tetris.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

class DrawView extends View {
    int yOffset;
    Paint paint;
    GameState gameState;

    public DrawView(Context context, final GameState gameState) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        yOffset = 200;
        this.gameState = gameState;
    }

    private int getBlockColorCode(int color) {
        switch (color) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.RED;
            case 4:
                return Color.GREEN;
            case 5:
                return Color.CYAN;
            case 6:
                return Color.MAGENTA;
            case 7:
                return Color.WHITE;
            default:
                return Color.TRANSPARENT;
        }

    }

    private void DrawMatrix(BasicBlock[][] matrix, Canvas canvas) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 14; j++) {
                if (matrix[i][j].state == BasicBlockState.ON_EMPTY)
                    continue;

                int color = this.getBlockColorCode(matrix[i][j].colour);
                Paint p = new Paint();
                p.setColor(color);
                canvas.drawRect(52 + j * 50, yOffset + i * 50 + 2, 98 + j * 50, yOffset + (i + 1) * 50 - 2, p);
            }
        }
    }

    private void Clear(BasicBlock[][] matrix, Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 14; j++) {
                canvas.drawRect(52 + j * 50, yOffset + i * 50 + 2, 98 + j * 50, yOffset + (i + 1) * 50 - 2, p);
            }
        }
    }

    private void DrawTetramino(Tetramino tetramino, Canvas canvas) {
        for (BasicBlock block : tetramino.blocks) {
            int color = this.getBlockColorCode(block.colour);
            Paint p = new Paint();
            p.setColor(color);
            canvas.drawRect(52 + block.coordinate.x * 50, yOffset + block.coordinate.y * 50 + 2, 98 + block.coordinate.x * 50, yOffset + (block.coordinate.y + 1) * 50 - 2, p);

        }
    }

    private void Boundary(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5f);
        canvas.drawLine(50, yOffset, 50, yOffset + 1000, paint);
        canvas.drawLine(50, yOffset, 750, yOffset, paint);
        canvas.drawLine(750, yOffset, 750, yOffset + 1000, paint);
        canvas.drawLine(750, yOffset + 1000, 50, yOffset + 1000, paint);
    }

    private void grid(Canvas canvas) {
        paint.setStrokeWidth(2f);
        for (int i = 100; i < 750; i = i + 50) {
            canvas.drawLine(i, yOffset, i, yOffset + 1000, paint);
        }
        for (int j = 50; j < 1000; j = j + 50) {
            canvas.drawLine(50, yOffset + j, 750, yOffset + j, paint);
        }
    }

    private void PrintScore(int score, Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0, 100, 200, 200, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        canvas.drawText(Integer.toString(score), 80, 170, paint);

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5f);
        Boundary(canvas);
        grid(canvas);
        if (gameState.status) {
            Clear(gameState.board, canvas);
            DrawMatrix(gameState.board, canvas);
            DrawTetramino(gameState.falling, canvas);
            PrintScore(gameState.score, canvas);
        } else {
            Paint paint = new Paint();
            DrawMatrix(gameState.board, canvas);
            DrawTetramino(gameState.falling, canvas);
            paint.setColor(Color.WHITE);
            paint.setTextSize(100);
            canvas.drawText(getResources().getString(R.string.game_over), 150, 800, paint);
            PrintScore(gameState.score, canvas);
        }

    }

}
