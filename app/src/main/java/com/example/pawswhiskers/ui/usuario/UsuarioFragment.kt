package com.example.pawswhiskers.ui.usuario

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pawswhiskers.databinding.FragmentUserBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.storage

class UsuarioFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentUserBinding? = null

    private lateinit var database: DatabaseReference

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = Firebase.database.reference

        //Muestra en el Logcat el email del usuario logeado
        Log.d("TAG", "Usuarioooooooooooooooooooo: " + Firebase.auth.currentUser?.email.toString())

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            //Asigna al boton de inicio de sesion una funcion
            btnLogin.setOnClickListener {

                //Recoge el contenido de los campos
                val email = binding.etEmail.text.toString()
                val password = binding.etContrasena.text.toString()

                //Hace las validaciones
                if(email.isEmpty()){
                    Toast.makeText(context, "Completa el correo", Toast.LENGTH_SHORT).show()
                }else if(password.isEmpty()){
                    Toast.makeText(context, "Completa la contraseña", Toast.LENGTH_SHORT).show()

                }else{
                    //Si todo esta ok, se logea
                    signIn(email, password)
                }

            }

            //Asigna al boton de registrarse una funcion
            btnRegistro.setOnClickListener {

                //Recoge el contenido de los campos
                val email = binding.etEmail.text.toString()
                val password = binding.etContrasena.text.toString()

                //Hace las validaciones
                if(email.isEmpty()){
                    Toast.makeText(context, "Completa el correo", Toast.LENGTH_SHORT).show()
                }else if(password.isEmpty()){
                    Toast.makeText(context, "Completa la contraseña", Toast.LENGTH_SHORT).show()

                }else{
                    //Si todo esta ok, se registra
                    register(email, password)
                }

            }

            //Asigna al boton de cerrar sesión su función
            btnLogOut.setOnClickListener {
                signOut()
            }

        }

        // Initialize Firebase Auth
        auth = Firebase.auth

        //Verifica que el usuario esté logeado y cambia la vista
        if(Firebase.auth.currentUser != null){
            updateUI(Firebase.auth.currentUser)
        }else{
            updateUI(null)
        }

    }

    //Obtiene la imagen de Firebase Storage
    private fun cargarImagen(nombreImagen: String){

        //Obtiene el storage
        val storage = Firebase.storage

        //Coge la referencia (URL)
        val storageRef = storage.reference

        //Coge el imageView
        val imageView = binding.imageView2

        storageRef.child(nombreImagen).downloadUrl.addOnSuccessListener { uri ->
            //Carga la imagen en el imageView
            Glide.with(this /* context */)
                .load(uri)
                .into(imageView)
        }.addOnFailureListener { exception ->
            //Manejo de errores
            exception.printStackTrace()
        }
    }


    private fun writeProduct(nombreProducto: String, nombreFoto: String, nombrePrecio: String) {

        // Creamos un nuevo nodo "productos" y obtenemos una referencia a él
        val productsRef = database.child("Relaciones").child("Productos")

        // Creamos un nuevo nodo hijo bajo "productos" con un ID único generado automáticamente por Firebase
        val newProductRef = productsRef.push()

        // Creamos un mapa para almacenar los datos del producto
        val productMap = HashMap<String, Any>()
        productMap["Nombre"] = nombreProducto
        productMap["Foto"] = nombreFoto
        productMap["Precio"] = nombrePrecio

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


    //Inicia sesión
    fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->

                //Si todo bien, se actualiza la vista
                if (task.isSuccessful) {

                    Log.d(TAG, "signInWithEmail:success")

                    val user = auth.currentUser

                    Toast.makeText(
                        context,
                        "Bienvenido " + user?.email.toString().substringBefore('@'),
                        Toast.LENGTH_SHORT,
                    ).show()

                    updateUI(user)

                //Si algo sale mal, se le informa al usuario y se actualiza la vista por si acaso
                } else {

                    Log.w(TAG, "signInWithEmail:failure", task.exception)

                    Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                    updateUI(null)

                }
            }
    }

    //Registra al usuario
    private fun register(email: String, password: String){

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->

                //Si todo bien, se actualiza la vista
                if (task.isSuccessful) {

                    Log.d(TAG, "createUserWithEmail:success")

                    val user = auth.currentUser

                    Toast.makeText(
                        context,
                        "Bienvenido " + user?.email.toString().substringBefore('@'),
                        Toast.LENGTH_SHORT,
                    ).show()

                    updateUI(user)

                //Si algo sale mal, se le informa al usuario y se actualiza la vista por si acaso
                } else {

                    Log.w(TAG, "createUserWithEmail:failure", task.exception)

                    Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                    updateUI(null)

                }
            }
    }

    //Actualiza la vista según el parametro
    private fun updateUI(user: FirebaseUser?) {

        //Si el usuario esta logeado
        if(user != null){

            //Se quitan los elementos de inicio de sesion
            binding.etEmail.visibility = View.GONE
            binding.etContrasena.visibility = View.GONE
            binding.btnLogin.visibility = View.GONE
            binding.btnRegistro.visibility = View.GONE

            //Aparecen los elementos de la sesión
            binding.btnLogOut.visibility = View.VISIBLE
            cargarImagen("cloud.jpg")
            binding.imageView2.visibility = View.VISIBLE
            binding.txtCorreoUsuario.text = Firebase.auth.currentUser?.email.toString().substringBefore('@')
            binding.txtCorreoUsuario.visibility = View.VISIBLE

        //Si el usuario NO está logeado
        }else{

            //Se ponen los elementos de inicio de sesion
            binding.etEmail.visibility = View.VISIBLE
            binding.etContrasena.visibility = View.VISIBLE

            binding.btnLogin.visibility = View.VISIBLE
            binding.btnRegistro.visibility = View.VISIBLE

            //Se quitan los elementos de la sesión iniciada
            binding.btnLogOut.visibility = View.GONE
            binding.imageView2.visibility = View.GONE
            binding.txtCorreoUsuario.visibility = View.GONE
        }

    }

    //Comprueba el usuario que este logeado
    private fun checkCurrentUser(): Boolean {
        // [START check_current_user]
        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
            return true
        } else {
            // No user is signed in
            return false
        }
        // [END check_current_user]
    }

    //Cierra sesion
    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]
        updateUI(null)
    }

}