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
        idsList.add(5);
        idsList.add(6);
        idsList.add(7);
        new LoadFaqs().execute();
    }

    class LoadFaqs extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            questionsHashMap.put(1, "How i can register?");
            questionsHashMap.put(2, "Can i change my favorite categories?");
            questionsHashMap.put(3, "How i can review the seller?");
            questionsHashMap.put(4, "Can i delete my bids?");
            questionsHashMap.put(5, "Can i update my bid?");
            questionsHashMap.put(6, "What will happen if no one bids on my product?");
            questionsHashMap.put(7, "How i can know if I was the winner of auction?");
            answersHashMap.put(1, "You just enter your details and press Register  button" +
                    "  then the Categories screen will appear " +
                    "to you and you must select the favorite ones to get" +
                    " notified when a product is available");
            answersHashMap.put(2, "Yes, you can change your favorite categories at " +
                    "any time by going to Categories screen");
            answersHashMap.put(3, "When the product is sold and you are the winner of " +
                    "this product you can review the seller");
            answersHashMap.put(4, "No");
            answersHashMap.put(5, "Yes, you can update your bid while the auction doesn’t stopped");
            answersHashMap.put(6, "After 24 hours if no one bids, your product will be deleted");
            answersHashMap.put(7, "You will received notification message from the application " +
                    "if you was the winner of auction");
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
