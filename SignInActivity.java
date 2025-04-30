package com.matheussilvagarcia.ecomunity;


import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.PickVisualMediaRequest;

import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;

import androidx.constraintlayout.widget.ConstraintLayout;


import android.Manifest;

import android.content.Intent;

import android.content.pm.PackageManager;

import android.graphics.Bitmap;

import android.net.Uri;

import android.os.Build;

import android.os.Bundle;

import android.provider.MediaStore;

import android.transition.Slide;

import android.transition.Transition;

import android.transition.TransitionManager;

import android.view.Gravity;

import android.view.View;

import android.widget.Button;

import android.widget.EditText;

import android.widget.ImageView;

import android.widget.TextView;

import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.UploadTask;

import com.matheussilvagarcia.ecomunity.Model.HomeActivity;

import com.matheussilvagarcia.ecomunity.Model.ProfileModel;


import java.io.ByteArrayOutputStream;

import java.io.IOException;


public class SignInActivity extends AppCompatActivity {


    FirebaseAuth mAuth;

    FirebaseFirestore firebaseFirestore;

    CollectionReference collectionReference;

    FirebaseStorage firebaseStorage;

    StorageReference storageReference;


    Bitmap bitmap;


    ConstraintLayout layout_main;

    CardView SignInCard, RegisterCard, RegisterImage;

    EditText SignInEmail, SignInPassword, RegisterName, RegisterEmail, RegisterPassword, RegisterPasswordAgain;

    Button SignInButton, RegisterButton;

    TextView TVRegister, TVSignIn;

    ImageView RegisterCustomImage;


    ActivityResultLauncher<PickVisualMediaRequest> pickVisualMedia =

            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {

                if (uri != null) {

                    RegisterCustomImage.setImageURI(uri);

                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);

                    } catch (IOException e) {

                        throw new RuntimeException(e);

                    }

                    Toast.makeText(this, "Imagem selecionada com sucesso!", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show();

                }

            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


// Inicializando as views

        layout_main = findViewById(R.id.signin);

        SignInCard = findViewById(R.id.SignInCard);

        RegisterCard = findViewById(R.id.RegisterCard);

        RegisterImage = findViewById(R.id.RegisterImage);


        SignInEmail = findViewById(R.id.SignInEmail);

        SignInPassword = findViewById(R.id.SignInPassword);

        RegisterName = findViewById(R.id.RegisterName);

        RegisterEmail = findViewById(R.id.RegisterEmail);

        RegisterPassword = findViewById(R.id.RegisterPassword);

        RegisterPasswordAgain = findViewById(R.id.RegisterPasswordAgain);


        SignInButton = findViewById(R.id.SignInButton);

        RegisterButton = findViewById(R.id.RegisterButton);


        TVRegister = findViewById(R.id.TVRegister);

        TVSignIn = findViewById(R.id.TVSignIn);


        RegisterCustomImage = findViewById(R.id.RegisterCustomImage);


// Inicializando o Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("Profiles");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Profiles");

// Agora sim pode chamar
        checklogin();



// Pedido de permissão para acessar imagens no dispositivo

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1001);

            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);

            }

        }


// Comportamento de seleção de imagem de perfil

        RegisterImage.setOnClickListener(v -> {

            pickVisualMedia.launch(new PickVisualMediaRequest.Builder()

                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)

                    .build());

        });


// Login

        SignInButton.setOnClickListener(v -> {

            if (SignInEmail.getText().toString().equals("")) {

                SignInEmail.setError("Digite seu Email");

            } else if (SignInPassword.getText().toString().equals("")) {

                SignInPassword.setError("Digite sua senha");

            } else {

                mAuth.signInWithEmailAndPassword(SignInEmail.getText().toString(), SignInPassword.getText().toString()).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (mAuth.getCurrentUser().isEmailVerified()) {

                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);

                            startActivity(intent);
                            finish();

                        } else {

                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {

                                if (task1.isSuccessful()) {

                                    Toast.makeText(SignInActivity.this, "Código de verificação enviado para o email. Verifique para fazer login.", Toast.LENGTH_SHORT).show();

                                } else {

                                    Toast.makeText(SignInActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            });

                        }

                    } else {

                        Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                });

            }

        });


