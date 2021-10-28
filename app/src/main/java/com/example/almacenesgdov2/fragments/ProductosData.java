package com.example.almacenesgdov2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.almacenesgdov2.R;
import com.example.almacenesgdov2.objetos.Productos;
import com.example.almacenesgdov2.recursos.ProductosAdapter;
import com.example.almacenesgdov2.recursos.VolleySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

public class ProductosData extends BottomSheetDialogFragment implements SearchView.OnQueryTextListener{
    private String servidor, busqueda;
    private Productos[] productos;
    private List<Productos> productosArrayList;
    private Gson gson = new Gson();
    private ProductosAdapter productosAdapter;
    private RecyclerView recyclerViewProductos;
    private SearchView svFilter;
    private Productos producto = null;

    public void setBusqueda(String busqueda){
        this.busqueda = busqueda;
    }

    private String getBusqueda(){
        return busqueda;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_productos_data, container, false);
        servidor = getString(R.string.servername);
        inicializa(v);
        buscarProducto(getBusqueda());
        return v;
    }

    private  void inicializa(View root){
        recyclerViewProductos = root.findViewById(R.id.rcvProductos);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(getContext()));
        servidor = getString(R.string.servername);
        svFilter = root.findViewById(R.id.svFiltroProducto);
        svFilter.setOnQueryTextListener(this);
    }

    private void buscarProducto(String descripcion){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(servidor+"buscarProductoLike2.php?busqueda="+descripcion, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                productos = null;
                productos = gson.fromJson(response.toString(), Productos[].class);
                productosArrayList = Arrays.asList(productos);
                populateRecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No hay registros para esos parametros", Toast.LENGTH_LONG).show();

            }
        }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void populateRecyclerView() {
        productosAdapter = new ProductosAdapter(productosArrayList, getContext(), recyclerViewProductos);
        productosAdapter.setOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                producto = productosAdapter.codigos(view);
                Bundle result = new Bundle();
                result.putSerializable("bundleKey2",producto);
                getParentFragmentManager().setFragmentResult("requestKey", result);
                dismiss();
            }
        });
        recyclerViewProductos.setAdapter(productosAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        productosAdapter.getFilter().filter(s);
        return false;
    }
}
