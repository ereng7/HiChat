package com.erengulbahar.hichat.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.erengulbahar.hichat.R;
import com.erengulbahar.hichat.databinding.FragmentWriteMessageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class WriteMessage extends Fragment
{
    private FragmentWriteMessageBinding binding;
    private FirebaseFirestore firestore;
    FirebaseAuth auth;

    String receiver;
    String sender;
    String topic;
    String message;

    public WriteMessage()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentWriteMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendMessageButton(view);
            }
        });
    }

    public void sendMessageButton(View view)
    {
        receiver = binding.receiverMailText.getText().toString();
        sender = auth.getCurrentUser().getEmail();
        topic = binding.topicText.getText().toString();
        message = binding.messageText.getText().toString();

        if(receiver.isEmpty() || topic.isEmpty() || message.isEmpty())
        {
            Toast.makeText(getContext(),"Lütfen her yeri doldurunuz !",Toast.LENGTH_SHORT).show();
        }

        else
        {
            HashMap<String,Object> data = new HashMap<>();
            String uniqueId = UUID.randomUUID().toString();

            data.put("receiver",receiver);
            data.put("sender",sender);
            data.put("topic",topic);
            data.put("message",message);
            data.put("time", System.currentTimeMillis());
            data.put("docId",uniqueId);

            firestore.collection("Messages").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference)
                {
                    NavDirections action = WriteMessageDirections.actionWriteMessageToReceivingMessages();
                    Navigation.findNavController(view).navigate(action);
                    Toast.makeText(getContext(),"Mesaj gönderildi !",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}