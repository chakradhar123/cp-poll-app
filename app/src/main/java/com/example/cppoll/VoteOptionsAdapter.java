package com.example.cppoll;


import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoDatabase;

class VoteOptionsAdapter implements ListAdapter {
    ArrayList<ViewOption> arrayList;
    Context context;
    public VoteOptionsAdapter(Context context, ArrayList<ViewOption> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
    }
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
//    public void  vote(){
//        MongoClient mongoClient;
//        MongoDatabase mongoDatabase;
//        Realm.init(VotePollActivity.class.t);
//        String appID = "cp-poll-rrqey";
//        App app = new App(new AppConfiguration.Builder(appID)
//                .build());
//
//        Credentials credentials = Credentials.anonymous();
//    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewOption o = arrayList.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.vote_option, null);

            TextView tittle = convertView.findViewById(R.id.title);

            tittle.setText(o.Name);
            convertView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView) parent).performItemClick(v, position, 0);
                }
            });



        }
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        if(arrayList.size()==0){
            return 1;
        }
        return arrayList.size();
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
}
