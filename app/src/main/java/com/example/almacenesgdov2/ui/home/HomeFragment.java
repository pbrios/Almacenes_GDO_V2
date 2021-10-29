package com.example.almacenesgdov2.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.almacenesgdov2.R;
import com.example.almacenesgdov2.fragments.BusquedaFragment;
import com.example.almacenesgdov2.fragments.MovimientosFragment;
import com.example.almacenesgdov2.objetos.Almacenes;
import com.example.almacenesgdov2.objetos.Persona;
import com.example.almacenesgdov2.objetos.Productos;
import com.example.almacenesgdov2.recursos.VolleySingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private Productos productos;
    private Productos[] productosList;
    private TextView txtCodigo, txtDescripcion, txtUnidad;
    private Persona persona;
    private ArrayList<Almacenes> almacenes;
    private Spinner spAlmacenO, spAlmacenD;
    private FloatingActionButton btnListaT;
    private Gson gson = new Gson();
    private List<Productos> productosArrayList;
    private SimpleDateFormat dateFormat;
    private Date date = new Date();
    private MovimientosFragment revisionMovimiento = new MovimientosFragment();
    private String idAlmacenO, idAlmacenD, servidor, fecha;
    private EditText edtCantidad;
    private ImageButton btnGuardar;


    private void inicializa(View root){
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        fecha = dateFormat.format(date);
        btnGuardar = root.findViewById(R.id.btnGuardarT);
        txtCodigo = root.findViewById(R.id.txvCodigoT);
        txtDescripcion = root.findViewById(R.id.txvDescripconT);
        edtCantidad = root.findViewById(R.id.edtCantidadT);
        txtUnidad = root.findViewById(R.id.txvUnidadT);
        spAlmacenO = root.findViewById(R.id.spAlmacenOT);
        spAlmacenD = root.findViewById(R.id.spAlmacenDT);
        btnListaT = root.findViewById(R.id.btnListaMovimientosT);
        almacenes = new ArrayList<Almacenes>();
        persona = (Persona) getActivity().getIntent().getExtras().getSerializable("usuario");
        almacenes = (ArrayList<Almacenes>) getActivity().getIntent().getExtras().getSerializable("almacenes");
        populateSpinner();
        FragmentManager fragmentManager = getChildFragmentManager();
        BusquedaFragment datosProducto = new BusquedaFragment();
        fragmentManager.beginTransaction().replace(R.id.lBusqueda, datosProducto).commit();
        servidor = getString(R.string.servername);
    }

    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();
        for(Almacenes a:almacenes)
            lables.add(a.getId()+" "+a.getAlmacen());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, lables);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlmacenO.setAdapter(spinnerAdapter);
        spAlmacenD.setAdapter(spinnerAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getChildFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                productos = (Productos) bundle.getSerializable("bundleKey");
                cargaDatos(productos);
            }
        });
    }

    private void cargaDatos(Productos producto){
        txtCodigo.setText(producto.getCodigo());
        txtDescripcion.setText(producto.getDescripcion());
        txtUnidad.setText(producto.getUnidades());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        inicializa(root);
        spAlmacenO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idAlmacenO = almacenes.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        spAlmacenD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idAlmacenD = almacenes.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        btnListaT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!idAlmacenO.equals("7") && !idAlmacenD.equals("7") && !idAlmacenO.equals(idAlmacenD))
                    buscarProducto(persona.getIdUsuario(), idAlmacenO, idAlmacenD, fecha);
                else
                    Toast.makeText(getContext(), "Revise los almacenes seleccionados", Toast.LENGTH_LONG).show();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                btnGuardar.setEnabled(false);
                if(!idAlmacenO.equals("7") && !idAlmacenD.equals("7") && !idAlmacenO.equals(idAlmacenD) && !edtCantidad.getText().toString().isEmpty() && !txtCodigo.getText().toString().isEmpty())
                    ejecutarServicio();
                else{
                    Toast.makeText(getContext(), "Favor de revisar los parametros", Toast.LENGTH_LONG).show();
                    btnGuardar.setEnabled(true);
                }
            }
        });

        return root;
    }

    private void buscarProducto(String idUsuario, String idAlmacen, String idAlmacenD, String fecha){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(servidor+"lista_traspasoUsuario.php?usuario="+idUsuario+"&almacen="+idAlmacen+"&almacenD="+idAlmacenD+"&fecha="+fecha, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                productosList = null;
                productosList = gson.fromJson(response.toString(), Productos[].class);
                productosArrayList = Arrays.asList(productosList);
                revisionMovimiento.setProductosArrayList(productosArrayList);
                revisionMovimiento.show(getChildFragmentManager(), "desde lista");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Sin movimientos", Toast.LENGTH_LONG).show();
            }
        }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void ejecutarServicio(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        Date date = new Date();
        final String fechaI = dateFormat.format(date);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, servidor+"insertar_traspaso.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                limpiaCampos();
                Toast.makeText(getContext(), "Conteo Agregado", Toast.LENGTH_LONG).show();
                btnGuardar.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                btnGuardar.setEnabled(true);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", persona.getIdUsuario());
                parametros.put("almacenO", idAlmacenO);
                parametros.put("codigo", txtCodigo.getText().toString());
                parametros.put("cantidad", edtCantidad.getText().toString());
                parametros.put("almacenD", idAlmacenD);
                parametros.put("created_at", fechaI);
                return parametros;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }

    private void limpiaCampos(){
        txtCodigo.setText("");
        edtCantidad.setText("");
        txtDescripcion.setText("");
        txtUnidad.setText("");
    }
}