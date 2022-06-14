package com.erengulbahar.hichat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.erengulbahar.hichat.R;
import com.erengulbahar.hichat.databinding.ReceiveRowBinding;
import com.erengulbahar.hichat.model.SendModel;
import com.erengulbahar.hichat.user.ReceivingMessagesDirections;

import java.util.ArrayList;

public class ReceiveAdapter extends RecyclerView.Adapter<ReceiveAdapter.ReceiveHolder>
{
    ArrayList<SendModel> sendModelArrayList;
    Context context;

    public ReceiveAdapter(ArrayList<SendModel> sendModelArrayList, Context context)
    {
        this.sendModelArrayList = sendModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReceiveAdapter.ReceiveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ReceiveRowBinding receiveRowBinding = ReceiveRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ReceiveHolder(receiveRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiveAdapter.ReceiveHolder holder, int position)
    {
        holder.receiveRowBinding.textView4.setText(sendModelArrayList.get(position).topic);
        holder.receiveRowBinding.timeText.setText(sendModelArrayList.get(position).hourTime);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ReceivingMessagesDirections.ActionReceivingMessagesToReceivingDetailMessage action = ReceivingMessagesDirections.actionReceivingMessagesToReceivingDetailMessage();

                action.setReceiver(sendModelArrayList.get(position).receiver);
                action.setSender(sendModelArrayList.get(position).sender);
                action.setTopic(sendModelArrayList.get(position).topic);
                action.setMessage(sendModelArrayList.get(position).message);
                action.setTime(sendModelArrayList.get(position).fullTime);

                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sendModelArrayList.size();
    }

    public class ReceiveHolder extends RecyclerView.ViewHolder
    {
        ReceiveRowBinding receiveRowBinding;

        public ReceiveHolder(ReceiveRowBinding receiveRowBinding)
        {
            super(receiveRowBinding.getRoot());
            this.receiveRowBinding = receiveRowBinding;
        }
    }
}