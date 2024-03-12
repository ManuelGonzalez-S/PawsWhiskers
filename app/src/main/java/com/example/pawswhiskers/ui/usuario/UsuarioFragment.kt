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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = Firebase.database.reference

        if(checkCurrentUser()){
            updateUI(Firebase.auth.currentUser)
        }else{
            updateUI(null)
        }

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

            btnLogin.setOnClickListener {
                val email = binding.etEmail.text.toString()
                val password = binding.etContrasena.text.toString()

                if(email.isEmpty()){
                    Toast.makeText(context, "Completa el correo", Toast.LENGTH_SHORT).show()
                }else if(password.isEmpty()){
                    Toast.makeText(context, "Completa la contraseña", Toast.LENGTH_SHORT).show()
                }else{
                    signIn(email, password)
                }

            }

            btnRegistro.setOnClickListener {
                val email = binding.etEmail.text.toString()
                val password = binding.etContrasena.text.toString()
                register(email, password)
            }

            btnLogOut.setOnClickListener {
                signOut()
            }

        }

        // Initialize Firebase Auth
        auth = Firebase.auth
    }

    private fun cargarImagen(nombreImagen: String){

        val storage = Firebase.storage

        val storageRef = storage.reference

        val imageView = binding.imageView2

        storageRef.child(nombreImagen).downloadUrl.addOnSuccessListener { uri ->
            // Usa Glide (o cualquier otra biblioteca de carga de imágenes) para cargar la imagen en el ImageView
            Glide.with(this /* context */)
                .load(uri)
                .into(imageView)
        }.addOnFailureListener { exception ->
            // Manejar errores
            // Por ejemplo, si la imagen no se puede descargar
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


    fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
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

    private fun register(email: String, password: String){

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
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

    private fun updateUI(user: FirebaseUser?) {

        if(user != null){
            //Si el usuario esta logeado
            binding.etEmail.visibility = View.GONE
            binding.etContrasena.visibility = View.GONE

            binding.btnLogin.visibility = View.GONE
            binding.btnRegistro.visibility = View.GONE

            binding.btnLogOut.visibility = View.VISIBLE
            cargarImagen("cloud.jpg")
            binding.imageView2.visibility = View.VISIBLE
        }else{
            //Si el usuario NO está logeado

            binding.etEmail.visibility = View.VISIBLE
            binding.etContrasena.visibility = View.VISIBLE

            binding.btnLogin.visibility = View.VISIBLE
            binding.btnRegistro.visibility = View.VISIBLE

            binding.btnLogOut.visibility = View.GONE
            binding.imageView2.visibility = View.GONE
        }

    }

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

    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]
        updateUI(null)
    }

}