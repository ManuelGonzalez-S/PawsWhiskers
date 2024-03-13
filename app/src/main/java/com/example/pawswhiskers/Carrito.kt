package com.example.pawswhiskers

import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.example.pawswhiskers.ui.tienda.TiendaFragment
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class Carrito : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        findViewById<ImageButton>(R.id.btnVolver).setOnClickListener {
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

            btnEliminar.setOnClickListener {
                // Obtener el nombre del producto de la vista actual
                val nombreProducto = textViewNombre.text.toString()

                // Crear una lista para almacenar los productos a eliminar
                val productosAEliminar = mutableListOf<Producto>()

                // Recorrer todos los productos en la lista de compra
                for (producto in ListaCompra.obtenerProductos()) {
                    // Si el nombre del producto coincide con el nombre del producto en la vista actual
                    if (producto.nombre.equals(nombreProducto, ignoreCase = true)) {
                        // Agregar el producto a la lista de productos a eliminar
                        productosAEliminar.add(producto)
                    }
                }

                // Eliminar los productos de la lista de compra
                for (producto in productosAEliminar) {
                    ListaCompra.eliminarProducto(producto)
                }

                // Eliminar la vista del producto del contenedor
                containerLayout.removeView(itemView)

                // Solicitar un nuevo diseño para el contenedor
                containerLayout.requestLayout()

                // Si la lista de productos está vacía, cerrar la actividad del carrito
                if (ListaCompra.obtenerProductos().isEmpty()) {
                    onBackPressed()
                }
            }

            btnSumarCantidad.setOnClickListener {
                // Incrementar la cantidad del producto en el mapa y en la vista
                var cantidad = txtCantidad.text.toString().toInt()
                val nuevaCantidad = cantidad + 1

                listaProductos[nombre] = nuevaCantidad
                txtCantidad.text = nuevaCantidad.toString()

                Log.e(TAG, "\nAntes de borrar")

                ListaCompra.mostrarProductos()

                ProductoRepositorio.obtenerProducto(textViewNombre.text.toString())
                    ?.let { it1 -> ListaCompra.añadirProducto(it1) }

                ListaCompra.mostrarProductos()

                Log.e(TAG, "\nDespues de borrar")

                btnRestarCantidad.isEnabled = true
            }

            btnRestarCantidad.setOnClickListener {
                // Reducir la cantidad del producto en el mapa y en la vista
                var cantidad = txtCantidad.text.toString().toInt()
                val nuevaCantidad = cantidad - 1

                listaProductos[nombre] = nuevaCantidad
                txtCantidad.text = nuevaCantidad.toString()

                Log.e(TAG, "\nAntes de borrar")

                ListaCompra.mostrarProductos()

                ProductoRepositorio.obtenerProducto(textViewNombre.text.toString())
                    ?.let { it1 -> ListaCompra.eliminarProducto(it1) }

                ListaCompra.mostrarProductos()

                Log.e(TAG, "\nDespues de borrar")

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