package com.firebase.login;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class ReadWriteActivity extends AppCompatActivity {

    public static Uri uri;
    static FirebaseAuth auth;
    static FirebaseStorage storage;
    static StorageReference storageRef;
    DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;
    Firebase ref;
    ArrayList<Person> persons = new ArrayList<Person>();
    ImageView img_user;
    private EditText editTextName;
    private EditText editTextAddress;
    private ListView listview;
    private Button buttonSave, btn_back;
    private ListViewAdapter adapter = null;
    private String img_url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_write);

        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
        ref = new Firebase(Config.FIREBASE_URL);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(Config.STORAGE_URL+auth.getCurrentUser().getUid());


        buttonSave = (Button) findViewById(R.id.buttonSave);
        img_user = (ImageView) findViewById(R.id.img_user);
        btn_back = (Button) findViewById(R.id.btn_back);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);

        listview = (ListView) findViewById(R.id.listview);
        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReadWriteActivity.this, MainActivity.class));
            }
        });
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                persons.clear();
                for (DataSnapshot data : dataSnapshot.child("Person").getChildren()) {

                    Person p = data.getValue(Person.class);
                    p.key = data.getKey();

                    persons.add(p);


                }


                if (adapter == null) {
                    adapter = new ListViewAdapter(ReadWriteActivity.this, persons);
                    listview.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
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


                if (uri != null) {
                    img_url = "img_" + System.currentTimeMillis() + ".jpg";
                    person.img_url = img_url;
                    storageRef.child(img_url).putFile(uri);
                }
                mDatabase.child("Person").push().setValue(person);


                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String s = dataSnapshot.toString();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            uri = data.getData();


            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(ReadWriteActivity.this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            img_user.setImageBitmap(bitmap);
//



           /* StorageReference islandRef = storageRef.child("images/img.jpg");

            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    img_user.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    String str=exception.getMessage();
                }
            });*/


        }

    }


}