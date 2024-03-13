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
import com.example.pawswhiskers.Repositorio.ListaCompra
import com.example.pawswhiskers.Repositorio.ProductoRepositorio
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class Carrito : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val containerLayout = findViewById<LinearLayout>(R.id.containerLayout)

        for (producto in ListaCompra.obtenerProductos()) {
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
            cargarImagen(this, producto.refFoto, imageViewProducto)
            textViewNombre.text = producto.nombre
            textViewPrecio.text = producto.precio

            // Agregar la vista del producto al contenedor
            containerLayout.addView(itemView)

            if(txtCantidad.text.toString().toInt() == 1){
                btnRestarCantidad.isEnabled = false
            }

            // Asignar una función al botón de eliminar
            btnEliminar.setOnClickListener {

                ListaCompra.eliminarProducto(producto)

                // Eliminar la vista del producto del contenedor
                containerLayout.removeView(itemView)

                // Solicitar un nuevo diseño para el contenedor
                containerLayout.requestLayout()
            }

            btnSumarCantidad.setOnClickListener {

                var cantidad = txtCantidad.text.toString().toInt()

                cantidad++

                txtCantidad.text = cantidad.toString()

                btnRestarCantidad.isEnabled = true

            }

            btnRestarCantidad.setOnClickListener {

                var cantidad = txtCantidad.text.toString().toInt()

                cantidad--

                txtCantidad.text = cantidad.toString()

                if(cantidad == 1){
                    btnRestarCantidad.isEnabled = false
                }

            }
        }


    }

    private fun getTotalHeight(viewGroup: ViewGroup): Int {
        var totalHeight = 0
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            totalHeight += child.height
        }
        return totalHeight
    }

    private fun cargarImagen(context: Context, nombreImagen: String?, imageView: ImageView) {
        val storage = Firebase.storage
        val storageRef = storage.reference

        // Cargar la imagen desde Firebase Storage utilizando Glide si el nombre de la imagen no es nulo
        nombreImagen?.let { imageName ->
            storageRef.child(imageName).downloadUrl.addOnSuccessListener { uri ->
                // Cargar la imagen en el ImageView usando Glide
                Glide.with(context)
                    .load(uri)
                    .into(imageView)
            }.addOnFailureListener { exception ->
                //Manej ode errores
                exception.printStackTrace()
            }
        }
    }

}