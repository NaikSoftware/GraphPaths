package ua.naiksoftware.graphpaths.module;

import ua.naiksoftware.graphpaths.CanvasView;

/**
 * 
 * @author Naik
 */
public abstract class Module {

    private final CanvasView canvasView;
    
    public Module(CanvasView canvasView) {
        this.canvasView = canvasView;
    }
    
    protected CanvasView getCanvasView() {
        return canvasView;
    }
    
    public abstract void resolve(Cell[][] m, int from, int to);
}
