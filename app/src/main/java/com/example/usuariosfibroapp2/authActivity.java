package com.example.usuariosfibroapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class authActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    private Button btn_signin;
    private EditText edit_mail, edit_password;

    private String sEmail = "";
    private String sPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //RELACIONNO LAYOUT CON BACKEND
        btn_signin = (Button) findViewById(R.id.btn_signin);
        edit_mail = (EditText) findViewById(R.id.edit_mail);
        edit_password = (EditText) findViewById(R.id.edit_password);

        //PONGO TITULO EN LA ACTIVITY
        setTitle("Autenticación");

        //PREOGRAMA
        mAuth = FirebaseAuth.getInstance();

        //SI SE HACE CLICK EN SIGN IN
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail = edit_mail.getText().toString();
                sPassword = edit_password.getText().toString();

                //verifico que esten completos los campos
                if (!sEmail.isEmpty() && !sPassword.isEmpty()){

                    loginUser();

                }else{
                    Toast.makeText(getApplicationContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //FUNCION PARA LOGEAR USUARIO (INICIO DE SESION)
    private void loginUser(){
        mAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    startActivity(new Intent(getApplicationContext(), HomeActivity.class)); // voy a activity HomeActivity
                    finish(); //finalizar el proceso, para que no pueda volver atrás con la flecha el usuario

                }else {
                    Toast.makeText(getApplicationContext(), "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //FUNCION PARA VERIFICAR SI EL USUARIO YA ESTABA LOGEADO.
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }
}