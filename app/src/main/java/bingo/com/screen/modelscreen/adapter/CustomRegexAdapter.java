package bingo.com.screen.modelscreen.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import bingo.com.R;

public class CustomRegexAdapter extends BaseAdapter {

    Map<String, String> customs;

    Context context;

    public CustomRegexAdapter(Context context, Map<String, String> customs) {
        this.customs = customs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return customs.size();
    }

    @Override
    public String getItem(int position) {
        ArrayList<String> keysSet = new ArrayList<>(customs.keySet());
        return keysSet.get(position);
    }

    public String getContent(int position) {
        String key = getItem(position);
        return customs.get(key);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            convertView = View.inflate(context, R.layout.simple_list_item_2, null);
        }

        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);

        String key = getItem(position);
        text1.setText(key);
        text2.setText(customs.get(key));

        return convertView;
    }
}
