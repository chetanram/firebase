package com.firebase.login;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReadWriteActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextAddress;
    private ListView listview;
    private Button buttonSave;
    static FirebaseAuth auth;
    DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;
    Firebase ref;
    ArrayList<Person> persons=new ArrayList<Person>();
    private ListViewAdapter adapter=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_write);

        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();


        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
        ref = new Firebase(Config.FIREBASE_URL);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);

        listview = (ListView) findViewById(R.id.listview);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                persons.clear();
                for (DataSnapshot data:dataSnapshot.child("Person").getChildren()){

                    Person p=data.getValue(Person.class);
                    p.key=data.getKey();

                    persons.add(p);



                }


                adapter = new ListViewAdapter(ReadWriteActivity.this, persons);
                listview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating firebase object
                //Getting values to store
                String name = editTextName.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();

                //Creating Person object
                Person person = new Person();

                //Adding values
                person.setName(name);
                person.setAddress(address);

                mDatabase.child("Person").push().setValue(person);

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String s=dataSnapshot.toString();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

               /* ref.child("users").child(auth.getCurrentUser().getUid()).child("Person").setValue(person);



                //Value event listener for realtime data update

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //Getting the data from snapshot
                            Person person = postSnapshot.getValue(Person.class);

                            //Adding it to a string
                            String string = "Name: " + person.getName() + "\nAddress: " + person.getAddress() + "\n\n";

                            //Displaying it on textview
                            textViewPersons.setText(string);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });*/

            }
        });

    }


}