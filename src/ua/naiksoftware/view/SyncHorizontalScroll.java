package ua.naiksoftware.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import java.util.ArrayList;

public class SyncHorizontalScroll extends HorizontalScrollView {

    private static final String tag = "MyLayout";
	private ArrayList<View> syncViews = new ArrayList<View>();

    public SyncHorizontalScroll(Context context) {
        super(context);
        //init(context);
    }

    public SyncHorizontalScroll(Context context, AttributeSet attr) {
        super(context, attr);
        //init(context);
    }

    private void init(Context context) {
    }

	public <T extends View> void addSync(T view) {
		syncViews.add(view);
	}

	@Override
    public void computeScroll() {
		super.computeScroll();
		int len = syncViews.size();
		for (int j = 0;j < len;j++) {
			syncViews.get(j).scrollTo(getScrollX(), 0);
		}
        //Log.d(tag, "computeScroll");
    }
}
