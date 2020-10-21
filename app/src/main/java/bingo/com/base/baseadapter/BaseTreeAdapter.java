package bingo.com.base.baseadapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.lang.ref.WeakReference;


public abstract class BaseTreeAdapter<GVH extends BaseTreeAdapter.ViewHolder, CVH extends BaseTreeAdapter.ViewHolder> extends BaseExpandableListAdapter {

    protected final int TYPE_GROUP = 0;

    protected final int TYPE_CHILD = 1;

    private WeakReference<Activity> mActivity;

    public BaseTreeAdapter(Activity context) {
        this.mActivity = new WeakReference<Activity>(context);
    }

    protected Activity getActivityContext() {
        return mActivity.get();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null)
        {
            convertView = getViewWithType(TYPE_GROUP);
            holder = onCreateViewHolder(parent, convertView, TYPE_GROUP);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        return onBindGroupView(holder, groupPosition, isExpanded, convertView, parent);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null)
        {
            convertView = getViewWithType(TYPE_CHILD);
            holder = onCreateViewHolder(parent, convertView, TYPE_CHILD);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        return onBindChildView(holder, groupPosition, childPosition, isLastChild, convertView, parent);
    }

    public abstract View getViewWithType(int viewType);

    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, View convertView, int viewType);

    public abstract View onBindGroupView (ViewHolder holder, int groupPosition, boolean isExpanded, View convertView, ViewGroup parent);

    public abstract View onBindChildView (ViewHolder holder, int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);

    public static abstract class ViewHolder {

        public ViewHolder(View view) {
            if (view == null) {
                throw new IllegalArgumentException("view may not be null");
            }
        }
    }
}
