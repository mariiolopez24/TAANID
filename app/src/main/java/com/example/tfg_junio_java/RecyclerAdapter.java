
package com.example.tfg_junio_java;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PeliculasViewHolder> implements Filterable {

    private final ArrayList<Pelicula> listaPelis;
    private final ArrayList<Pelicula> listaPelisFull;
    private final FragmentTransaction ft;
    private final boolean esAdmin;
    private final Context context;
    private FirebaseUser user;

    public RecyclerAdapter(Context context, ArrayList<Pelicula> listaPelis, FragmentTransaction ft, boolean esAdmin) {
        this.context = context;
        this.listaPelis = listaPelis;
        this.listaPelisFull = new ArrayList<>(listaPelis);
        this.ft = ft;
        this.esAdmin = esAdmin;
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
        String idioma = java.util.Locale.getDefault().getLanguage(); // "es", "en", etc.
        String sinopsis = peli.getSinopsis().get(idioma);
        if (sinopsis == null) {
            sinopsis = peli.getSinopsis().get("es"); // idioma por defecto
        }
        holder.detalle.setText(sinopsis);


        Glide.with(context)
                .load(peli.getImagenUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(holder.trailer);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("DatosUsuario")
                    .document(user.getUid())
                    .collection("Favoritos")
                    .document(peli.getId())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            holder.estrella.setImageResource(R.drawable.ic_star_filled); // naranja
                        } else {
                            holder.estrella.setImageResource(R.drawable.ic_star_border); // blanca
                        }
                    });
        }

        holder.estrella.setOnClickListener(v -> {
            user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null || user.isAnonymous()) {
                Toast.makeText(context, context.getString(R.string.iniciarFavoritos), Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = user.getUid();
            String peliId = peli.getId();

            db.collection("DatosUsuario").document(uid)
                    .collection("Favoritos").document(peliId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            db.collection("DatosUsuario").document(uid)
                                    .collection("Favoritos").document(peliId)
                                    .delete();
                            holder.estrella.setImageResource(R.drawable.ic_star_border);
                        } else {
                            db.collection("DatosUsuario").document(uid)
                                    .collection("Favoritos").document(peliId)
                                    .set(peli);
                            holder.estrella.setImageResource(R.drawable.ic_star_filled);
                        }
                    });
        });
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
                    String titulo = peli.getNombrePeli() != null ? peli.getNombrePeli().toLowerCase() : "";
                    String idioma = java.util.Locale.getDefault().getLanguage();
                    String sinopsis = "";
                    if (peli.getSinopsis() != null) {
                        sinopsis = peli.getSinopsis().getOrDefault(idioma, peli.getSinopsis().get("es"));
                        if (sinopsis == null) sinopsis = "";
                        sinopsis = sinopsis.toLowerCase();
                    }


                    if (titulo.contains(filterPattern) || sinopsis.contains(filterPattern)) {
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
        public final ImageView estrella;

        public PeliculasViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTitulo);
            detalle = itemView.findViewById(R.id.textDetalles);
            trailer = itemView.findViewById(R.id.imageTrailer);
            estrella = itemView.findViewById(R.id.estrellaFavorito);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Pelicula peli = listaPelis.get(position);

                FragmentTransaction nuevaTransaccion = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                nuevaTransaccion.setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                );
                nuevaTransaccion.replace(R.id.fragmentContainerView, Detalles.newInstance(peli, esAdmin));
                nuevaTransaccion.addToBackStack(null);
                nuevaTransaccion.commit();
            }
        }
    }
}
