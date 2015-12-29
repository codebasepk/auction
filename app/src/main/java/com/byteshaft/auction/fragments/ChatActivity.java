package com.byteshaft.auction.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.auction.R;


public class ChatActivity extends Activity implements View.OnClickListener {

    private TextView textViewContactName;
    private ImageButton buttonSend;
    private EditText editTextMessage;
    private String contactName;
    private String mContextUserTable;
    String[] messages = new String[] {"incoming" , "outgoing",  "outgoing"};
    public ChatArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        textViewContactName = (TextView) findViewById(R.id.tv_contact_name_chat);
        editTextMessage = (EditText) findViewById(R.id.et_chat);
        buttonSend = (ImageButton) findViewById(R.id.button_chat_send);
        buttonSend.setOnClickListener(this);
        ListView bubbleList = (ListView) findViewById(R.id.lv_chat);
        Intent intent = getIntent();
        contactName = intent.getStringExtra("CONTACT_NAME");
//        MessagesDatabase database = new MessagesDatabase(this);
        mContextUserTable = intent.getStringExtra("user_table");
        textViewContactName.setText(contactName);
        try {
//            messages = database.getMessagesForContact(mContextUserTable);
        } catch (SQLiteException e) {
            e.printStackTrace();
            // Apparently no table exists.
        }

        adapter = new ChatArrayAdapter(
                this, R.layout.singlemessage_chat, messages);
        bubbleList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

    }

    // custom raw chat interface
    private class ChatArrayAdapter extends ArrayAdapter {

        public ChatArrayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.singlemessage_chat, parent, false);
                holder = new ViewHolder();
                holder.layout = (LinearLayout) convertView.findViewById(R.id.singleMessageContainer);
                holder.title = (TextView) holder.layout.findViewById(R.id.singleMessage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
                holder.title.setText(messages[0]);
            return convertView;
        }
    }

    // custom Member class for viewHolder to access xml elements
    static class ViewHolder {
        public TextView title;
        public LinearLayout layout;
    }
}
