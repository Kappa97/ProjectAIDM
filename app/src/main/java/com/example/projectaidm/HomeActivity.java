package com.example.projectaidm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.SocketImpl;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    Button logOutButton, valutaButton, schimbaButton, calculeazaButton, rssButton, cameraButton;
    DatabaseReference databaseReference, databaseReferenceTranzactie, databaseReferenceUpdate;
    EditText editText1, editText2;
    ValueEventListener listener;
    ArrayAdapter<String> adapterFromHome;
    ArrayList<String> spinnerDataList;
    Spinner spinner1, spinner2;
    String spinner1GetValue = "", spinner2GetValue = "", editText1GetValue = "", editText2GetValue = "";
    TextView welcomeTextView;
    double sm1, sm2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        editText1 = (EditText) findViewById(R.id.sumaIntrodusaEditText);
        editText2 = (EditText) findViewById(R.id.sumaCalculataEditText);
        welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        //databaseReference = FirebaseDatabase.getInstance().getReference().child("Cont");
        spinnerDataList = new ArrayList<>();
        adapterFromHome = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        spinner1.setAdapter(adapterFromHome);
        spinner2.setAdapter(adapterFromHome);
        if (getIntent().hasExtra("username")) {
            String username = getIntent().getExtras().getString("username");
            welcomeTextView.setText("Welcome " + username + " !");
        }
        retrieveData();


        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //intruce in baza de date
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        Tranzactie tranzactie = new Tranzactie();
                        tranzactie.setValuta1(spinner1GetValue);
                        tranzactie.setValuta2(spinner2GetValue);
                        tranzactie.setSuma1(Double.parseDouble(editText1GetValue));

                        tranzactie.setSuma2(Double.parseDouble(editText2.getText().toString()));
                        tranzactie.setData(formatter.format(date));
                        databaseReferenceTranzactie = FirebaseDatabase.getInstance().getReference().child("Tranzactie");
                        databaseReferenceTranzactie.push().setValue(tranzactie);
                        sm1 = Double.parseDouble(editText1GetValue);
                        sm2 = tranzactie.getSuma2();
                        updateDBAfterExchange(spinner1GetValue, spinner2GetValue, sm1, sm2);
                        editText1.setText("");
                        editText2.setText("");
                        Toast.makeText(HomeActivity.this, "!!! Schimbul s-a efectuat cu success !!!", Toast.LENGTH_LONG).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        spinner1GetValue = "";
                        editText2.setText("");
                        Toast.makeText(HomeActivity.this, "!!! Schimbul a esuat !!!", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        logOutButton = (Button) findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intentToMain = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intentToMain);
            }
        });

        valutaButton = (Button) findViewById(R.id.valutaButton);
        valutaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToValuta = new Intent(HomeActivity.this, ValutaActivity.class);
                intentToValuta.putExtra("welcomeUsername", welcomeTextView.getText().toString());
                startActivity(intentToValuta);
            }
        });

        rssButton = (Button) findViewById(R.id.rssButton);
        rssButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToRss = new Intent(HomeActivity.this, RssActivity.class);
                startActivity(intentToRss);
            }
        });

        cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToCamera = new Intent(HomeActivity.this, CameraActivity.class);
                startActivity(intentToCamera);
            }
        });

        calculeazaButton = (Button) findViewById(R.id.calculeazaButton);
        calculeazaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText1.getText().toString().isEmpty()) {
                    spinner1GetValue = spinner1.getSelectedItem().toString();
                    spinner2GetValue = spinner2.getSelectedItem().toString();
                    editText1GetValue = editText1.getText().toString();
                    editText2GetValue = editText2.getText().toString();
                    getConvertedsum(spinner1GetValue, spinner2GetValue);
                } else {
                    Toast.makeText(HomeActivity.this, "Introduceti suma pe care doriti sa o schimbati", Toast.LENGTH_LONG).show();
                }

            }
        });

        schimbaButton = (Button) findViewById(R.id.schimbaButton);
        schimbaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spinner1GetValue.isEmpty() || !spinner2GetValue.isEmpty() || !editText1GetValue.isEmpty() || !editText2GetValue.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();


                } else {
                    Toast.makeText(HomeActivity.this, "!!!Calculeaza mai intai suma !!!", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    public void retrieveData() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cont");
        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    String valuta = item.child("valuta").getValue(String.class);
                    //spinnerDataList.add(item.getValue().toString());
                    spinnerDataList.add(valuta);
                }
                adapterFromHome.notifyDataSetChanged();
                databaseReference.removeEventListener(listener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getConvertedsum(final String valutaFromSpinner1, final String valutaFromSpiner2) {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cont");
        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item1 : dataSnapshot.getChildren()) {
                    String valuta1 = item1.child("valuta").getValue(String.class);
                    double coefv = item1.child("coefv").getValue(Double.class);
                    double suma1 = item1.child("suma").getValue(Double.class);
                    if (valuta1.equals(valutaFromSpinner1)) {
                        for (DataSnapshot item2 : dataSnapshot.getChildren()) {
                            String valuta2 = item2.child("valuta").getValue(String.class);
                            double coefc = item2.child("coefc").getValue(Double.class);
                            double suma2 = item2.child("suma").getValue(Double.class);

                            if (valuta2.equals(valutaFromSpiner2) && !valuta2.equals(valuta1)) {
                                double ed1 = Double.valueOf(editText1.getText().toString());
                                if (suma1 > ed1) {
                                    double ed2 = Math.round((ed1 * coefv / coefc * 100.0)) / 100.0;
                                    editText2.setText(String.valueOf(ed2));
                                    databaseReference.removeEventListener(listener);
                                    break;
                                } else {
                                    Toast.makeText(HomeActivity.this, "!!! Inuficienti bani (" + suma1 + ") !!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                            } else if (valuta2.equals(valutaFromSpiner2) && valuta2.equals(valuta1)) {
                                Toast.makeText(HomeActivity.this, "!!! Trebuie selectate 2 valute diferite !!!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void updateDBAfterExchange(final String valuta1, final String valuat2, final double suma1, final double suma2) {
        databaseReferenceUpdate = FirebaseDatabase.getInstance().getReference().child("Cont");
        listener = databaseReferenceUpdate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numberOfIteration = 0;
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    String valutaFromDB = item.child("valuta").getValue(String.class);
                    double sumaFromDB = item.child("suma").getValue(Double.class);
                    if (dataSnapshot.getChildrenCount() == numberOfIteration) {
                        break;
                    }
                    if (valutaFromDB.equals(valuta1)) {
                        String keyOfItem = item.getKey();
                        System.out.println(keyOfItem);
                        System.out.println(sumaFromDB);
                        double finalSuma = sumaFromDB - suma1;
                        databaseReferenceUpdate.child(keyOfItem).child("suma").setValue(finalSuma);
                        databaseReferenceUpdate.removeEventListener(listener);
                    }
                    if (valutaFromDB.equals(valuat2)) {
                        String keyOfItem = item.getKey();
                        System.out.println(keyOfItem);
                        System.out.println(sumaFromDB);
                        double finalSuma = sumaFromDB + suma2;
                        databaseReferenceUpdate.child(keyOfItem).child("suma").setValue(finalSuma);
                        databaseReferenceUpdate.removeEventListener(listener);
                    }

                    numberOfIteration++;
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
