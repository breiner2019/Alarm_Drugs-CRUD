package com.example.alarm_drugs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.alarm_drugs.Clases.Drugs;

public class add_alarm extends AppCompatActivity {
    private NumberPicker hournumberpicker;
    private NumberPicker minutenumberpicker;
    private EditText editTextName;
    private EditText editTextDosis;
    private EditText editTextCantidad;
    private EditText editTextMotivo;
    private RadioGroup radioGroup;
    int update = 0;
    int position = 0;
    String x;
    AlarmDrugs Alarm;
    Drugs drug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        Intent xt = getIntent();
        Bundle bundle=xt.getBundleExtra("xx");

        editTextName = findViewById(R.id.edittextName);
        editTextDosis = findViewById(R.id.editTextDosis);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        editTextMotivo = findViewById(R.id.editTextMotivo);
        radioGroup = findViewById(R.id.RadioGroupDrug);
        hournumberpicker = findViewById(R.id.numberpciker_hour);
        String Tipo;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_button_jarabe:
                        // Opción 1 seleccionada
                        x="jarabe";
                        break;
                    case R.id.radio_button_crema:
                        // Opción 2 seleccionada
                        x="GEL";
                        break;
                    case R.id.radio_button_injection:
                        // Opción 3 seleccionada
                        x="inyeccion";
                        break;
                    case R.id.radio_button_gotas:
                        // Opción 3 seleccionada
                        x="gotas";
                        break;
                    case R.id.radio_button_pill:
                        // Opción 3 seleccionada
                        x="pastillas";
                        break;
                    case R.id.radio_button_otros:
                        // Opción 3 seleccionada
                        x="otras";
                        break;
                }
            }
        });
        if (bundle!=null)
            Alarm =(AlarmDrugs)bundle.getSerializable("grasa");
            drug=Alarm.getDrug();
        if(Alarm!=null) {

            editTextName.setText(Alarm.getDrug().getNombre());
            editTextDosis.setText(Alarm.getDrug().getCantidad());
            editTextCantidad.setText(String.valueOf(Alarm.getDrug().getTotal()));
            editTextMotivo.setText(Alarm.getDrug().getInfo());
            hournumberpicker.setValue(Alarm.getDrug().getTotal());
            switch (Alarm.getDrug().getTipo()) {
                case "inyeccion":
                    radioGroup.setId(R.id.radio_button_injection);
                    break;
                case "GEL":
                    radioGroup.setId( R.id.radio_button_crema);
                    break;
                case "pastillas":
                    radioGroup.setId(R.id.radio_button_pill);
                    break;
                case "jarabe":
                    // Opción 1 seleccionada
                    radioGroup.setId(R.id.radio_button_jarabe);
                    break;
                case "gotas":
                    radioGroup.setId(R.id.radio_button_gotas);
                    break;
                case "otras":
                    radioGroup.setId(R.id.radio_button_otros);
                    break;
            }

            update=1;
            position=xt.getIntExtra("position",0);
        }



        hournumberpicker.setMaxValue(24);
        hournumberpicker.setMinValue(0);




    }
    public void savedata(){

        //setResult();
        //finish();
    }

    public void Guardar(View view) {
        int id = (int) System.currentTimeMillis();
        Drugs Drug = new Drugs();
        AlarmDrugs AlarmDrug = new AlarmDrugs();
        if(update==1){

            Drug = drug;
            Drug.setNombre(editTextName.getText().toString());

            Drug.setTipo(x);
            Drug.setCantidad(editTextDosis.getText().toString());
            Drug.setTiempo(hournumberpicker.getValue());
            Drug.setTotal(Integer.parseInt(editTextCantidad.getText().toString()));
            Drug.setInfo(editTextMotivo.getText().toString());
            Drug.setCodigo(Alarm.getDrug().getCodigo());
            Drug.setContraindicacciones(Alarm.getDrug().getContraindicacciones());
            Drug.setId_receta(Alarm.getDrug().getId_receta());



            AlarmDrug = Alarm;
            AlarmDrug.setDrug(Drug);
            AlarmDrug.setCantidad(Drug.getTotal());
            AlarmDrug.setTime(Drug.getTiempo()* 3600000L);
            AlarmDrug.setStarted(false);
        }else {
            Drug.setId(id);
            Drug.setNombre(editTextName.getText().toString());

            Drug.setTipo(x);
            Drug.setCantidad(editTextDosis.getText().toString());
            Drug.setTiempo(hournumberpicker.getValue());
            Drug.setTotal(Integer.parseInt(editTextCantidad.getText().toString()));
            Drug.setInfo(editTextMotivo.getText().toString());
            Drug.setCodigo("xxx");
            Drug.setContraindicacciones("xxx");
            Drug.setId_receta(id);



            AlarmDrug.setAlarmDrugsId(id);
            AlarmDrug.setDrug(Drug);
            AlarmDrug.setCantidad(Drug.getTotal());
            AlarmDrug.setTime(Drug.getTiempo()* 3600000L);
            AlarmDrug.setStarted(false);
        }




        Intent intent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putSerializable("grasa",AlarmDrug);
        Bundle bundlex=new Bundle();
        bundlex.putSerializable("grasax",Drug);
        intent.putExtra("xx",bundle);
        intent.putExtra("xxx",bundlex);
        intent.putExtra("update",update);
        intent.putExtra("position",position);

        setResult(MainActivity.RESULT_OK, intent);
        finish();
    }
}