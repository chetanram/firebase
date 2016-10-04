package com.firebase.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chetan on 4/10/16.
 */

public class ListViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<Person> persons;
    LayoutInflater layoutInflater;
    DatabaseReference mDatabase;

    public ListViewAdapter(Context context, ArrayList<Person> persons){
        this.context=context;
        this.persons=persons;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(ReadWriteActivity.auth.getCurrentUser().getUid());
    }
    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int i) {
        return persons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= layoutInflater.inflate(R.layout.listview_items,parent,false);
        final Person person=persons.get(position);
        TextView txtName= (TextView) view.findViewById(R.id.txtName);
        TextView txtAddress=(TextView) view.findViewById(R.id.txtAddress);
        Button btnDelete=(Button) view.findViewById(R.id.btnDelete);
        Button btnUpdate=(Button) view.findViewById(R.id.btnUpdate);

        txtName.setText(person.name);
        txtAddress.setText(person.address);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("Person").child(person.key).removeValue();

            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map =new HashMap<String, Object>();
                map.put("name","Chetan");
                map.put("address","Ram");


                mDatabase.child("Person").child(person.key).updateChildren(map);

            }
        });

        return view;
    }


}
