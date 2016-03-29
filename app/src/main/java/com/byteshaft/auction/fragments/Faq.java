package com.byteshaft.auction.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.auction.R;
import com.byteshaft.auction.utils.AppGlobals;

import java.util.ArrayList;
import java.util.HashMap;


public class Faq extends Fragment {

    private View mBaseView;
    private ListView mListView;
    private ArrayList<Integer> idsList;
    private HashMap<Integer, String> questionsHashMap;
    private HashMap<Integer, String> answersHashMap;
    private ViewHolder viewHolder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.faq, container, false);
        mListView = (ListView) mBaseView.findViewById(R.id.listView_faq);
        idsList = new ArrayList<>();
        questionsHashMap = new HashMap<>();
        answersHashMap = new HashMap<>();
        return mBaseView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        idsList.add(1);
        idsList.add(2);
        idsList.add(3);
        idsList.add(4);
        new LoadFaqs().execute();
    }

    class LoadFaqs extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            questionsHashMap.put(1, "How to register?");
            questionsHashMap.put(2, "Product seller procedure?");
            questionsHashMap.put(3, "How categories Works?");
            questionsHashMap.put(4, "How reviews works");
            answersHashMap.put(1, "You just enter your details and you can login. Username should " +
                    "not contains any  spaces and you will get instant replay if entered username is available");
            answersHashMap.put(2, "you post a product there are multiple senerios  if your product got no " +
                    "bid your product will be deleted after 24 hour in this case we will send a notification " +
                    "to all customers subscribed to the category in which you posted. Product will be " +
                    "automatically awarded after 24 hours");
            answersHashMap.put(3, "Go to categories and select the favourite ones and get notified when " +
                    "a product is available");
            answersHashMap.put(4, "when product is sold you can review the seller");
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            FaqAdapter faqAdapter = new FaqAdapter(getActivity().getApplicationContext(),
                    R.layout.single_faq, idsList);
            mListView.setAdapter(faqAdapter);
        }
    }

    class FaqAdapter extends ArrayAdapter {

        private ArrayList<Integer> idsArray;

        public FaqAdapter(Context context, int resource, ArrayList<Integer> arrayList) {
            super(context, resource, arrayList);
            this.idsArray = arrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater)
                        AppGlobals.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.single_faq, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.question = (TextView) convertView.findViewById(R.id.question);
                System.out.println(viewHolder.question == null);
                viewHolder.answer = (TextView) convertView.findViewById(R.id.answer);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            System.out.println(viewHolder.question == null);
            viewHolder.question.setText(questionsHashMap.get(idsArray.get(position)));
            viewHolder.answer.setText(answersHashMap.get(idsArray.get(position)));
            return convertView;
        }

        @Override
        public int getCount() {
            return idsArray.size();
        }
    }

    static class ViewHolder {
        public TextView question;
        public TextView answer;
    }
}
