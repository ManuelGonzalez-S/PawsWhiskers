package com.example.pawswhiskers.Modelo

class Producto(nombre: String?, precio: String?, refFoto: String?) {

    var nombre: String? = nombre
        private set

    var precio: String? = precio
        private set

    var refFoto: String? = refFoto
        private set

    companion object {
        private var listaProductos: ArrayList<Producto> = ArrayList()

        fun agregarProducto(producto: Producto) {
            listaProductos.add(producto)
        }

        fun eliminarProducto(producto: Producto) {
            listaProductos.remove(producto)
        }

        fun obtenerProductos(): ArrayList<Producto> {
            return listaProductos
        }

        fun limpiarProductos() {
            listaProductos.clear()
        }
    }

    override fun toString(): String {
        return "Producto(nombre=$nombre, precio=$precio, refFoto=$refFoto)"
    }

}