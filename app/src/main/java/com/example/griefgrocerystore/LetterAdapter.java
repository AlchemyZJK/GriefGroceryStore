package com.example.griefgrocerystore;

        import android.content.Context;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import java.util.LinkedList;
        import java.util.List;

        import cn.bmob.v3.BmobQuery;
        import cn.bmob.v3.exception.BmobException;
        import cn.bmob.v3.listener.FindListener;


public class LetterAdapter extends BaseAdapter {
    private LinkedList<Letter> data;
    private Context context;

    public LetterAdapter(LinkedList<Letter> mData, Context mContext) {
        data = mData;
        context = mContext;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
//        Log.i("ListView getView()", "getView" + position);
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
        final TextView textViewName, textViewDate, textViewIntro;
        ImageView iconRead;
        textViewName = view.findViewById(R.id.sender_or_receiver);
        textViewDate = view.findViewById(R.id.letter_date);
        textViewIntro = view.findViewById(R.id.letter_intro);
        iconRead = view.findViewById(R.id.letter_read_icon);

        // 装备信件寄信者姓名/来信者姓名
        if(data.get(position).getLetter_Reply() == 0) {
            Long userId = data.get(position).getUser_ID();
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("User_ID", userId);
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    User user = list.get(0);
                    String fromLetter = "From：" + user.getUser_Name();
                    textViewName.setText(fromLetter);
                }
            });
        }
        else {
            Long userId = data.get(position).getLetter_contact();
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("User_ID", userId);
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    User user = list.get(0);
                    String toLetter = "To：" + user.getUser_Name();
                    textViewName.setText(toLetter);
                }
            });
        }
        // 装备信件日期
        textViewDate.setText(data.get(position).getLetter_Date());
        // 装备信件简介
        if(data.get(position).getLetter_Content().length() > 20) {
            String intro = data.get(position).getLetter_Content().substring(0, 20) + "...";
            textViewIntro.setText(intro);
        }
        else {
            String intro = data.get(position).getLetter_Content() + "...";
            textViewIntro.setText(intro);
        }
        // 显示已读/未读
        if(!data.get(position).getLetter_Read())
            iconRead.setVisibility(View.VISIBLE);
        else
            iconRead.setVisibility(View.INVISIBLE);

        return view;
    }
}
