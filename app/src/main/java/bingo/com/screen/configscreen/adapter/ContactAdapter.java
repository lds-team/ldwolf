package bingo.com.screen.configscreen.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bingo.com.R;
import bingo.com.base.baseadapter.BaseCursorRecyclerAdapter;
import bingo.com.base.baseview.BaseRecyclerViewHolder;
import bingo.com.callbacks.UpdateSync;
import bingo.com.dialog.InfoContactDialog;
import bingo.com.helperdb.ContactColumnMaps;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.db.ContactDatabase;
import butterknife.Bind;

public class ContactAdapter extends BaseCursorRecyclerAdapter<ContactAdapter.ViewHolder> {

    private Context context;

    public ContactAdapter(Context context, Cursor cursor) {
        super(context, cursor);

        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.setData(cursor.getString(ContactColumnMaps.NAME), cursor.getString(ContactColumnMaps.TYPE).equals(ContactDatabase.TYPE_CHU_NHAN_SO));

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.simple_list_item_1, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    class ViewHolder extends BaseRecyclerViewHolder {

        @Bind(android.R.id.text1)
        TextView name;

        private InfoContactDialog dialog;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDialogData(getCursor(), ContactColumnMaps.NAME, ContactColumnMaps.PHONE, ContactColumnMaps.TYPE, ContactDBHelper.getColumnsHeso());
                }
            });
        }

        public void setDialogData(Cursor cursor, int nameIndex, int numberIndex, int typeIndex, String... columns) {
            cursor.moveToPosition(getLayoutPosition());
            dialog = new InfoContactDialog(context, false, cursor, nameIndex, numberIndex, typeIndex, columns);
            dialog.show();
        }

        public void setData(String name, boolean isChuNhanso) {
            this.name.setText(name);

            if (isChuNhanso)
                this.name.setTextColor(Color.BLUE);
            else
                this.name.setTextColor(Color.BLACK);
        }
    }
}
