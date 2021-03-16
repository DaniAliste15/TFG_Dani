package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetContraActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button buttonResCon;

    private String email="";
    private FirebaseAuth Auth;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_contra);

        editTextEmail = (EditText) findViewById(R.id.textEmail);
        buttonResCon = (Button)findViewById(R.id.btnResContraseña);

        Auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        buttonResCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = editTextEmail.getText().toString();

                if(!email.isEmpty()){
                    dialog.setTitle("Restableciendo");
                    dialog.setMessage("Espere...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    resetPassword();
                }
                else {
                    //Toast.makeText(ResetContraActivity.this, "Debe ingresar un Email", Toast.LENGTH_SHORT).show();

                    editTextEmail.setError("Debe ingresar un email");
                }
                          }
        });

    }

    private void resetPassword() {

        Auth.setLanguageCode("es");
        Auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {

                    Toast.makeText(ResetContraActivity.this, "Se ha enviado el correo para restablecer" +
                            "la contraseña", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ResetContraActivity.this, "No se pudo enviar el correo", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }
}
