package bingo.com.screen.modelscreen.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bingo.com.R;
import bingo.com.base.baseadapter.BaseTreeAdapter;

public class ModelAdapter extends BaseTreeAdapter<ModelAdapter.GroupViewHolder, ModelAdapter.ChildViewHolder> {

    private List<String> mHeader;

    private HashMap<Integer, String> mData;

    public ModelAdapter(Activity context) {
        super(context);
        mHeader = new ArrayList<>();
        mData = new HashMap<>();
    }

    public void setData(List<String> header, HashMap<Integer, String> data) {
        this.mHeader = header;
        this.mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getViewWithType(int viewType) {
        if (viewType == TYPE_GROUP)
        {
            return View.inflate(getActivityContext(), R.layout.simple_expand_item_header, null);
        }
        else
        {
            return View.inflate(getActivityContext(), R.layout.simple_expand_item_content, null);
        }
    }

    @Override
    public BaseTreeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, View convertView, int viewType) {
        if (viewType == TYPE_GROUP)
        {
            return new GroupViewHolder(convertView);
        }
        else
        {
            return new ChildViewHolder(convertView);
        }
    }

    @Override
    public View onBindGroupView(BaseTreeAdapter.ViewHolder holder, int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ((GroupViewHolder) holder).name.setText(mHeader.get(groupPosition));

        return convertView;
    }

    @Override
    public View onBindChildView(BaseTreeAdapter.ViewHolder holder, int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ((ChildViewHolder) holder).name.setText(mData.get(groupPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getChildTypeCount() {
        return super.getChildTypeCount();
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return super.getChildType(groupPosition, childPosition);
    }

    @Override
    public int getGroupTypeCount() {
        return super.getGroupTypeCount();
    }

    @Override
    public int getGroupType(int groupPosition) {
        return super.getGroupType(groupPosition);
    }

    public static class GroupViewHolder extends BaseTreeAdapter.ViewHolder {

        public TextView name;

        public GroupViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(android.R.id.text1);
        }
    }

    public static class ChildViewHolder extends BaseTreeAdapter.ViewHolder {

        public TextView name;

        public ChildViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}
