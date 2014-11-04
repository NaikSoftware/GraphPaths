package ua.naiksoftware.graphpaths.module;

import ua.naiksoftware.graphpaths.CanvasView;

/**
 *
 * @author Naik
 */
public class Shimbell extends Module {

    public Shimbell(CanvasView canvasView) {
        super(canvasView);
    }

    public static class CellS extends Cell {

        int value, newVal;
    }

    public void resolve(Cell[][] m, int from, int to) {
        CanvasView canvasView = getCanvasView();
        canvasView.reset();
        canvasView.addString("Shimbell method");
        canvasView.addString("(from all points to all in graph)");
        canvasView.addMatrix(m, "A");
        int size = m.length, i, j;
        CellS[][] cells = new CellS[size][size];
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                cells[i][j] = new CellS();
                cells[i][j].value = m[i][j].value;
            }
        }
        boolean run = true;
        int min, k, sum, extent = 1;
        while (run) {
            run = false;
            for (i = 0; i < size; i++) {
                for (j = 0; j < size; j++) {
                    min = Integer.MAX_VALUE;
                    for (k = 0; k < size; k++) {
                        if (cells[i][k].value == Integer.MAX_VALUE || cells[k][j].value == Integer.MAX_VALUE) {
                            sum = Integer.MAX_VALUE;
                        } else {
                            sum = cells[i][k].value + cells[k][j].value;
                        }
                        min = min > sum ? sum : min;
                    }
                    if (min < cells[i][j].value) {
                        run = true;
                        cells[i][j].newVal = min;
                    } else {
                        cells[i][j].newVal = cells[i][j].value;
                    }
                }
            }
            for (i = 0; i < size; i++) {
                for (j = 0; j < size; j++) {
                    cells[i][j].value = cells[i][j].newVal;
                }
            }
            if (run) {
                extent++;
                for (i = 0; i < size; i++) {
                    for (j = 0; j < size; j++) {
                        m[i][j].value = cells[i][j].value;
                    }
                }
                canvasView.addMatrix(m, "A^" + extent);
            } else {
                canvasView.addString("Result = A^" + extent + " (A^" + extent + " = A^" + (extent + 1) + ")");
            }
        }
    }
}
