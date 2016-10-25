package com.firebase.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.firebase.login.ReadWriteActivity.auth;
import static com.firebase.login.ReadWriteActivity.storageRef;
import static com.firebase.login.ReadWriteActivity.uri;

/**
 * Created by chetan on 4/10/16.
 */

public class ListViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<Person> persons;
    LayoutInflater layoutInflater;
    DatabaseReference mDatabase;
    StorageReference islandRef = null;


    public ListViewAdapter(Context context, ArrayList<Person> persons){
        this.context=context;
        this.persons=persons;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
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
        final ImageView img_item=(ImageView) view.findViewById(R.id.img_item);

        if(person.img_url!=null) {
            islandRef = storageRef.child(person.img_url);

            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    img_item.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    String str = exception.getMessage();
                }
            });
        }
        txtName.setText(person.name);
        txtAddress.setText(person.address);


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("Person").child(person.key).removeValue();
                if(islandRef!=null){

                    islandRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context,"image Deleted",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map =new HashMap<String, Object>();
                map.put("name","Chetan");
                map.put("address","Ram");

                if(uri!=null) {
                    storageRef.child(person.img_url).putFile(uri);
                }
                mDatabase.child("Person").child(person.key).updateChildren(map);
                notifyDataSetChanged();

            }
        });

        return view;
    }


}
