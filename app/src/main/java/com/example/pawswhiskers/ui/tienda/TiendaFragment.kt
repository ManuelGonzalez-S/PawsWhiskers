package com.example.pawswhiskers.ui.tienda

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pawswhiskers.Modelo.Producto
import com.example.pawswhiskers.R
import com.example.pawswhiskers.Repositorio.ProductoRepositorio
import com.example.pawswhiskers.databinding.FragmentTiendaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TiendaFragment : Fragment() {

    private var _binding: FragmentTiendaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTiendaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Si la lista esta vacia, se guardan los datos
        if(ProductoRepositorio.obtenerProductos().isEmpty()){


            val productosRef = FirebaseDatabase.getInstance().reference.child("Relaciones").child("Productos")

            // Agrega un listener para obtener una instantánea única de los datos
            productosRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Itera sobre los hijos de "Productos" para obtener los elementos individuales
                    for (productoSnapshot in snapshot.children) {
                        // Accede a los valores de nombre, foto y precio de cada elemento
                        val nombre = productoSnapshot.child("Nombre").getValue(String::class.java)
                        val foto = productoSnapshot.child("Foto").getValue(String::class.java)
                        val precio = productoSnapshot.child("Precio").getValue(String::class.java)

                        var miProducto = Producto(nombre, precio, foto)

                        ProductoRepositorio.agregarProducto(miProducto)

                        // Haz algo con los valores recuperados, como mostrarlos en un RecyclerView o imprimirlos en el Logcat
                        ProductoRepositorio.mostrarProductos()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Maneja la cancelación de la operación, si es necesario
                    Log.e("TAG", "Error al obtener datos: ${error.message}")
                }
            })
        }


        var listaProducto = ProductoRepositorio.obtenerProductos()
        var listaTienda = binding.listaTienda
        var topMarginInPixels = 20 // Definir el margen inicial fuera del bucle

        for ((index, producto) in listaProducto.withIndex()) {
            var txtField = EditText(context)
            val editableText = Editable.Factory.getInstance().newEditable(producto.toString())

            // Asigna el Editable como texto del EditText
            txtField.text = editableText

            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            params.topMargin = topMarginInPixels
            topMarginInPixels += 300 // Incrementa el margen para la próxima iteración

            listaTienda.addView(txtField, params)
        }

    }
}