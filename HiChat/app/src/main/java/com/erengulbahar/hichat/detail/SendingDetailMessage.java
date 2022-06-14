package com.erengulbahar.hichat.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erengulbahar.hichat.R;
import com.erengulbahar.hichat.databinding.FragmentSendingDetailMessageBinding;

import java.util.Calendar;

public class SendingDetailMessage extends Fragment
{
    private FragmentSendingDetailMessageBinding binding;
    Calendar calendar;

    public SendingDetailMessage()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentSendingDetailMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getData();
    }

    public void getData()
    {
        if(getArguments() != null)
        {
            String receiver = SendingDetailMessageArgs.fromBundle(getArguments()).getReceiver();
            String topic = SendingDetailMessageArgs.fromBundle(getArguments()).getTopic();
            String message = SendingDetailMessageArgs.fromBundle(getArguments()).getMessage();
            String time = SendingDetailMessageArgs.fromBundle(getArguments()).getTime();

            binding.textView5.setText(receiver);
            binding.textView6.setText(topic);
            binding.textView9.setText(time);
            binding.textView7.setText(message);
        }
    }
}