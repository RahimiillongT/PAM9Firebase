package com.example.pam9firebase;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertUpdateActivity extends AppCompatActivity {

    private EditText etTitle, etDesc;
    private Button btnSubmit, btnClear;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insert_update);

        etTitle = findViewById(R.id.et_title);
        etDesc = findViewById(R.id.et_description);
        btnSubmit = findViewById(R.id.btn_submit);
        btnClear = findViewById(R.id.btn_clear);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://pam9firebase-3f452-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("notes");

        if (getIntent().hasExtra("noteId")){
            noteId = getIntent().getStringExtra("noteId");
            etTitle.setText(getIntent().getStringExtra("title"));
            etDesc.setText(getIntent().getStringExtra("desc"));
            btnSubmit.setText("Update");
        } else {
            noteId = null;
        }

        btnClear.setOnClickListener(v->{
            etTitle.setText("");
            etDesc.setText("");
        });

        btnSubmit.setOnClickListener(v->{
            if (validateForm()){
                submitData();
            }
        });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            etTitle.setError("Required");
            result = false;
        } else {
            etTitle.setError(null);
        }
        if (TextUtils.isEmpty(etDesc.getText().toString())) {
            etDesc.setError("Required");
            result = false;
        } else {
            etDesc.setError(null);
        }
        return result;
    }

    private void submitData() {
        String title = etTitle.getText().toString();
        String desc = etDesc.getText().toString();
        Note note = new Note(title, desc);

        String uid = mAuth.getUid();
        if (noteId == null) {
            databaseReference.child(uid).push().setValue(note)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(InsertUpdateActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(InsertUpdateActivity.this, "Failed to add note", Toast.LENGTH_SHORT).show());
        } else {
            databaseReference.child(uid).child(noteId).setValue(note)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(InsertUpdateActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(InsertUpdateActivity.this, "Failed to update note", Toast.LENGTH_SHORT).show());
        }
    }
}