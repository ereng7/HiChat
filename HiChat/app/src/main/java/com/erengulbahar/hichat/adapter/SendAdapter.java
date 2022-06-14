package com.erengulbahar.hichat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.erengulbahar.hichat.databinding.SendRowBinding;
import com.erengulbahar.hichat.model.SendModel;
import com.erengulbahar.hichat.user.SendingMessagesDirections;

import java.util.ArrayList;

public class SendAdapter extends RecyclerView.Adapter<SendAdapter.SendHolder>
{
    ArrayList<SendModel> sendModelArrayList;

    public SendAdapter(ArrayList<SendModel> sendModelArrayList)
    {
        this.sendModelArrayList = sendModelArrayList;
    }

    @NonNull
    @Override
    public SendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        SendRowBinding sendRowBinding =SendRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new SendHolder(sendRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SendHolder holder, int position)
    {
        holder.sendRowBinding.textView3.setText(sendModelArrayList.get(position).topic);
        holder.sendRowBinding.timeText2.setText(sendModelArrayList.get(position).hourTime);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendingMessagesDirections.ActionSendingMessagesToSendingDetailMessage action = SendingMessagesDirections.actionSendingMessagesToSendingDetailMessage();

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
    public int getItemCount()
    {
        return sendModelArrayList.size();
    }

    public class SendHolder extends RecyclerView.ViewHolder
    {
        SendRowBinding sendRowBinding;

        public SendHolder(SendRowBinding sendRowBinding)
        {
            super(sendRowBinding.getRoot());
            this.sendRowBinding = sendRowBinding;
        }
    }
}