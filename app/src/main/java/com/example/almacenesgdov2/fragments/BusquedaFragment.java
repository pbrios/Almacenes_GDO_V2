package com.example.almacenesgdov2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.almacenesgdov2.R;
import com.example.almacenesgdov2.objetos.Productos;
import com.example.almacenesgdov2.recursos.VolleySingleton;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;

public class BusquedaFragment extends Fragment implements View.OnClickListener{
    private Productos productos[];
    private EditText edtCodigo;
    private Gson gson = new Gson();
    private String servidor;
    private ImageButton btnCamara, btnBusqueda;
    private CheckBox chTipoBusqueda;
    private ProductosData productosData = new ProductosData();
    private Productos producto;

    private void inicializa(View v){
        servidor = getString(R.string.servername);
        edtCodigo = v.findViewById(R.id.edtCodigo);
        btnBusqueda = v.findViewById(R.id.btnBusqueda);
        btnCamara = v.findViewById(R.id.btnCamara);
        chTipoBusqueda = v.findViewById(R.id.chTipoBusqueda);
        btnBusqueda.setOnClickListener(this);
        btnCamara.setOnClickListener(this);
    }

    private void buscarProducto(String codigo){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(servidor+"buscar_producto.php?codigo="+codigo, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                productos = null;
                productos = gson.fromJson(response.toString(), Productos[].class);
                cargaCampos(productos[0]);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Codigo no registrado "+edtCodigo.getText(), Toast.LENGTH_LONG).show();
                limpiaCampos();
            }
        });
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void limpiaCampos(){
        edtCodigo.setText("");
        edtCodigo.requestFocus();
    }

    private void cargaCampos(Productos producto){
        limpiaCampos();
        Bundle result = new Bundle();
        result.putSerializable("bundleKey",producto);
        getParentFragmentManager().setFragmentResult("requestKey", result);
    }

    private void realizarBusqueda(){
        if(!edtCodigo.getText().toString().isEmpty())
            if(!chTipoBusqueda.isChecked())
                buscarProducto(edtCodigo.getText().toString());
            else
                busquedaTexto(edtCodigo.getText().toString());
        else
            Toast.makeText(getContext(), "Favor de indicar el producto a buscar", Toast.LENGTH_LONG).show();
    }

    private void busquedaTexto(String codigo){
        productosData.setBusqueda(codigo);
        productosData.show(getChildFragmentManager(), "sdasda");
    }

    public void escanear(){
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(BusquedaFragment.this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Escanear Codigo");
        intentIntegrator.setCameraId(0);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult != null){
            if(intentResult.getContents() == null){
                Toast.makeText(getContext(), "Cancelaste Proceso", Toast.LENGTH_LONG).show();
            }else{
                buscarProducto(intentResult.getContents());
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_busqueda, container, false);

        inicializa(root);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getChildFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                producto= (Productos) bundle.getSerializable("bundleKey2");
                cargaCampos(producto);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBusqueda:
                realizarBusqueda();
                break;
            case R.id.btnCamara:
                escanear();
                break;
            default:
                break;
        }
    }
}