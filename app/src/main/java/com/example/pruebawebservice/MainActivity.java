package com.example.pruebawebservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText etCodigo, etProducto, etPrecio, etFabricante;
    Button BtnAgregar, BtnBuscar, BtnEditar, BtnEliminar;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enlaceObjetos();

        accionDeBotones();
    }

    public void enlaceObjetos(){
        etCodigo = (EditText)findViewById(R.id.etCodigo);
        etProducto = (EditText)findViewById(R.id.etProducto);
        etPrecio = (EditText)findViewById(R.id.etPrecio);
        etFabricante = (EditText)findViewById(R.id.etFabricante);
        BtnAgregar = (Button) findViewById(R.id.BtnAgregar);
        BtnBuscar = (Button)findViewById(R.id.BtnBuscar);
        BtnEditar = (Button)findViewById(R.id.BtnEditar);
        BtnEliminar = (Button)findViewById(R.id.BtnEliminar);
    }

    public void accionDeBotones(){
        BtnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://10.1.19.2:80/Developeru/insertar_producto.php");//Nuestra ip y la direccion donde esta el .php
            }
        });

        BtnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarProducto("http://10.1.19.2:80/Developeru/buscar_producto.php?codigo=" + etCodigo.getText() + "");
            }
        });

        BtnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutarServicio("http://10.1.19.2:80/Developeru/editar_producto.php");
            }
        });

        BtnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarProducto("http://10.1.19.2:80/Developeru/eliminar_producto.php");
            }
        });
    }

    private void ejecutarServicio(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "OPERACION EXITOSA", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String, String>();
                parametros.put("codigo", etCodigo.getText().toString());//primero va el nombre con el que lo identificamos en el PHP
                parametros.put("producto", etProducto.getText().toString());
                parametros.put("precio", etPrecio.getText().toString());
                parametros.put("fabricante", etFabricante.getText().toString());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void buscarProducto(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        etProducto.setText(jsonObject.getString("producto"));
                        etPrecio.setText(jsonObject.getString("precio"));
                        etFabricante.setText(jsonObject.getString("fabricante"));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void eliminarProducto(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "EL PRODUCTO FUE ELIMINADO", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String, String>();
                parametros.put("codigo", etCodigo.getText().toString());//primero va el nombre con el que lo identificamos en el PHP
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void limpiarFormulario(){
        etFabricante.setText("");
        etPrecio.setText("");
        etProducto.setText("");
        etCodigo.setText("");

    }
}
