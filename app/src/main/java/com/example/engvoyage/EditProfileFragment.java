package com.example.engvoyage;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;

public class EditProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private User user;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference docRefUser;
    private TextInputEditText nameValue;
    private TextInputEditText surnameValue;
    private TextInputEditText emailValue;
    private OnProfileUpdatedListener profileUpdatedListener;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance(User user) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Make sure the host activity implements the interface
        try {
            profileUpdatedListener = (OnProfileUpdatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnProfileUpdatedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_PARAM1);
        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String uid = currentUser.getUid();
        docRefUser = db.collection("users").document(uid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        cancelEdit(view);
        setCurrentDetails(view);
        onClickSave(view);
        return view;
    }

    public void cancelEdit(View view) {
        Button cancelBtn = (Button) view.findViewById(R.id.backBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getParentFragmentManager().beginTransaction();
                fr.replace(R.id.frame_layout, ProfileFragment.newInstance(user));
                fr.commit();
            }
        });
    }

    public void onClickSave(View view) {
        Button saveBtn = (Button) view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewDetails();
            }
        });
    }

    public void setNewDetails() {
        String name = nameValue.getText().toString().trim();
        String surname = surnameValue.getText().toString().trim();

        if (name.isEmpty()) {
            nameValue.setError("The name cannot be empty");
            nameValue.requestFocus();
            return;
        }
        if (surname.isEmpty()) {
            surnameValue.setError("The name cannot be empty");
            surnameValue.requestFocus();
            return;
        }

        docRefUser.update("name",name, "surname", surname)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("EditProfileFragment", "DocumentSnapshot successfully updated!");
                        Toast.makeText(getActivity(),"Details updated!",Toast.LENGTH_SHORT).show();
                        user.setName(name);
                        user.setSurname(surname);
                        profileUpdatedListener.onProfileUpdated(user);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EditProfileFragment", "Error updating document", e);
                        Toast.makeText(getActivity(),"Error! Changes were not applied",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setCurrentDetails(View view) {
        nameValue = (TextInputEditText) view.findViewById(R.id.nameInputEdit);
        surnameValue = (TextInputEditText) view.findViewById(R.id.surnameInputEdit);
        nameValue.setText(user.getName());
        surnameValue.setText(user.getSurname());
    }

    public interface OnProfileUpdatedListener {
        void onProfileUpdated(User updatedUser);
    }
}