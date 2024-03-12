package com.example.pawswhiskers

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pawswhiskers.Modelo.Producto
import com.example.pawswhiskers.Repositorio.ProductoRepositorio
import com.example.pawswhiskers.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var datosRecuperados: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Si los datos de los productos no han sido recogidos, los recoge
        if (!datosRecuperados) {
            obtenerDatos()
        }

    }

    //Recoge los datos de los productos
    private fun obtenerDatos() {

        //Recoge los nodos hijo de Productos
        val productosRef = FirebaseDatabase.getInstance().reference.child("Relaciones").child("Productos")

        productosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Itera sobre Productos para obtener los elementos uno por uno
                for (productoSnapshot in snapshot.children) {

                    // Accede a los valores de nombre, foto y precio de cada elemento
                    val nombre = productoSnapshot.child("Nombre").getValue(String::class.java)
                    val foto = productoSnapshot.child("Foto").getValue(String::class.java)
                    val precio = productoSnapshot.child("Precio").getValue(String::class.java)

                    //Convierte el nodo a un objeto
                    var miProducto = Producto(nombre, precio, foto)

                    //Lo guarda en el repositorio estatico
                    ProductoRepositorio.agregarProducto(miProducto)
                }

                // Marcar los datos como recuperados
                datosRecuperados = true
            }

            override fun onCancelled(error: DatabaseError) {
                // Maneja la cancelación de la operación, si es necesario
                Log.e("TAG", "Error al obtener datos: ${error.message}")
            }
        })
    }

}