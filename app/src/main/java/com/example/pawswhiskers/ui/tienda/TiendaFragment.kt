package com.example.pawswhiskers.ui.tienda

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pawswhiskers.Carrito
import com.example.pawswhiskers.Repositorio.ListaCompra
import com.example.pawswhiskers.Repositorio.ProductoRepositorio
import com.example.pawswhiskers.databinding.FragmentTiendaBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.storage


class TiendaFragment : Fragment() {

    //Permite coger los elementos de la vista
    private var _binding: FragmentTiendaBinding? = null

    private val binding get() = _binding!!
    private lateinit var  database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        database = Firebase.database.reference
        _binding = FragmentTiendaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        comprobarListaCesta()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        comprobarListaCesta()
    }

    /*
    private fun actualizarVistaTienda() {

        val listaTienda = binding.listaTienda

        Log.e(TAG, "RESUMIR")
        ListaCompra.mostrarProductos()

        var previousItemId = View.NO_ID
        //Bucle forEach
        for ((index, producto) in ProductoRepositorio.obtenerProductos().withIndex()) {
            val itemView = layoutInflater.inflate(com.example.pawswhiskers.R.layout.item_producto, listaTienda, false)

            //Coge los campos dentro de la plantilla de los productos (item_producto.xml)
            val imageViewProducto = itemView.findViewById<ImageView>(com.example.pawswhiskers.R.id.imageViewProducto)
            val textViewNombre = itemView.findViewById<TextView>(com.example.pawswhiskers.R.id.textViewNombre)
            val textViewPrecio = itemView.findViewById<TextView>(com.example.pawswhiskers.R.id.textViewPrecio)

            //Carga la imagen segun refFoto en el producto
            cargarImagen(requireContext(), producto.refFoto, imageViewProducto)

            // Asignar nombre y precio del producto a los TextViews
            textViewNombre.text = producto.nombre
            textViewPrecio.text = producto.precio

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            //Hace un espacio entre medias de los productos para que no se superpongan
            layoutParams.topMargin = resources.getDimensionPixelSize(com.example.pawswhiskers.R.dimen.margin_between_items) * index
            itemView.layoutParams = layoutParams

            if (previousItemId != View.NO_ID) {
                layoutParams.topToBottom = previousItemId
            }

            val btnAddToCart = itemView.findViewById<Button>(com.example.pawswhiskers.R.id.btnAddToCart)

            if(ListaCompra.obtenerProductos().contains(producto)){
                // Primero establece el texto del botón
                btnAddToCart.text = "En tu cesta"
                // Luego establece el color de fondo y el color del texto
                btnAddToCart.setBackgroundColor(Color.CYAN)
                btnAddToCart.setTextColor(Color.BLACK)
            }else{
                // Primero establece el texto del botón
                btnAddToCart.text = "Añadir a la cesta"
                // Luego establece el color de fondo y el color del texto
                btnAddToCart.setBackgroundColor(Color.BLUE)
                btnAddToCart.setTextColor(Color.WHITE)
            }

            btnAddToCart.setBackgroundColor(Color.BLUE)
            // Configurar el OnClickListener para el botón
            btnAddToCart.setOnClickListener {
                // Añadir el producto a la cesta
                if (btnAddToCart.text.toString().equals("Añadir a la cesta")) {

                    ListaCompra.añadirProducto(producto)

                    Log.e(TAG, "PUEROOOOOOOOOOOOO")

                    // Primero establece el texto del botón
                    btnAddToCart.text = "En tu cesta"
                    // Luego establece el color de fondo y el color del texto
                    btnAddToCart.setBackgroundColor(Color.CYAN)
                    btnAddToCart.setTextColor(Color.BLACK)

                } else {
                    ListaCompra.eliminarProducto(producto)

                    // Primero establece el texto del botón
                    btnAddToCart.text = "Añadir a la cesta"
                    // Luego establece el color de fondo y el color del texto
                    btnAddToCart.setBackgroundColor(Color.BLUE)
                    btnAddToCart.setTextColor(Color.WHITE)
                }

                comprobarListaCesta()
            }

            // Agregar el elemento de lista a la listaTienda
            listaTienda.addView(itemView)

            // Actualizar el ID del elemento anterior
            previousItemId = itemView.id
        }

        val totalHeight = getTotalHeight(binding.listaTienda)

        // Establece la altura del ScrollView
        binding.scrollView.layoutParams.height = totalHeight

        comprobarListaCesta()
    }
    */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Ahora se guardan en MainActivity cuando se inicializa la aplicacion

        //Si la lista esta vacia, se guardan los datos
        /*
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
        */

        binding.iconoCesta.setOnClickListener{
            val intent = Intent(requireContext(), Carrito::class.java)
            startActivity(intent)
        }

        //Coge el scrollView
        val listaTienda = binding.listaTienda

        var previousItemId = View.NO_ID
        //Bucle forEach
        for ((index, producto) in ProductoRepositorio.obtenerProductos().withIndex()) {
            val itemView = layoutInflater.inflate(com.example.pawswhiskers.R.layout.item_producto, listaTienda, false)

            //Coge los campos dentro de la plantilla de los productos (item_producto.xml)
            val imageViewProducto = itemView.findViewById<ImageView>(com.example.pawswhiskers.R.id.imageViewProducto)
            val textViewNombre = itemView.findViewById<TextView>(com.example.pawswhiskers.R.id.textViewNombre)
            val textViewPrecio = itemView.findViewById<TextView>(com.example.pawswhiskers.R.id.textViewPrecio)

            //Carga la imagen segun refFoto en el producto
            cargarImagen(requireContext(), producto.refFoto, imageViewProducto)

            // Asignar nombre y precio del producto a los TextViews
            textViewNombre.text = producto.nombre
            textViewPrecio.text = producto.precio

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            //Hace un espacio entre medias de los productos para que no se superpongan
            layoutParams.topMargin = resources.getDimensionPixelSize(com.example.pawswhiskers.R.dimen.margin_between_items) * index
            itemView.layoutParams = layoutParams

            if (previousItemId != View.NO_ID) {
                layoutParams.topToBottom = previousItemId
            }

            // Buscar el botón "Añadir a la cesta" en el diseño del producto
            val btnAddToCart = itemView.findViewById<Button>(com.example.pawswhiskers.R.id.btnAddToCart)

            if(ListaCompra.obtenerProductos().contains(producto)){
                btnAddToCart.text = "En tu cesta"
                // Luego establece el color de fondo y el color del texto
                btnAddToCart.setBackgroundColor(Color.CYAN)
                btnAddToCart.setTextColor(Color.BLACK)
            }else{
                btnAddToCart.text = "Añadir a la cesta"
                // Luego establece el color de fondo y el color del texto
                btnAddToCart.setBackgroundColor(Color.BLUE)
                btnAddToCart.setTextColor(Color.WHITE)
            }

            btnAddToCart.setBackgroundColor(Color.BLUE)
            // Configurar el OnClickListener para el botón
            btnAddToCart.setOnClickListener {
                // Añadir el producto a la cesta
                if (btnAddToCart.text.toString().equals("Añadir a la cesta")) {

                    ListaCompra.añadirProducto(producto)

                    Log.e(TAG, "PUEROOOOOOOOOOOOO")

                    // Primero establece el texto del botón
                    btnAddToCart.text = "En tu cesta"
                    // Luego establece el color de fondo y el color del texto
                    btnAddToCart.setBackgroundColor(Color.CYAN)
                    btnAddToCart.setTextColor(Color.BLACK)

                } else {
                    ListaCompra.eliminarProducto(producto)

                    // Primero establece el texto del botón
                    btnAddToCart.text = "Añadir a la cesta"
                    // Luego establece el color de fondo y el color del texto
                    btnAddToCart.setBackgroundColor(Color.BLUE)
                    btnAddToCart.setTextColor(Color.WHITE)
                }

                comprobarListaCesta()
            }


            // Agregar el elemento de lista a la listaTienda
            listaTienda.addView(itemView)

            // Actualizar el ID del elemento anterior
            previousItemId = itemView.id
        }

        val totalHeight = getTotalHeight(binding.listaTienda)

        // Establece la altura del ScrollView
        binding.scrollView.layoutParams.height = totalHeight

    }

    private fun comprobarListaCesta(){

        var totalCesta = ListaCompra.obtenerProductos().size

        if(totalCesta == 0){

            binding.circuloConTexto.visibility = View.GONE
            binding.iconoCesta.visibility = View.GONE
            binding.txtCantidadCesta.visibility = View.GONE

        }else{
            binding.circuloConTexto.visibility = View.VISIBLE
            binding.iconoCesta.visibility = View.VISIBLE

            binding.txtCantidadCesta.text = totalCesta.toString()

            binding.txtCantidadCesta.visibility = View.VISIBLE
        }

    }


    /*
    private fun writeProduct(idProducto: String, idUsuario: String, cantidad: Int) {

        // Creamos un nuevo nodo "productos" y obtenemos una referencia a él
        val productsRef = database.child("Relaciones").child("ProductoXUsuario")


        // Creamos un nuevo nodo hijo bajo "ProductoXUsuario" con un ID único generado automáticamente por Firebase
        val newProductRef = productsRef.push()

        // Creamos un mapa para almacenar los datos del producto
        val productMap = HashMap<String, Any>()
        productMap["idProducto"] = idProducto
        productMap["idUsuario"] = idUsuario
        productMap["cantidad"] = cantidad

        // Escribimos los datos del producto en la base de datos
        newProductRef.setValue(productMap)
            .addOnSuccessListener {
                // Manejamos el éxito, si es necesario
            }
            .addOnFailureListener { e ->
                // Manejamos el fallo
                // Por ejemplo, si la escritura en la base de datos falla
                e.printStackTrace()
            }
    }
     */


    //Coge la altura total del ScrollView
    private fun getTotalHeight(viewGroup: ViewGroup): Int {
        var totalHeight = 0
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            totalHeight += child.height
        }
        return totalHeight
    }

    //Obtiene la imagen de Firebase Storage
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