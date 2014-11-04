package ua.naiksoftware.graphpaths.module;

import ua.naiksoftware.graphpaths.CanvasView;

/**
 *
 * @author Naik
 */
public class SalesmanProblem extends Module {

    private static final int INT_MAX = Integer.MAX_VALUE;

    public SalesmanProblem(CanvasView canvasView) {
        super(canvasView);
    }

    private static class Node {

        Node childExclude, childInclude, parent;
        boolean isExclude;
        int[][] matrix;
        int[] idxRows, idxCols;
        int from, to, limit, size;
    };

    @Override
    public void resolve(Cell[][] m, int from, int to) {
        getCanvasView().reset();
        getCanvasView().addString("Salesman problem (branch and bound method)");
        /* Створимо корненевий вузол */
        Node rootNode = new Node();
        int size = m.length;
        rootNode.size = size;
        int[][] matrix = new int[size][size];
        int[] idxRows = new int[size];
        int[] idxCols = new int[size];
        int i, j;
        for (i = 0; i < size; i++) {
            idxRows[i] = idxCols[i] = i;
            for (j = 0; j < size; j++) {
                matrix[i][j] = m[i][j].value;
            }
        }
        rootNode.matrix = matrix;
        rootNode.idxRows = idxRows;
        rootNode.idxCols = idxCols;
        rootNode.limit = simplification(matrix);

        int valid = 0;
        Node closeNode = rootNode, node;
        while (closeNode.size > 2) {
            mountChildNodes(closeNode);
            closeNode = getBetterNode(rootNode);
            valid = 0;
            for (i = 0; i < closeNode.size; i++) {
                for (j = 0; j < closeNode.size; j++) {
                    if (closeNode.matrix[i][j] != INT_MAX) {
                        valid = 1;
                    }
                }
            }
            if (valid == 0) {
                break;
            }
        }
        int start = -1, end = -1;
        if (valid == 1) {
            int[][] arr = closeNode.matrix;
            if (arr[0][0] == 0) {
                start = closeNode.idxRows[0];
                end = closeNode.idxCols[0];
            } else {
                start = closeNode.idxRows[0];
                end = closeNode.idxCols[1];
            }
            if (arr[1][0] == 0) {
                rootNode.from = closeNode.idxRows[1];
                rootNode.to = closeNode.idxCols[0];
            } else {
                rootNode.from = closeNode.idxRows[1];
                rootNode.to = closeNode.idxCols[1];
            }

            /* Print Gamilton Contour */
            StringBuilder sb = new StringBuilder("Контур: ");
            sb.append(start + 1).append(" ").append(end + 1);
            do {
                node = closeNode;
                while (end != node.from || node.isExclude) {
                    node = node.parent;
                }
                end = node.to;
                sb.append(" ").append(end + 1);
            } while (start != end);
            getCanvasView().addString(sb.toString());
            getCanvasView().addString("Довжина: " + closeNode.limit);
        } else {
            getCanvasView().addString("Solution not found!");
        }
    }

