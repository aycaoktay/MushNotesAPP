package com.sao.mushnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {
        EditText titleText , contentText ;
        ImageButton saveNoteBtn;
        TextView pagetitleTv;
        String title , content , docId ;
        boolean isEditMode = false;
        TextView deletenoteTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleText = findViewById(R.id.notes_title_text);
        contentText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pagetitleTv = findViewById(R.id.page_title);
        deletenoteTv = findViewById(R.id.delete_note_tv);
        //recieve data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleText.setText(title);
        contentText.setText(content);
        if (isEditMode){
            pagetitleTv.setText("Edit your note");
            deletenoteTv.setVisibility(View.VISIBLE);
        }



        saveNoteBtn.setOnClickListener((v) -> saveNote());
        deletenoteTv.setOnClickListener((v) -> deleteNoteFromFirebase() );

    }
    void saveNote(){
        String noteTitle = titleText.getText().toString();
        String noteContent = contentText.getText().toString();
        if (noteTitle == null || noteTitle.isEmpty() ){
            titleText.setError("Title is required.");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }
    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if (isEditMode){
            //Update note
            documentReference = Utility.getCollectionForNotes().document(docId);
        }else{
            //create new note ( yeni not olustur)
            documentReference = Utility.getCollectionForNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Not başarıyla eklendi
                    Utility.showToast(NoteDetailsActivity.this, "Note added successfully");
                    finish();
                } else {
                    Utility.showToast(NoteDetailsActivity.this, "Failed while adding note");
                }
            }
        });

    }
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;

                documentReference = Utility.getCollectionForNotes().document(docId);

            documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Not başarıyla silindi
                    Utility.showToast(NoteDetailsActivity.this, "Note deleted successfully");
                    finish();
                } else {
                    Utility.showToast(NoteDetailsActivity.this, "Failed while deleting note");
                }
            }
        });
    }
}