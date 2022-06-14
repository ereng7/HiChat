package com.erengulbahar.hichat.user;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.erengulbahar.hichat.R;
import com.erengulbahar.hichat.adapter.SendAdapter;
import com.erengulbahar.hichat.databinding.FragmentSendingMessagesBinding;
import com.erengulbahar.hichat.model.SendModel;
import com.erengulbahar.hichat.swhelper.MyButtonClickListener;
import com.erengulbahar.hichat.swhelper.SwipeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class SendingMessages extends Fragment
{
    private FragmentSendingMessagesBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    SendAdapter sendAdapter;
    ArrayList<SendModel> sendModelArrayList;
    Calendar calendar;

    public SendingMessages()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);
        calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentSendingMessagesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        sendModelArrayList = new ArrayList<>();

        getData();

        sendAdapter = new SendAdapter(sendModelArrayList);
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(view.getContext()));
        binding.recyclerView2.setAdapter(sendAdapter);

        SwipeHelper swipeHelper = new SwipeHelper(getContext(),binding.recyclerView2,200)
        {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer)
            {
                buffer.add(new MyButton(getContext(), R.drawable.ic_delete, Color.parseColor("#FFFA0707"), new MyButtonClickListener() {
                    @Override
                    public void onClick(int pos)
                    {
                        String docId = sendModelArrayList.get(viewHolder.getAdapterPosition()).docId;

                        sendModelArrayList.remove(viewHolder.getAdapterPosition());
                        sendAdapter.notifyDataSetChanged();

                        deleteData(docId);
                    }
                }));
            }
        };
    }

    public void getData()
    {
        String email = auth.getCurrentUser().getEmail();

        firestore.collection("Messages").orderBy("time", Query.Direction.DESCENDING).whereEqualTo("sender",email).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
            {
                if(error != null)
                {
                    Toast.makeText(getContext(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }

                if(value != null)
                {
                    for(DocumentSnapshot snapshot : value.getDocuments())
                    {
                        Map<String,Object> data = snapshot.getData();

                        String docId = (String) data.get("docId");
                        String receiver = (String) data.get("receiver");
                        String sender = (String) data.get("sender");
                        String topic = (String) data.get("topic");
                        String message = (String) data.get("message");
                        Object time = data.get("time");
                        String newTime = time.toString();
                        Long longTime = Long.parseLong(newTime);

                        calendar.setTimeInMillis(longTime);

                        String date = "" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR);
                        String dateTime = "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                        String fullTime = date + " " + dateTime;

                        sendModelArrayList.add(new SendModel(receiver,sender,topic,message,fullTime,dateTime,docId));
                    }

                    sendAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void deleteData(String docId)
    {
        firestore.collection("Messages").whereEqualTo("docId",docId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if(task.isComplete() && !task.getResult().isEmpty())
                {
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    String documentId = documentSnapshot.getId();

                    firestore.collection("Messages").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused)
                        {
                            Toast.makeText(getContext(),"Mesaj Başarıyla silindi !",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(getContext(),"Bir hata oluştu tekrar deneyiniz !",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        sendAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.logoutMenu)
        {
            NavDirections action = SendingMessagesDirections.actionSendingMessagesToHomepage();
            Navigation.findNavController(requireView()).navigate(action);
        }

        return super.onOptionsItemSelected(item);
    }
}