    /*
     * @node - вузол, в якому потрібно створити дочірні
     */
    private void mountChildNodes(Node node) {
        /* Find high-priced zero */
        int col = 0, row = 0, price = 0, i, j, k, oldSize, newSize, minInRow, minInCol;
        int zeroRow = 0, zeroCol = 0;
        oldSize = node.size;
        int arr[][] = node.matrix;
        for (i = 0; i < oldSize; i++) {
            for (j = 0; j < oldSize; j++) {
                if (arr[i][j] != 0) {
                    continue;
                }
                minInRow = minInCol = INT_MAX;
                for (k = 0; k < oldSize; k++) {
                    if (k != j) {
                        minInRow = Math.min(minInRow, arr[i][k]);
                    }
                    if (k != i) {
                        minInCol = Math.min(minInCol, arr[k][j]);
                    }
                }
                if (minInRow == INT_MAX || minInCol == INT_MAX) {
                    minInRow = 0; // захист від переповнення
                    minInCol = INT_MAX;
                }
                if (price <= (minInRow + minInCol)) {
                    price = minInRow + minInCol;
                    zeroRow = i;
                    zeroCol = j;
                    row = node.idxRows[i];
                    col = node.idxCols[j];
                }
            }
        }
        /* Create 'include' child */
        Node childIncl = new Node();
        newSize = (oldSize - 1);
        childIncl.size = newSize;
        childIncl.from = row;
        childIncl.to = col;
        childIncl.parent = node;
        /* Create new matrix without one row and column */
        int idxRows[] = new int[newSize];
        int idxCols[] = new int[newSize];
        int m[][] = new int[newSize][newSize];
        int offsetRow = 0, offsetCol;
        for (i = 0; i < oldSize; i++) {
            if (node.idxRows[i] == row) {
                offsetRow = 1;
                continue;
            }
            idxRows[i - offsetRow] = node.idxRows[i];
            offsetCol = 0;
            for (j = 0; j < oldSize; j++) {
                if (node.idxCols[j] == col) {
                    offsetCol = 1;
                    continue;
                }
                idxCols[j - offsetCol] = node.idxCols[j];
                m[i - offsetRow][j - offsetCol] = node.matrix[i][j];
            }
        }
        childIncl.idxRows = idxRows;
        childIncl.idxCols = idxCols;
        childIncl.matrix = m;
        /* Exclude short cycle */
        int start = childIncl.from, end = childIncl.to;
        int[] real = new int[2];
        Node nCurr = childIncl;
        while (nCurr != null) {
            if (start == nCurr.to) {
                start = nCurr.from;
            } else if (end == nCurr.from) {
                end = nCurr.to;
            }
            cellExists(childIncl, end, start, real);
            if (real[0] != -1 && real[1] != -1) {
                m[real[0]][real[1]] = INT_MAX;
                break;
            }
            nCurr = nCurr.parent;
        }
        /* Simplificate and set limit */
        k = simplification(m); // захист від переповнення
        if (k == INT_MAX || node.limit == INT_MAX) {
            childIncl.limit = INT_MAX;
        } else {
            childIncl.limit = node.limit + k;
        }
        node.childInclude = childIncl;
        /* Create 'exclude' child (just copy parent matrix and modify) */
        Node childExcl = new Node();
        childExcl.size = node.size;
        childExcl.from = row;
        childExcl.to = col;
        childExcl.parent = node;
        childExcl.isExclude = true;
        childExcl.idxRows = node.idxRows;
        childExcl.idxCols = node.idxCols;
        childExcl.matrix = node.matrix;
        childExcl.matrix[zeroRow][zeroCol] = INT_MAX;
        simplification(node.matrix);
        if (price == INT_MAX || node.limit == INT_MAX) {
            childExcl.limit = INT_MAX;
        } else {
            childExcl.limit = node.limit + price;
        }
        node.childExclude = childExcl;
    }

    /* Вертає суму констант приведення */
    private int simplification(int[][] arr) {
        int size = arr.length;
        int min, i, j, downLimit = 0;
        for (int k = 0; k < 2; k++) {
            for (i = 0; i < size; i++) {
                min = INT_MAX;
                for (j = 0; j < size; j++) {
                    if (k == 0) {
                        min = Math.min(min, arr[i][j]); // по рядках
                    } else {
                        min = Math.min(min, arr[j][i]); // по стовпчиках
                    }
                }
                if (downLimit == INT_MAX || min == INT_MAX) {
                    downLimit = INT_MAX;
                } else {
                    downLimit += min;
                }
                for (j = 0; j < size; j++) {
                    if (k == 0 && arr[i][j] != INT_MAX) {
                        arr[i][j] -= min;
                    } else if (k == 1 && arr[j][i] != INT_MAX) {
                        arr[j][i] -= min;
                    }
                }
            }
        }
        return downLimit;
    }

    private void cellExists(Node node, int row, int col, int[] real) {
        int size = node.size;
        real[0] = real[1] = -1;
        for (int i = 0; i < size; i++) {
            if (node.idxRows[i] == row) {
                real[0] = i;
            }
            if (node.idxCols[i] == col) {
                real[1] = i;
            }
        }
    }

    private Node getBetterNode(Node node) {
        if (node.childExclude == null) { // якщо листок
            return node;
        } else {                         // якщо вузол дерева
            Node node1 = getBetterNode(node.childExclude);
            Node node2 = getBetterNode(node.childInclude);
            if (node1.limit > node2.limit) {
                return node2;
            } else {
                return node1;
            }
        }
    }
}
