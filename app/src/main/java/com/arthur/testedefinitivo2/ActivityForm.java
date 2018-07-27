package com.arthur.testedefinitivo2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arthur.testedefinitivo2.funcs.SharedPrefManager;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ActivityForm extends AppCompatActivity {

    private EditText aplicacaoinfo;
    private TextView investimento;
    private EditText percentual;
    private Button simular;
    private String aplicacao;

    Calendar calendar;
    public static String dataMostrar;
    public static String dataBanco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);



        aplicacaoinfo=(EditText) findViewById(R.id.aplicacao_Id);
        investimento = (TextView) findViewById(R.id.investimento_Id);
        percentual = (EditText) findViewById(R.id.percentual_Id);
        simular = (Button) findViewById(R.id.SimularBtn);





        final Intent intent = new Intent(ActivityForm.this, ResultadoActivity.class);



        //PEGAR DATA ATUAL
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        dataMostrar = sdf2.format(date);

        //COLOCAR DATA ATUAL NO EDITTEXT
        investimento.setText(dataMostrar.toString());

        //EVENTO ONCLICK CHAMAR O CALENDARIO
        calendar = Calendar.getInstance();
        investimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityForm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat dateParser = new SimpleDateFormat("MM-dd-yyyy");
                        NumberFormat f = new DecimalFormat("00");
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        //String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
                        //String date = dateParser.format(calendar.getTime().toString());
                        dataMostrar = String.valueOf(f.format(dayOfMonth)) + "/"
                                + String.valueOf(f.format(monthOfYear+1)) + "/" + String.valueOf(year);
                        dataBanco = String.valueOf(year) + "-"
                                + String.valueOf(f.format(monthOfYear+1)) + "-" + String.valueOf(f.format(dayOfMonth));
                        investimento.setText(dataMostrar);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });



        SimpleMaskFormatter simpleMaskAplicacao = new SimpleMaskFormatter("NNNN.NNN");
        SimpleMaskFormatter simpleMaskPercentual = new SimpleMaskFormatter( "NNN" );

        MaskTextWatcher maskAplicacao = new MaskTextWatcher(aplicacaoinfo, simpleMaskAplicacao);
        MaskTextWatcher maskPercentual = new MaskTextWatcher(percentual, simpleMaskPercentual);


        aplicacaoinfo.addTextChangedListener(maskAplicacao);
        percentual.addTextChangedListener( maskPercentual );

        simular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                //GRAVANDO VALORES
                SharedPrefManager.getInstance(ActivityForm.this).gravarCampo("APLICACAO",aplicacaoinfo.getText().toString());
                SharedPrefManager.getInstance(ActivityForm.this).gravarCampo("INVESTIMENTO",dataBanco);
                SharedPrefManager.getInstance(ActivityForm.this).gravarCampo("PERCENTUAL",percentual.getText().toString());

                startActivity(new Intent(ActivityForm.this,ResultadoActivity.class));


            }

        });
    }
}