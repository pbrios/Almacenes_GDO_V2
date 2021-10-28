package com.example.almacenesgdov2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.almacenesgdov2.R;
import com.example.almacenesgdov2.objetos.Productos;
import com.example.almacenesgdov2.recursos.ProductosListaAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;

public class MovimientosFragment extends BottomSheetDialogFragment implements SearchView.OnQueryTextListener{
    private RecyclerView recyclerViewProductos;
    private ProductosListaAdapter productosAdapter;
    private List<Productos> productosArrayList;
    private SearchView svFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lista_movimientos,container, false);
        inicializa(v);
        return v;
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

    public void setProductosArrayList(List<Productos> productosArrayList){
        this.productosArrayList = productosArrayList;
    }

    private List<Productos> getProductosArrayList(){
        return productosArrayList;
    }

    private void inicializa(View root){
        recyclerViewProductos = root.findViewById(R.id.rcvMovimientos);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(getContext()));
        svFilter = root.findViewById(R.id.svMovimientos);
        svFilter.setOnQueryTextListener(this);
        populateRecyclerView();
    }

    private void populateRecyclerView() {
        productosAdapter = new ProductosListaAdapter(getProductosArrayList(), getContext());
        recyclerViewProductos.setAdapter(productosAdapter);
    }
}
