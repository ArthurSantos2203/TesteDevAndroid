package com.arthur.testedefinitivo2;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arthur.testedefinitivo2.funcs.SharedPrefManager;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultadoActivity extends AppCompatActivity {

    private ProgressBar pBar;
    private TextView tvValorInvestido, tvRendimentoTotal, tvValorAplicadoInicialmente;
    private TextView tvValorBrutoInvestido, tvValordoRendimento, tvIRInvestimento;
    private TextView tvValorLiquidoInvestimento, tvDataResgate, tvDiasCorridos, tvRendimentoMensal;
    private TextView tvPercentualCDI, tvRentabilidadeAnual, tvRentabilidadePeriodo;
    private LinearLayout lytResultado;
    private Button btnSimularNovamente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        pBar = findViewById(R.id.progressCarregaInformacoes);
        pBar.setVisibility(View.VISIBLE);

        lytResultado = findViewById(R.id.lylResultado);

        tvValorInvestido = findViewById(R.id.tvValorInvestido);

        tvRendimentoTotal = findViewById(R.id.tvRendimentoTotal);
        tvValorAplicadoInicialmente = findViewById(R.id.tvValorAplicadoInicialmente);
        tvValorBrutoInvestido = findViewById(R.id.tvValorBrutoInvestido);
        tvValordoRendimento = findViewById(R.id.tvValorDoRendimento);
        tvIRInvestimento = findViewById(R.id.tvIRInvestimento);
        tvValorLiquidoInvestimento = findViewById(R.id.tvValorLiquidoInvestimento);

        tvDataResgate = findViewById(R.id.tvDataResgate);
        tvDiasCorridos = findViewById(R.id.tvDiasCorridos);
        tvRendimentoMensal = findViewById(R.id.tvRendimentoMensal);
        tvPercentualCDI = findViewById(R.id.tvPercentualCDI);
        tvRentabilidadeAnual = findViewById(R.id.tvRentabilidadeAnual);
        tvRentabilidadePeriodo = findViewById(R.id.tvRentabilidadePeriodo);

        btnSimularNovamente = findViewById(R.id.btnSimularNovamente);
        btnSimularNovamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        //CARREGAR OS DOS VIA API
        carregaInformacoes();

    }

    //CARREGAR INFORMAÇÕES DA API
    private void carregaInformacoes() {


        final String aplicacao = SharedPrefManager.getInstance(ResultadoActivity.this).pegarCampo("APLICACAO");
        final String investimento = SharedPrefManager.getInstance(ResultadoActivity.this).pegarCampo("INVESTIMENTO");
        final String percentual = SharedPrefManager.getInstance(ResultadoActivity.this).pegarCampo("PERCENTUAL");

        if (verificaConexao(this)) {
            Ion.with(this)
                    .load("https://api-simulator-calc.easynvest.com.br/calculator/simulate?investedAmount="+aplicacao+"&index=CDI&rate="+percentual+"&isTaxFree=false&maturityDate="+investimento)
                    .setLogging("passosemergencias", Log.DEBUG)
                    .setTimeout(60 * 60 * 1000)
                    .noCache()
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            pBar.setVisibility(View.GONE);
                            lytResultado.setVisibility(View.VISIBLE);
                            if (result!=null) {

                                    //PEGA PARAMENTOS DE INVESTIMENTOS
                                    JsonObject object = new JsonObject();
                                    object = result.getAsJsonObject("investmentParameter");

                                    //INSERE NO TEXTVIEW DO BLOCO INVESTIMENTO
                                    tvValorInvestido.setText(object.get("investedAmount").toString());
                                    tvValorAplicadoInicialmente.setText("R$ "+object.get("investedAmount"));
                                    tvValorBrutoInvestido.setText("R$ "+object.get("grossAmountProfit"));
                                    tvDataResgate.setText("R$ "+object.get("maturityDate"));
                                    tvDiasCorridos.setText(""+object.get("maturityTotalDays"));


                                    //INSERE OS DEMAIS PARAMENTROS
                                    tvRendimentoTotal.setText(result.get("grossAmount").toString());
                                    tvRentabilidadePeriodo.setText(result.get("rateProfit").toString());
                                    tvRentabilidadeAnual.setText(result.get("annualGrossRateProfit").toString());
                                    tvPercentualCDI.setText(result.get("taxesRate").toString());
                                    tvRendimentoMensal.setText(result.get("monthlyGrossRateProfit").toString());
                                    tvValorLiquidoInvestimento.setText(result.get("netAmount").toString());
                                    tvIRInvestimento.setText(result.get("taxesRate").toString());
                                    tvValordoRendimento.setText(result.get("netAmountProfit").toString());
                                    tvValorBrutoInvestido.setText(result.get("grossAmountProfit").toString());




                            } else {
                                Toast.makeText(ResultadoActivity.this,"Tente novamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this,"SEM INTERNET", Toast.LENGTH_SHORT).show();
            pBar.setVisibility(View.GONE);
        }

    }


    //VERIFICAR SE TEM INTERNET
    public static final boolean verificaConexao(Activity context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }
}
