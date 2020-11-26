package com.example.along_the_road;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.along_the_road.managebudgetActivity.TABLE_NAME;

public class MyCursorAdapter extends CursorAdapter {

    public final static String TAG = "MyCursorAdapter";
    private int _id;

    public MyCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    //리스트뷰에 표시될 뷰 반환
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from( context );
        View v = inflater.inflate( R.layout.list_item, parent,false );
        return v;

    }

    //뷰의 속성 지정
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        TextView item_context = (TextView) view.findViewById( R.id.item_context );
        TextView item_price = (TextView) view.findViewById( R.id.item_price );
        //ImageButton item_modify = (ImageButton) view.findViewById(R.id.budget_list_modify);
        final LinearLayout list_area = (LinearLayout)view.findViewById(R.id.list_area);

        //getColumnindex(name) : name에 해당하는 필드의 인덱스 번호를 반환한다.
//        _id = cursor.getInt(cursor.getColumnIndex(managebudgetActivity.KEY_ID)); // : 해당 커서가 위치한 인덱스 위치의 값을 반환한다.
        String contexts = cursor.getString( cursor.getColumnIndex( managebudgetActivity.KEY_CONTEXT ) );
        String price = cursor.getString( cursor.getColumnIndex( managebudgetActivity.KEY_PRICE ) );

        Log.d(TAG, contexts + ", " + price);

        item_context.setText( contexts );
        item_price.setText( price );

    }
}
