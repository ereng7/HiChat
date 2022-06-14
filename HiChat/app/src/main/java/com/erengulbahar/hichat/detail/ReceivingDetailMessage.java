package com.erengulbahar.hichat.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erengulbahar.hichat.R;
import com.erengulbahar.hichat.databinding.FragmentReceivingDetailMessageBinding;
import com.erengulbahar.hichat.databinding.FragmentReceivingMessagesBinding;

import java.util.Calendar;

public class ReceivingDetailMessage extends Fragment
{
    private FragmentReceivingDetailMessageBinding binding;
    Calendar calendar;

    public ReceivingDetailMessage()
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
        binding = FragmentReceivingDetailMessageBinding.inflate(getLayoutInflater());
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
            String sender = ReceivingDetailMessageArgs.fromBundle(getArguments()).getSender();
            String topic = ReceivingDetailMessageArgs.fromBundle(getArguments()).getTopic();
            String message = ReceivingDetailMessageArgs.fromBundle(getArguments()).getMessage();
            String time = ReceivingDetailMessageArgs.fromBundle(getArguments()).getTime();

            binding.textView15.setText(sender);
            binding.textView16.setText(topic);
            binding.textView19.setText(time);
            binding.textView17.setText(message);
        }
    }
}