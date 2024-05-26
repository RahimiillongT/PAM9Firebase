package com.example.pam9firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Dashboard extends AppCompatActivity {

    private TextView tvEmail;
    private Button btnKeluar, btnAdd;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;
    private FirebaseAuth mAuth;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        tvEmail = findViewById(R.id.tv_email);
        btnKeluar = findViewById(R.id.btn_keluar);
        btnAdd = findViewById(R.id.btn_add);
        recyclerView = findViewById(R.id.recycler_note);

        mAuth = FirebaseAuth.getInstance();
        btnKeluar.setOnClickListener(v-> logOut());
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, InsertUpdateActivity.class);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(this, noteList);
        recyclerView.setAdapter(noteAdapter);

        loadNotes();
//        firebaseDatabase = FirebaseDatabase.getInstance("https://pam9firebase-3f452-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        databaseReference = firebaseDatabase.getReference();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            tvEmail.setText(currentUser.getEmail());
        }
    }


    public void logOut(){
        mAuth.signOut();
        Intent intent = new Intent(Dashboard.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
        startActivity(intent);
    }

    private void loadNotes(){
        String uid = mAuth.getUid();
        FirebaseDatabase.getInstance("https://pam9firebase-3f452-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("notes").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noteList.clear();
                for (DataSnapshot noteSnapshot : snapshot.getChildren()){
                    Note note = noteSnapshot.getValue(Note.class);
                    note.setId(noteSnapshot.getKey());
                    noteList.add(note);
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Failed to load notes", Toast.LENGTH_SHORT).show();
            }
        });
    }

}