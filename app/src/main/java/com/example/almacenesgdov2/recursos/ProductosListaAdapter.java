package com.example.almacenesgdov2.recursos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.almacenesgdov2.R;
import com.example.almacenesgdov2.objetos.Productos;

import java.util.ArrayList;
import java.util.List;

public class ProductosListaAdapter extends RecyclerView.Adapter<ProductosListaAdapter.viewHolder> implements Filterable {
    private List<Productos> productosList;
    private Context context;
    /*
       Inician cambios reversibles
    */
    private List<Productos> productosListRespaldo;
    /*
        Finalizan
    */

    public ProductosListaAdapter(List<Productos> productosList, Context context) {
        this.productosList = productosList;
        this.context = context;
        this.productosListRespaldo = new ArrayList<>(productosList);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_items_revision,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.txvCCodigo.setText(productosList.get(position).getCodigo());
        holder.txvCDescripcion.setText(productosList.get(position).getDescripcion());
        holder.txvCCantidad.setText(productosList.get(position).getCantidad());
        holder.txvCUnidad.setText(productosList.get(position).getUnidades());
    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    //Cambios
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Productos> filterList = new ArrayList<>();

            if(charSequence.toString().isEmpty()){
                filterList.addAll(productosListRespaldo);
            }else{
                for(Productos productos: productosListRespaldo){
                    if(productos.getDescripcion().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterList.add(productos);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            productosList = (List<Productos>) filterResults.values;
            notifyDataSetChanged();
        }
    };

    //fin cambios

    public static class viewHolder extends RecyclerView.ViewHolder{
        private TextView txvCCodigo, txvCDescripcion, txvCCantidad, txvCUnidad;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            txvCCodigo = itemView.findViewById(R.id.txtCCodigo);
            txvCDescripcion = itemView.findViewById(R.id.txtCDescripcion);
            txvCCantidad = itemView.findViewById(R.id.txtCCantidad);
            txvCUnidad = itemView.findViewById(R.id.txtCUnidad);
        }
    }
}
