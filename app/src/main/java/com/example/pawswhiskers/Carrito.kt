package com.example.pawswhiskers

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.pawswhiskers.Modelo.Producto
import com.example.pawswhiskers.Repositorio.ListaCompra
import com.example.pawswhiskers.Repositorio.ProductoRepositorio
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class Carrito : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            onBackPressed()
        }

        val containerLayout = findViewById<LinearLayout>(R.id.containerLayout)

        // Mapa que mapea el nombre del producto a su cantidad en el carrito
        val listaProductos = mutableMapOf<String, Int>()

        // Llenar el mapa con los productos de la lista de compra
        for (producto in ListaCompra.obtenerProductos()) {
            // Incrementar la cantidad del producto en el mapa
            val nombre = producto.nombre ?: "Nombre Desconocido"
            listaProductos[nombre] = (listaProductos[nombre] ?: 0) + 1
        }

        // Recorrer el mapa para crear vistas de productos en el carrito
        for ((nombre, cantidad) in listaProductos) {
            // Crear vista del producto
            val itemView = layoutInflater.inflate(R.layout.carrito_producto, containerLayout, false)

            // Buscar los elementos de la vista del producto
            val imageViewProducto = itemView.findViewById<ImageView>(R.id.imageViewProducto)
            val textViewNombre = itemView.findViewById<TextView>(R.id.textViewNombre)
            val textViewPrecio = itemView.findViewById<TextView>(R.id.textViewPrecio)
            val txtCantidad = itemView.findViewById<TextView>(R.id.txtCantidadCesta)
            val btnEliminar = itemView.findViewById<ImageButton>(R.id.btnEliminar)
            val btnSumarCantidad = itemView.findViewById<TextView>(R.id.buttonSuma)
            val btnRestarCantidad = itemView.findViewById<TextView>(R.id.buttonResta)

            // Actualizar los datos de la vista del producto con los datos del producto actual
            cargarImagen(this, ProductoRepositorio.obtenerProducto(nombre)?.refFoto, imageViewProducto)
            textViewNombre.text = nombre
            textViewPrecio.text = ProductoRepositorio.obtenerProducto(nombre)?.precio ?: "Precio Desconocido"
            txtCantidad.text = cantidad.toString()

            if(cantidad == 1){
                btnRestarCantidad.isEnabled = false
            }

            // Agregar la vista del producto al contenedor
            containerLayout.addView(itemView)

            // Asignar una función al botón de eliminar
            btnEliminar.setOnClickListener {
                // Reducir la cantidad del producto en el mapa
                val nuevaCantidad = cantidad - 1
                if (nuevaCantidad > 0) {
                    listaProductos[nombre] = nuevaCantidad
                    txtCantidad.text = nuevaCantidad.toString()
                } else {
                    // Si la cantidad llega a cero, eliminar el producto del mapa y del carrito
                    listaProductos.remove(nombre)
                    containerLayout.removeView(itemView)
                }
            }

            btnSumarCantidad.setOnClickListener {
                // Incrementar la cantidad del producto en el mapa y en la vista
                var cantidad = txtCantidad.text.toString().toInt()
                val nuevaCantidad = cantidad + 1

                listaProductos[nombre] = nuevaCantidad
                txtCantidad.text = nuevaCantidad.toString()

                btnRestarCantidad.isEnabled = true
            }

            btnRestarCantidad.setOnClickListener {
                // Reducir la cantidad del producto en el mapa y en la vista
                var cantidad = txtCantidad.text.toString().toInt()
                val nuevaCantidad = cantidad - 1

                listaProductos[nombre] = nuevaCantidad
                txtCantidad.text = nuevaCantidad.toString()

                if (nuevaCantidad == 1) {
                    btnRestarCantidad.isEnabled = false
                }else{

                }
            }
        }
    }

    private fun cargarImagen(context: Context, nombreImagen: String?, imageView: ImageView) {
        val storage = Firebase.storage
        val storageRef = storage.reference

        // Cargar la imagen desde Firebase Storage utilizando Glide si el nombre de la imagen no es nulo
        nombreImagen?.let { imageName ->
            val imageRef: StorageReference = storageRef.child(imageName)
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Cargar la imagen en el ImageView usando Glide
                Glide.with(context)
                    .load(uri)
                    .into(imageView)
            }.addOnFailureListener { exception ->
                //Manejo de errores
                exception.printStackTrace()
            }
        }
    }

}