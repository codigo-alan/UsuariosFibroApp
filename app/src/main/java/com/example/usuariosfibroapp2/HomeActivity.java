package com.example.usuariosfibroapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btn_signout, btn_actualizar, btn_visualizar;
    private TextView txt_mail, txt_elemento;
    private EditText edit_precio;
    Spinner sp_conectores;
    FirebaseFirestore mRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //RELACIONAR LAYOUT CON BACKEND
        sp_conectores = (Spinner) findViewById(R.id.sp_con);
        btn_signout = (Button) findViewById(R.id.btn_signout);
        btn_actualizar = (Button) findViewById(R.id.btn_actualizar);
        btn_visualizar = (Button) findViewById(R.id.btn_visualizar);

        edit_precio = (EditText) findViewById(R.id.edit_precio);
        txt_mail = (TextView) findViewById(R.id.txt_mail);
        txt_elemento = (TextView) findViewById(R.id.txt_elemento);

        //PROGRAMA
        mAuth = FirebaseAuth.getInstance(); //obtengo instancia de mi autenticacion de firebase
        mRoot = FirebaseFirestore.getInstance(); //obtengo la referencia de mi cloud firestore

        //INFLO SPINNER CON LISTA
        String [] opc_seleccion = {"FC UPC MM", "SC UPC MM", "LC UPC MM", "ST UPC MM",
                "FC UPC SM", "SC UPC SM", "LC UPC SM", "ST UPC SM", "FC APC SM", "SC APC SM", "LC APC SM", "E2000 UPC SM", "E2000 APC SM",
                "PVC 3mm SM", "PVC 3mm MM OM1", "PVC 3mm MM OM2", "PVC 3mm MM OM3"}; //lista de una dimensión para mostrar opciones
        ArrayAdapter<String> adapterSel = new ArrayAdapter<String>(this, R.layout.spinner_conectores, opc_seleccion); //Array para  luego agregar lista de opciones en el spinner
        sp_conectores.setAdapter(adapterSel);//coloco lista opciones en spinner

        //CUANDO SELECCIONO ALGO NUEVO EN EL SPINNER
        sp_conectores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                edit_precio.setText("");
                txt_elemento.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //REGISTRO NUEVO DE DATOS Y ACTUALIZACION
        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_elemento.setText("");
                String sConector = sp_conectores.getSelectedItem().toString();
                String sPrecio = edit_precio.getText().toString();

                Map<String, Object> datosConectores = new HashMap<>();
                datosConectores.put("precio", sPrecio);

                mRoot.collection("conectores").document(sConector).set(datosConectores).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { //Si se actualiza correctamente
                        Toast.makeText(getApplicationContext(), "Se actualizo la base de datos", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { //Si falla la actualizacion por algun motivo
                        Toast.makeText(getApplicationContext(), "No se pudo actualizar revise su conexión", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        //ESTO OCURRIRA AL HACER CLICK EN VER REGISTROS
        btn_visualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerRegistro(); //voy a la funcion obtenerRegistro.
            }
        });


        //ESTO OCURRIRA AL HACER CLICK EN CERRAR SESIÓN
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
                //mAuth.signOut(); //Cierro sesión del usuario
                //startActivity(new Intent(getApplicationContext(), authActivity.class)); //voy a la activity authActivity
                //finish(); //finalizo el proceso, o algo asi
            }
        });

    }

    //FUNCION PARA CAMBIAR EL TITULO QUE SE VE EN LA ACTIVITY (LA REALIZA CUANDO SE EJECUTA)
    @Override
    protected void onStart() {
        super.onStart();
        setTitle("Actualización de registros");
        String sEmail = mAuth.getCurrentUser().getEmail();
        txt_mail.setText(sEmail);
    }


    //FUNCION PARA OBTENER UN DATO SELECCIONADO DESDE EL SPINNER
    private void obtenerRegistro() {
        String sElemento = sp_conectores.getSelectedItem().toString();
        mRoot.collection("conectores").document(sElemento).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String sPrecio = documentSnapshot.getString("precio");
                    txt_elemento.setText("Artículo seleccionado: "+sElemento + "\n" +
                            "Precio actual en Base de datos: "+sPrecio);
                }
            }
        });
    }

    //Esta función crea el cuadro de dialogo y llama al objeto listener (del tipo DListener) para manejar las acciones
    //del botón que el usuario selecione dentro del cuadro de diálogo.
    protected Dialog onCreateDialog(int id){
        Dialog dialog = null; //creo el cuadro de dialogo
        DListener listener = new DListener(); //creo nueva variable del tipo DListener
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //creo nueva variable del tipo Alert...
        builder = builder.setMessage("Su sesión se cerrará ¿Continuar?"); //a la variable creada del tipo Alert...le coloco mensaje
        builder.setPositiveButton("Si", listener); //a la variable también le seteo el botón positivo (texto, llamada a la clase)
        builder.setNegativeButton("No", listener);//lo mismo pero para el botón negativo
        dialog = builder.create();//creo o relleno el cuadro de dialogo a partir de la variable del tipo AlertDialog.Builder
        return dialog;
    }

    //Esta clase maneja las acciones para cada boton que el usuario selecciona del cuadro de Dialogo
    class DListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which){
            //en este if manejo lo que hará la app si se selecciona el boton positivo, cerrará sesión e irá a la activity
            //de autenticación de usuario
            if (which == DialogInterface.BUTTON_POSITIVE){
                mAuth.signOut(); //Cierro sesión del usuario
                Toast.makeText(getApplicationContext(),"Cerrando sesión... ",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), authActivity.class)); //voy a la activity authActivity
                finish(); //finalizo el proceso, o algo asi
            }

            //en este if manejo el caso del boton negativo, simplemente cierra el cuadro de dialogo
            if (which == DialogInterface.BUTTON_NEGATIVE){
                dialog.dismiss();
            }
        }
    }

}