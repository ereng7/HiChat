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

import com.erengulbahar.hichat.databinding.FragmentHomepageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Homepage extends Fragment
{
    private FragmentHomepageBinding binding;
    FirebaseAuth auth;

    public Homepage()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentHomepageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                login(view);
            }
        });

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                register(view);
            }
        });
    }

    public void login(View view)
    {
        String email = binding.mailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if(email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(getContext(),"E-posta ve şifre alanını boş bırakmayınız !",Toast.LENGTH_SHORT).show();
        }

        else
        {
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult)
                {
                    NavDirections action = HomepageDirections.actionHomepageToReceivingMessages();
                    Navigation.findNavController(view).navigate(action);
                    Toast.makeText(getContext(),"Hoş Geldiniz !",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(getContext(),"E-posta veya şifre hatalı !",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void register(View view)
    {
        String email = binding.mailText.getText().toString();
        String password = binding.passwordText.getText().toString();

        if(email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(getContext(),"E-posta ve şifre alanını boş bırakmayınız !",Toast.LENGTH_SHORT).show();
        }

        else
        {
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult)
                {
                    NavDirections action = HomepageDirections.actionHomepageToReceivingMessages();
                    Navigation.findNavController(view).navigate(action);
                    Toast.makeText(getContext(),"Hoş Geldiniz !",Toast.LENGTH_SHORT).show();
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