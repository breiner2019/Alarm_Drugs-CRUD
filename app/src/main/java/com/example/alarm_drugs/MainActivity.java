package com.example.alarm_drugs;


import static com.example.alarm_drugs.app.CHANNEL_ID;
import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alarm_drugs.Clases.Drugs;
import com.example.alarm_drugs.Clases.User;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Escuchador{
    private TextView txtview;
    private ImageButton add_alarm;
    private ImageButton edit_alarm;
    private ImageButton delete_alarm;

    private static final String TAG = "MyActivity";
    private static final int REQUEST_CODE_NUEVA_ACTIVIDAD = 1;
    private AlarmDrugAdapter adapter ;


    AlarmDrugs Alarmxx;
    Drugs Drugxx;
    public static final int LENGTH_SHORT = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtview = findViewById(R.id.AlarmTxtView);
        add_alarm=findViewById(R.id.add_alarm);
        edit_alarm=findViewById(R.id.edit_alarm);
        delete_alarm=findViewById(R.id.delete_alarm);
        adapter = new AlarmDrugAdapter(this);
        add_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,com.example.alarm_drugs.add_alarm.class);
                startActivityForResult(i, REQUEST_CODE_NUEVA_ACTIVIDAD);
            }
        });
        List<Long> times = new ArrayList<Long>();

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbarx);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Alarma app");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });


        RecyclerView recyclerView = findViewById(R.id.recycleview);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        AlarmViewModel Alarmviewmodel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AlarmViewModel.class);;

        Alarmviewmodel.getAllAlarm().observe(this, new Observer<List<AlarmDrugs>>() {
            @Override
            public void onChanged(List<AlarmDrugs> alarmDrugs) {
                adapter.setAlarms(alarmDrugs);
                long LessTime=0;
                String LessTimeDrug="";
                for(int i =0;i<alarmDrugs.size();i++){
                        if(alarmDrugs.get(i).isStarted()){
                            if(LessTime==0){
                                LessTime=alarmDrugs.get(i).getTimestart();
                                LessTimeDrug=alarmDrugs.get(i).getDrug().getNombre();
                            }else{
                                if(LessTime>alarmDrugs.get(i).getTimestart()){
                                    LessTime=alarmDrugs.get(i).getTimestart();
                                    LessTimeDrug=alarmDrugs.get(i).getDrug().getNombre();
                                }
                            }

                        }
                    }

                    if (LessTime!=0){

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a ");
                        String currentDateAndTime = sdf.format(new Date(LessTime));
                        txtview.setText(LessTimeDrug+" es  tu proxima medicina a las "+currentDateAndTime );

                    }else{
                        txtview.setText("No hay alamas activas");
                    }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NUEVA_ACTIVIDAD && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle bundle=data.getBundleExtra("xx");
                Bundle bundlex=data.getBundleExtra("xxx");
                int update = data.getIntExtra("update",0);
                int position = data.getIntExtra("position",0);

                Alarmxx =(AlarmDrugs)bundle.getSerializable("grasa");
                Drugxx =(Drugs)bundlex.getSerializable("grasax");



                // Aquí puedes usar los datos recibidos como desees
                if(update==1){
                    UpdateAlarma(Alarmxx,position);
                }else {
                    InsertDrug(Drugxx);
                    InsertAlarm(Alarmxx);
                }



                Toast.makeText(this, "Nombre: " + Drugxx.getNombre() + ", Edad: " + Alarmxx.getDrug().getNombre(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void UpdateAlarma(AlarmDrugs alarm, int position) {
        AlarmViewModel Alarmviewmodel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AlarmViewModel.class);;
        Alarmviewmodel.update(alarm);
        adapter.notifyItemChanged(position);


    }
    public void updatedrug(Drugs drug){
        AlarmViewModel Alarmviewmodel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AlarmViewModel.class);;

    }

    @Override
    public void SelectedAlarma(AlarmDrugs alarm, int position) {
        delete_alarm.setVisibility(View.VISIBLE);
        edit_alarm.setVisibility(View.VISIBLE);

        delete_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletealarma(alarm,position);
                delete_alarm.setVisibility(View.INVISIBLE);
                edit_alarm.setVisibility(View.INVISIBLE);

            }
        });
        edit_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatealarm(alarm,position);
                delete_alarm.setVisibility(View.INVISIBLE);
                edit_alarm.setVisibility(View.INVISIBLE);
            }
        });


    }
    public void updatealarm(AlarmDrugs alarm, int position){
        Intent ix = new Intent(MainActivity.this,com.example.alarm_drugs.add_alarm.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("grasa",alarm);
        ix.putExtra("xx",bundle);
        ix.putExtra("position",position);
        startActivityForResult(ix, REQUEST_CODE_NUEVA_ACTIVIDAD);
    }


    public void deletealarma(AlarmDrugs alarm, int position) {

        AlarmViewModel Alarmviewmodel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AlarmViewModel.class);;
        Alarmviewmodel.delete(alarm);
        adapter.notifyItemChanged(position);



    }

    public void Deleteall() {
        AlarmViewModel Alarmviewmodel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AlarmViewModel.class);;
        Alarmviewmodel.deleteallalarm();
    }
    public void InsertDrug( Drugs x) {
        AlarmViewModel Alarmviewmodel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AlarmViewModel.class);;
        Alarmviewmodel.insertDrugs(x);
    }
    public void InsertAlarm(AlarmDrugs x) {
        AlarmViewModel Alarmviewmodel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AlarmViewModel.class);;
        Alarmviewmodel.insert(x);
        adapter.notifyDataSetChanged();

    }
}