// Registro de usuário

        RegisterButton.setOnClickListener(v -> {

            if (RegisterCustomImage.getDrawable() == null) {

                Toast.makeText(SignInActivity.this, "Selecione sua foto de perfil", Toast.LENGTH_SHORT).show();

            } else if (RegisterName.getText().toString().equals("")) {

                RegisterName.setError("Digite seu nome de usuário");

            } else if (RegisterEmail.getText().toString().equals("")) {

                RegisterEmail.setError("Digite seu email");

            } else if (RegisterPassword.getText().toString().equals("")) {

                RegisterPassword.setError("Digite sua senha");

            } else if (RegisterPasswordAgain.getText().toString().equals("")) {

                RegisterPasswordAgain.setError("Confirme sua senha");

            } else if (!RegisterPassword.getText().toString().equals(RegisterPasswordAgain.getText().toString())) {

                Toast.makeText(SignInActivity.this, "As senhas não são iguais", Toast.LENGTH_SHORT).show();

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);

                builder.setMessage("Criando perfil, aguarde...");

                builder.setCancelable(false);


                AlertDialog alertDialog = builder.create();

                alertDialog.show();


                mAuth.createUserWithEmailAndPassword(RegisterEmail.getText().toString(), RegisterPassword.getText().toString()).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                        byte[] image = baos.toByteArray();


                        UploadTask uploadTask = storageReference.child(mAuth.getCurrentUser().getUid()).putBytes(image);

                        uploadTask.addOnCompleteListener(task1 -> {

                            if (task1.isSuccessful()) {

                                String path = task1.getResult().getMetadata().getPath();

                                ProfileModel model = new ProfileModel(mAuth.getCurrentUser().getUid(),

                                        RegisterName.getText().toString(), RegisterEmail.getText().toString(),

                                        RegisterPassword.getText().toString(), path);

                                collectionReference.document(mAuth.getCurrentUser().getUid()).set(model).addOnCompleteListener(task2 -> {

                                    if (task2.isSuccessful()) {

                                        alertDialog.dismiss();

                                        Toast.makeText(SignInActivity.this, "Perfil criado com sucesso", Toast.LENGTH_SHORT).show();

                                    } else {

                                        alertDialog.dismiss();

                                        mAuth.getCurrentUser().delete();

                                        Toast.makeText(SignInActivity.this, task2.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }

                                });

                            } else {

                                alertDialog.dismiss();

                                mAuth.getCurrentUser().delete();

                                Toast.makeText(SignInActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        });

                    } else {

                        Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                });

            }

        });


// Alternar entre telas de login e registro

        TVRegister.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Transition transition = new Slide(Gravity.END);

                transition.setDuration(300);

                transition.addTarget(SignInCard);


                Transition transition2 = new Slide(Gravity.START);

                transition2.setDuration(300);

                transition2.addTarget(RegisterCard);


                TransitionManager.beginDelayedTransition(layout_main, transition);

                SignInCard.setVisibility(View.GONE);


                TransitionManager.beginDelayedTransition(layout_main, transition2);

                RegisterCard.setVisibility(View.VISIBLE);

            } else {

                SignInCard.setVisibility(View.GONE);

                RegisterCard.setVisibility(View.VISIBLE);

            }

        });


        TVSignIn.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Transition transition = new Slide(Gravity.END);

                transition.setDuration(300);

                transition.addTarget(RegisterCard);


                Transition transition2 = new Slide(Gravity.START);

                transition2.setDuration(300);

                transition2.addTarget(SignInCard);


                TransitionManager.beginDelayedTransition(layout_main, transition);

                RegisterCard.setVisibility(View.GONE);


                TransitionManager.beginDelayedTransition(layout_main, transition2);

                SignInCard.setVisibility(View.VISIBLE);

            } else {

                RegisterCard.setVisibility(View.GONE);

                SignInCard.setVisibility(View.VISIBLE);

            }

        });

    }


    void checklogin()

    {

        if (mAuth.getCurrentUser()!= null) {

            if (mAuth.getCurrentUser().isEmailVerified()) {

                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);

                startActivity(intent);

            }

        }

    }

}