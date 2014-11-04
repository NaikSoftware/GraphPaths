package ua.naiksoftware.graphpaths.module;

import java.util.ArrayList;
import ua.naiksoftware.graphpaths.CanvasView;
import static ua.naiksoftware.graphpaths.MainActivity.INFINITY;

/**
 * 
 * @author Naik
 */
public class Deikstra extends Module {

    public Deikstra(CanvasView canvasView) {
        super(canvasView);
    }

    private class CellD extends Cell{

        CellD parent;
        int passability;
        int closed;
        String name;
    }
    
    @Override
    public void resolve(Cell[][] m, int from, int to) {
        CanvasView canvasView = getCanvasView();
        canvasView.reset();
        canvasView.addString("Deikstra method:");
        canvasView.addMatrix(m, "A");
        findDeikstra(m, from, to);
    }
    
    private void findDeikstra(Cell[][] m, int start, int end) {
        int size = m.length;
        CellD[] row = new CellD[size];
        start--;
        end--;
        int i, j;
        for (i = 0; i < size; i++) {
            row[i] = new CellD();
        }
        // First part (init)
        row[start].passability = 0;
        row[start].closed = 1;
        for (i = 0; i < size; i++) {
            row[i].name = ("X" + (i + 1));
            if (i != start) {
                row[i].parent = row[start];
                row[i].passability = m[start][i].value;
                row[i].closed = 0;
            }
        }

        // Second part (cycle)
        int min, idx = -1;
        for (i = 0; i < (size - 2); i++) {
            min = Integer.MAX_VALUE;
            idx = -1;
            for (j = 0; j < size; j++) {
                if (row[j].closed == 1) {
                    continue;
                }
                if (min > row[j].passability) {
                    min = row[j].passability;
                    idx = j;
                }
            }
            if (idx == -1) {//нет пути

            } else {
                row[idx].closed = 1;
                for (j = 0; j < size; j++) {
                    if (row[j].closed == 1) {
                        continue;
                    }
                    if (row[j].passability > (m[idx][j].value == Integer.MAX_VALUE ? Integer.MAX_VALUE : m[idx][j].value + row[idx].passability)) {
                        row[j].passability = row[idx].passability + m[idx][j].value;
                        row[j].parent = row[idx];
                    }
                }
            }
        }
        getCanvasView().addString("Path from " + (start + 1) + " to " + (end + 1) + " equal " + (idx == -1 ? INFINITY : row[end].passability));
        if (idx != -1) {
            ArrayList<String> path = new ArrayList<String>();
            CellD cell = row[end];
            while (cell != null) {
                path.add(cell.name);
                cell = cell.parent;
            }
            StringBuilder sb = new StringBuilder("Path:");
            for (i = (path.size() - 1); i >= 0; i--) {
                sb.append(" ").append(path.get(i));
            }
            getCanvasView().addString(sb.toString());
        }
    }
}
