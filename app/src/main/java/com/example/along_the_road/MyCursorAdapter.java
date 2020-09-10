package com.example.along_the_road;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.along_the_road.managebudgetActivity.TABLE_NAME;


public class MyCursorAdapter extends CursorAdapter {
    String TAG = "MyCursorAdapter";
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
    public void bindView(final View view, final Context context, Cursor cursor) {

        TextView item_context = (TextView) view.findViewById( R.id.item_context );
        TextView item_price = (TextView) view.findViewById( R.id.item_price );
        //ImageButton item_modify = (ImageButton) view.findViewById(R.id.budget_list_modify);
        final LinearLayout list_area = (LinearLayout)view.findViewById(R.id.list_area);

        //getColumnindex(name) : name에 해당하는 필드의 인덱스 번호를 반환한다.
        //cursor.getString(index) : 해당 커서가 위치한 인덱스 위치의 값을 반환한다.
        String contexts = cursor.getString( cursor.getColumnIndex( managebudgetActivity.KEY_CONTEXT ) );
        String price = cursor.getString( cursor.getColumnIndex( managebudgetActivity.KEY_PRICE ) );

        Log.d(TAG, contexts + ", " + price);

        item_context.setText( contexts );
        item_price.setText( price );

        //롱클릭 이벤트
        list_area.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("삭제하시겠습니까?");

                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                MyDBHelper dbHelper;
                                SQLiteDatabase db = null;

                                dbHelper = new MyDBHelper(context);
                                db = dbHelper.getWritableDatabase();

                                db.delete(TABLE_NAME, null, null);
                                db.close();

                                Toast.makeText(context,"삭제가 왜 안되지..",Toast.LENGTH_LONG).show();
                            }
                        });

                //builder.setNegativeButton("아니오",null);

                builder.setNeutralButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                builder.create().show();

                return false;
                // true:다른 이벤트 실행 안함, false:다른 이벤트 실행
            }
        });
    }
}
