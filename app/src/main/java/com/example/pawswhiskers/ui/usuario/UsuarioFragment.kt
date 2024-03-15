package com.example.pawswhiskers.ui.usuario

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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
                val repPassword = binding.etRepPassword.text.toString()
                if(email.contains('@') && !email.toString().substringAfter('@').contains('@')){
                    if (email.contains('.') && !email.toString().substringAfter('.').contains('.')){
                        if (email.contains("@gmail.com") && email.toString().substringAfter("@gmail.com").isEmpty()){
                            if (password.length>5){
                                if (password.equals(repPassword)){
                                    register(email, password)
                                }else{
                                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(context, "El email debe terminar en '@gmail.com'", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, "El email debe tener un '.'", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "El email debe tener una '@'", Toast.LENGTH_SHORT).show()
                }

            }

            btnRegistro.visibility= View.GONE
            etRepPassword.visibility= View.GONE
            btnActualizar.visibility= View.GONE
            etActualizarPassword.visibility= View.GONE
            etActualizarCorreo.visibility= View.GONE

            btnLogOut.setOnClickListener {
                signOut()

                binding.etRepPassword.visibility = View.GONE
                binding.btnRegistro.visibility = View.GONE

                etEmail.setText("")
                etContrasena.setText("")
                etRepPassword.setText("")
            }

            btnCambiarVista.setOnClickListener {
                if (btnLogin.isVisible){
                    btnLogin.visibility = View.GONE
                    btnRegistro.visibility = View.VISIBLE
                    etRepPassword.visibility = View.VISIBLE
                    btnCambiarVista.setText("Ir a login")
                    etEmail.setText("")
                    etContrasena.setText("")
                }else{
                    btnLogin.visibility = View.VISIBLE
                    btnRegistro.visibility = View.GONE
                    etRepPassword.visibility = View.GONE
                    btnCambiarVista.setText("Ir a registro")
                    etEmail.setText("")
                    etContrasena.setText("")
                    etRepPassword.setText("")
                }
            }

            btnActualizarDatos.setOnClickListener {
                btnActualizarDatos.visibility = View.GONE
                btnActualizar.visibility = View.VISIBLE
                etActualizarCorreo.visibility = View.VISIBLE
                etActualizarPassword.visibility = View.VISIBLE
                btnLogOut.visibility = View.GONE
            }

            btnActualizar.setOnClickListener {
                val email = binding.etActualizarCorreo.text.toString()
                val password = binding.etActualizarPassword.text.toString()
                if(email.contains('@') && !email.toString().substringAfter('@').contains('@')){
                    if (email.contains('.') && !email.toString().substringAfter('.').contains('.')){
                        if (email.contains("@gmail.com") && email.toString().substringAfter("@gmail.com").isEmpty()){
                            if (password.length>5){
                                updateEmail()
                                updatePassword()
                                Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                                updateUI(Firebase.auth.currentUser)
                            }else{
                                Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(context, "El email debe terminar en '@gmail.com'", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, "El email debe tener un '.'", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "El email debe tener una '@'", Toast.LENGTH_SHORT).show()
                }

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


    fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser

                    Toast.makeText(
                        context,
                        "Bienvenido " + user?.email.toString().substringBefore('@'),
                        Toast.LENGTH_SHORT,
                    ).show()

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

                    Toast.makeText(
                        context,
                        "Bienvenido " + auth.currentUser?.email.toString().substringBefore('@'),
                        Toast.LENGTH_SHORT,
                    ).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context,
                        "Este correo ya ha sido registrado",
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
            binding.etRepPassword.visibility= View.GONE
            binding.btnCambiarVista.visibility = View.GONE

            binding.btnLogOut.visibility = View.VISIBLE
            cargarImagen("perfil.png")
            binding.txtCorreoUsuario.text = Firebase.auth.currentUser?.email.toString().substringBefore('@')
            binding.imageView2.visibility = View.VISIBLE
            binding.txtCorreoUsuario.visibility = View.VISIBLE
            binding.btnActualizarDatos.visibility = View.VISIBLE
            binding.btnActualizar.visibility = View.GONE
            binding.etActualizarPassword.setText("")
            binding.etActualizarCorreo.setText("")
            binding.etActualizarCorreo.visibility = View.GONE
            binding.etActualizarPassword.visibility = View.GONE
        }else{
            //Si el usuario NO está logeado

            binding.etEmail.visibility = View.VISIBLE
            binding.etContrasena.visibility = View.VISIBLE

            binding.btnLogin.visibility = View.VISIBLE
            binding.btnRegistro.visibility = View.VISIBLE
            binding.etRepPassword.visibility = View.VISIBLE
            binding.btnCambiarVista.visibility = View.VISIBLE

            binding.btnLogOut.visibility = View.GONE
            binding.imageView2.visibility = View.GONE
            binding.txtCorreoUsuario.visibility = View.GONE
            binding.btnActualizarDatos.visibility = View.GONE
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

    private fun updateEmail() {
        // [START update_email]
        val user = Firebase.auth.currentUser
        user?.updateEmail(binding.etActualizarCorreo.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email address updated successfully.")
                } else {
                    Log.w(TAG, "Failed to update user email address.", task.exception)
                }
            }
        // [END update_email]
    }

    private fun updatePassword() {
        // [START update_password]
        val user = Firebase.auth.currentUser
        user!!.updatePassword(binding.etActualizarPassword.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User password updated.")
                }else{
                    Log.d(TAG, "ERROR User password NOT updated.")
                }
            }
        // [END update_password]
    }

}