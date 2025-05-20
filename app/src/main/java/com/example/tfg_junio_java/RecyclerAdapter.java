package com.example.tfg_junio_java;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PeliculasViewHolder> implements Filterable {

    private ArrayList<Pelicula> listaPelis;
    private ArrayList<Pelicula> listaPelisFull;
    private FragmentTransaction ft;
    private Context context;

    public RecyclerAdapter(Context context, ArrayList<Pelicula> listaPelis, FragmentTransaction ft) {
        this.context = context;
        this.listaPelis = listaPelis;
        this.listaPelisFull = new ArrayList<>(listaPelis);
        ;
    }

    @NonNull
    @Override
    public PeliculasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_pelis_row, parent, false);
        return new PeliculasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculasViewHolder holder, int position) {
        Pelicula peli = listaPelis.get(position);
        holder.titulo.setText(peli.getNombrePeli());
        holder.detalle.setText(peli.getSinopsis());

        // Cargar imagen desde URL con Glide
        Glide.with(context)
                .load(peli.getImagenUrl())
                .placeholder(R.drawable.placeholder) // imagen temporal mientras carga
                .error(R.drawable.error_image)       // imagen si falla la carga
                .into(holder.trailer);
    }

    @Override
    public int getItemCount() {
        return listaPelis.size();
    }

    @Override
    public Filter getFilter() {
        return listaPelisFiltrada;
    }

    private final Filter listaPelisFiltrada = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Pelicula> listaFiltrada = new ArrayList<>();
            String filterPattern = charSequence.toString().toLowerCase().trim();

            if (charSequence == null || charSequence.length() == 0) {
                listaFiltrada.addAll(listaPelisFull);
            } else {
                for (Pelicula peli : listaPelisFull) {
                    if (peli.getNombrePeli().toLowerCase().contains(filterPattern)) {
                        listaFiltrada.add(peli);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = listaFiltrada;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listaPelis.clear();
            listaPelis.addAll((List<Pelicula>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class PeliculasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titulo;
        private final TextView detalle;
        private final ImageView trailer;

        public PeliculasViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTitulo);
            detalle = itemView.findViewById(R.id.textDetalles);
            trailer = itemView.findViewById(R.id.imageTrailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Pelicula peli = listaPelis.get(position);
                FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, Detalles.newInstance(peli));
                ft.addToBackStack(null);
                ft.commit();
            }
        }
    }
}
