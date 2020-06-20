package com.example.projectaidm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.strictmode.CredentialProtectedWhileLockedViolation;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ValutaActivity extends AppCompatActivity {

    EditText valutaEditText, sumaEditText, coefcEditText, coefvEditText;
    TextView welcomeValutaEditText;
    Button createButton, readButton, updateButton, deleteButton, clearFieldsButton, backToHomeButton;
    Conturi cont;
    DatabaseReference databaseReference;
    ValueEventListener listener;
    ArrayAdapter<String> adapter, adapterForHome;
    ArrayList<String> spinnerDataList;
    Spinner spinner;


    long maxid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valuta);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        valutaEditText = findViewById(R.id.valutaEditText);
        sumaEditText = findViewById(R.id.sumaEditText);
        coefcEditText = findViewById(R.id.coefcEditText);
        coefvEditText = findViewById(R.id.coefvEditText);
        spinner = (Spinner) findViewById(R.id.readValutaSpinner);
        welcomeValutaEditText = (TextView) findViewById(R.id.welcomeValutaTextView);
        cont = new Conturi();
        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(ValutaActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        spinner.setAdapter(adapter);
        retrieveData();
        if (getIntent().hasExtra("welcomeUsername")) {
            welcomeValutaEditText.setText(getIntent().getExtras().getString("welcomeUsername"));
        }
        createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valuta = valutaEditText.getText().toString().trim();
                double suma = Double.parseDouble(sumaEditText.getText().toString().trim());
                double coefc = Double.parseDouble(coefcEditText.getText().toString().trim());
                double coefv = Double.parseDouble(coefvEditText.getText().toString().trim());
                cont.setValuta(valuta);
                cont.setSuma(suma);
                cont.setCoefc(coefc);
                cont.setCoefv(coefv);
                databaseReference.push().setValue(cont);
                spinnerDataList.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(ValutaActivity.this, "data inserted succesfully", Toast.LENGTH_LONG).show();
            }
        });
        readButton = (Button) findViewById(R.id.readButton);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valuta = spinner.getSelectedItem().toString();
                readData(valuta);
            }
        });
        updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valutaFromEditText = valutaEditText.getText().toString();
                String valutaFromSpinner = spinner.getSelectedItem().toString();
                if (valutaFromEditText.equals(valutaFromSpinner)) {
                    updateDataDB(valutaFromSpinner);
                    spinnerDataList.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ValutaActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ValutaActivity.this, "Please Press READ!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valutaFromEditText = valutaEditText.getText().toString();
                String valutaFromSpinner = spinner.getSelectedItem().toString();
                if (valutaFromEditText.equals(valutaFromSpinner)) {
                    deleteDataDB(valutaFromSpinner);
                    spinnerDataList.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ValutaActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ValutaActivity.this, "Please Press READ!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearFieldsButton = (Button) findViewById(R.id.clearFieldsButton);
        clearFieldsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valutaEditText.setText("");
                sumaEditText.setText("");
                coefcEditText.setText("");
                coefvEditText.setText("");
            }
        });
        backToHomeButton = (Button) findViewById(R.id.backToHomeButton);
        backToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToHome = new Intent(ValutaActivity.this, HomeActivity.class);
                startActivity(intentToHome);

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
                    System.out.println(valuta);
                    spinnerDataList.add(valuta);
                    //spinnerDataList.add(item.getValue().toString());

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readData(final String valutaFromSpinner) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cont");
        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    String valuta = item.child("valuta").getValue(String.class);
                    if (valuta == valutaFromSpinner) {
                        valutaEditText.setText(valuta);
                        double suma = item.child("suma").getValue(Double.class);
                        sumaEditText.setText(String.valueOf(suma));
                        double coefc = item.child("coefc").getValue(Double.class);
                        coefcEditText.setText(String.valueOf(coefc));
                        double coefv = item.child("coefv").getValue(Double.class);
                        coefvEditText.setText(String.valueOf(coefv));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateDataDB(final String valutaFromSpinner) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cont");
        listener = databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    String valutaFromDB = item.child("valuta").getValue(String.class);
                    if (valutaFromDB.equals(valutaFromSpinner)) {
                        String keyOfItem = item.getKey();
                        String valuta = valutaEditText.getText().toString().trim();
                        double suma = Double.parseDouble(sumaEditText.getText().toString().trim());
                        double coefc = Double.parseDouble(coefcEditText.getText().toString().trim());
                        double coefv = Double.parseDouble(coefvEditText.getText().toString().trim());
                        databaseReference.child(keyOfItem).child("suma").setValue(suma);
                        databaseReference.child(keyOfItem).child("coefc").setValue(coefc);
                        databaseReference.child(keyOfItem).child("coefv").setValue(coefv);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void deleteDataDB(final String valutaFromSpinner) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cont");
        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    String valutaFromDB = item.child("valuta").getValue(String.class);
                    if (valutaFromDB.equals(valutaFromSpinner)) {
                        String keyOfItem = item.getKey();
                        databaseReference.child(keyOfItem).removeValue();
                        valutaEditText.setText("");
                        sumaEditText.setText("");
                        coefcEditText.setText("");
                        coefvEditText.setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
