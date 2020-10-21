package bingo.com.base.baseadapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;


public abstract class BaseCursorTreeAdapter<GVH extends BaseCursorTreeAdapter.ViewHolder, CVH extends BaseCursorTreeAdapter.ViewHolder> extends CursorTreeAdapter {

    protected BaseCursorTreeAdapter(Cursor cursor, Context context) {
        super(cursor, context);
    }

    protected BaseCursorTreeAdapter(Cursor cursor, Context context, boolean autoRequery) {
        super(cursor, context, autoRequery);
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
        View group = onGroupViewCreate(context, cursor, b, viewGroup);
        GVH holder = onCreateGroupViewHolder(group);
        group.setTag(holder);

        return group;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean b) {
        GVH holder = (GVH) view.getTag();

        bindGroupData(view, context, b, holder, cursor);
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
        View child = onChildViewCreate(context, cursor, b, viewGroup);
        CVH holder = onCreateChildViewHolder(child);
        child.setTag(holder);

        return child;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean b) {
        CVH holder = (CVH) view.getTag();

        bindChildData(view, context, b, holder, cursor);
    }

    protected abstract View onGroupViewCreate(Context context, Cursor cursor, boolean b, ViewGroup viewGroup);

    protected abstract View onChildViewCreate(Context context, Cursor cursor, boolean b, ViewGroup viewGroup);

    protected abstract GVH onCreateGroupViewHolder(View view);

    protected abstract CVH onCreateChildViewHolder(View view);

    protected abstract void bindGroupData(View view, Context context, boolean b, GVH holder, Cursor cursor);

    protected abstract void bindChildData(View view, Context context, boolean b, CVH holder, Cursor cursor);

    public static class ViewHolder {
        public ViewHolder(View view) {
            if (view == null) {
                throw new IllegalArgumentException("view may not be null");
            }
        }
    }
}
