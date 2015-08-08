package ua.naiksoftware.graphpaths.module;

import java.util.ArrayList;
import java.util.Iterator;

import ua.naiksoftware.graphpaths.CanvasView;

/**
 * Created by Naik on 26.04.15.
 */
public class JDD extends Module {

    private static final int INF = -1;

    public JDD(CanvasView canvasView) {
        super(canvasView);
    }

    private class CellJ extends Cell {

        public static final int ASCII_START = 97;
        private ArrayList<String> summa, new_summa;

        public CellJ() {
            summa = new ArrayList<String>();
            new_summa = new ArrayList<String>();
        }

        void mult(String str, CellJ other) {
            for (String str2 : other.summa) {
                new_summa.add(str + str2);
            }
        }

        void commit(String row) {
            Iterator<String> it = new_summa.iterator();
            while (it.hasNext()) {
                if (it.next().contains(row)) {
                    it.remove();
                    it = new_summa.iterator();
                }
            }
            summa.clear();
            summa.addAll(new_summa);
            new_summa.clear();
        }

        boolean empty() {
            return summa.size() == 0;
        }

        int get_most_short(Cell[][] m, char start) {
            CanvasView canvasView = getCanvasView();
            canvasView.addString("Select diagonal " + this);
            String path = null;
            int len = Integer.MAX_VALUE;
            int tmp;
            char prev;
            StringBuilder buff = new StringBuilder();
            for (String str : summa) {
                tmp = 0;
                prev = start;
                buff.delete(0, buff.length());
                for (char ch : str.toCharArray()) {
                    tmp += m[prev - ASCII_START][ch - ASCII_START].getInt();
                    prev = ch;
                    buff.append(ch);
                }
                tmp += m[prev - ASCII_START][start - ASCII_START].getInt(); // cycled path
                canvasView.addString("    Path " + start + str + start + " with length " + tmp);
                if (tmp < len) {
                    len = tmp;
                    path = (start + buff.toString() + start);
                }
            }
            if (len != Integer.MAX_VALUE) {
                canvasView.addString("Shortest path: " + path + " Length: " + len);
            } else {
                canvasView.addString("Gamilton contours not found in this graph, try other");
            }
            return len;
        }

        void add(String str) {
            summa.add(str);
        }

        @Override
        public String toString() {
            int size = summa.size();
            if (size < 1) return Cell.INFINITY;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sb.append(summa.get(i));
                if (i < size - 1) sb.append('+');
            }
            return sb.toString();
        }

    }

    @Override
    public void resolve(Cell[][] m, int from, int to) {
        CanvasView canvasView = getCanvasView();
        canvasView.reset();
        canvasView.addString("Yau, Danielson, Dhawan algo");
        // Зчитуємо матрицю суміжності ваг графа
        int size = m.length;
        canvasView.addMatrix(m, "Source");

        // Створення матриці перетвореннь і результуючої
        String[][] v = new String[size][size];
        CellJ[][] p = new CellJ[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                v[i][j] = new String();
                p[i][j] = new CellJ();
                char ch = m[i][j].getInt() != Integer.MAX_VALUE ? (char) (CellJ.ASCII_START + j) : '0';
                v[i][j] += ch;
            }
        }

        canvasView.addMatrix(v, "Matrix V");

        // Обрахування першої результуючої матриці p2 (бо p1=m)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    if (!v[i][k].equals("0") && m[k][j].getInt() != Integer.MAX_VALUE && i != j) {
                        p[i][j].add(v[i][k]);
                    }
                }
            }
        }

        canvasView.addString("P1 = Source");
        canvasView.addMatrix(p, "P2");

        // Обчислення інших матриць P
        for (int i = 3; i <= size; i++) {
            if (i == size) {            // Якщо остання матриця P
                mult(v, p, size, true); // то рахуємо діагоналі
            } else mult(v, p, size, false);
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    char ch = (char) (CellJ.ASCII_START + j);
                    p[j][k].commit(String.valueOf(ch));
                }
            }
            canvasView.addMatrix(p, "P" + i);
        }

        int len = p[0][0].get_most_short(m, 'a');
        // Длинна найдена, печать в методе get_most_short
    }

    /*
     * Перемножити матрицi
     */
    void mult(String[][] v, CellJ[][] p, int size, boolean calc_diag) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    if (!v[i][k].equals("0") && !p[k][j].empty()
                            && (i != j || calc_diag)) {
                        p[i][j].mult(v[i][k], p[k][j]);
                    }
                }
            }
        }
    }

}
