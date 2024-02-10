package edu.utem.ftmk.studentattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import edu.utem.ftmk.studentattendance.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAdd.setOnClickListener(view -> {
            String studNo = binding.edtStudNo.getText().toString();
            String studName = binding.edtStudName.getText().toString();
            String gender = binding.edtGender.getText().toString();
            String studBd = binding.edtStudBd.getText().toString();
            String studState = binding.edtStudState.getText().toString();
            String email = binding.edtEmail.getText().toString();
            Student student = new Student(studName, studNo, email, gender, studBd, studState);
            db.collection("students")
                    .add(student)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Success save to Firestore!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Could not save to Firestore!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        binding.btnGet.setOnClickListener(view ->
                db.collection("students")
                        .whereEqualTo("strStudNo", binding.edtStudNo.getText().toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            //for(DocumentSnapshot documentSnapshot)

                            for(QueryDocumentSnapshot document : task.getResult())
                            {
                                Student student =  document.toObject(Student.class);
                                String strDocId = document.getId();
                                if(student != null)
                                {
                                    binding.edtGender.setText(student.getStrGender());
                                    binding.edtStudName.setText(student.getStrFullname());
                                    binding.edtStudBd.setText(student.getStrBirthdate());
                                }
                            }
                        }
                    }
                }));

        binding.btnDel.setOnClickListener(view ->
                db.collection("students")
                        .whereEqualTo("strStudNo", binding.edtStudNo.getText().toString()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot document : task.getResult())
                            {
                                String strDocId = document.getId();
                                db.collection("students").document(strDocId).delete();
                            }
                        }
                    }
                }));

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        db.collection("students")
                .whereEqualTo("strStudNo", binding.edtStudNo.getText().toString()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {

                            QuerySnapshot documentSnapshot = task.getResult();
                            String strDocId = documentSnapshot.getDocuments().get(0).getId();
                            Student updateStud = documentSnapshot.getDocuments().get(0).toObject(Student.class);
                            updateStud.setStrFullname("Aiman Daa");
                            updateStud.setStrState("MLK");

                            try {
                                db.collection("students").document(strDocId).
                                        set(updateStud).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(),"Yeay Saved!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }catch (Exception ish)
                            {
                                Log.d("Grr:", "Arggh:"+ish.getMessage());
                                String strDebug= "";
                            }
                        }
                    }
                });
            }
        });

    }
}