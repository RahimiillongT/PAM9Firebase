package com.example.pam9firebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PublicKey;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context context;
    private List<Note> noteList;
    private DatabaseReference databaseReference;

    public NoteAdapter(Context context, List<Note> noteList){
        this.context = context;
        this.noteList = noteList;
        this.databaseReference = FirebaseDatabase.getInstance("https://pam9firebase-3f452-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("notes");
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position){
        Note note = noteList.get(position);
        holder.tvTitle.setText(note.getTitle());
        holder.tvDesc.setText(note.getDescription());

        holder.btnEdit.setOnClickListener(v-> {
            Intent intent = new Intent(context, InsertUpdateActivity.class);
            intent.putExtra("noteId", note.getId());
            intent.putExtra("title", note.getTitle());
            intent.putExtra("desc", note.getDescription());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v-> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        databaseReference.child(uid).child(note.getId()).removeValue().addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
                            if (noteList.size() > 0 && position < noteList.size()) {
                                noteList.remove(position);
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to delete Note", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public  int getItemCount(){
        return noteList.size();
    }

    public  class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvDesc;
        Button btnEdit, btnDelete;

        public  NoteViewHolder(@NonNull View itemView){
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
