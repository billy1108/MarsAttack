package org.proyecto.MarsAttack;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MarsAttack extends Activity {
	
	private Button bSalir;
	private Button bPreferencias;
	private Button bJuego;
	private Button bPuntuacion;
	public static AlmacenPuntuaciones almacen;
	public MediaPlayer mp;
	
	
	// variable preferencias
	private SharedPreferences pref;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		pref = getSharedPreferences("org.proyecto.MarsAttack_preferences",
				Context.MODE_PRIVATE);

		mp = MediaPlayer.create(this, R.raw.audio);

		if (pref.getString("Almacenamiento", "0").equals("0"))
			almacen = new AlmacenPuntuacionesXML_SAX(this);
		if (pref.getString("Almacenamiento", "0").equals("1"))
			almacen = new AlmacenPuntuacionesSerWeb();
	
		
		if (pref.getBoolean("musica", true) == false) {
			mp.pause();
		}

		bPuntuacion = (Button) findViewById(R.id.button4);
		bPuntuacion.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				lanzarSalir_boton();
			}
		});
		Typeface font_bPuntuacion = Typeface.createFromAsset(getAssets(), "AstronBoy.ttf");
		bPuntuacion.setTypeface(font_bPuntuacion);

		bSalir = (Button) findViewById(R.id.button3);
		bSalir.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				lanzarPuntuaciones();
			}
		});
		Typeface font_bSalir = Typeface.createFromAsset(getAssets(), "AstronBoy.ttf");
		bSalir.setTypeface(font_bSalir);

		bPreferencias = (Button) findViewById(R.id.button2);
		bPreferencias.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				lanzarPreferencias();
			}
		});
		Typeface font_bPreferencias = Typeface.createFromAsset(getAssets(), "AstronBoy.ttf");
		bPreferencias.setTypeface(font_bPreferencias);

		bJuego = (Button) findViewById(R.id.button1);
		bJuego.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				lanzarJuego();
			}
		});
		Typeface font_bJuego = Typeface.createFromAsset(getAssets(), "AstronBoy.ttf");
		bJuego.setTypeface(font_bJuego);

		TextView texto = (TextView) findViewById(R.id.TextView);
		//Cargamos el tipo de fuente personalizado
		Typeface font_Texview = Typeface.createFromAsset(getAssets(), "ALIEE___.TTF");
		texto.setTypeface(font_Texview);
		
		Animation animacion = AnimationUtils.loadAnimation(this,
				R.anim.animaciontitulo);
		texto.startAnimation(animacion);

		Animation animacion_bJuego = AnimationUtils.loadAnimation(this,
				R.anim.animacion_boton_1);
		bJuego.startAnimation(animacion_bJuego);

		Animation animacion_bPreferencias = AnimationUtils.loadAnimation(this,
				R.anim.animacionboton_2);
		bPreferencias.startAnimation(animacion_bPreferencias);

		Animation animacion_bSalir = AnimationUtils.loadAnimation(this,
				R.anim.animacionboton_3);
		bSalir.startAnimation(animacion_bSalir);

		Animation animacion_bPuntuacion = AnimationUtils.loadAnimation(this,
				R.anim.animacionboton_4);
		bSalir.startAnimation(animacion_bPuntuacion);

		
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		

	}


	public void lanzarSalir_boton() {
				
		finish();

	}

	public void lanzarPuntuaciones() {
		Intent i = new Intent(this, Puntuaciones.class);
		startActivity(i);
	}

	public void lanzarPreferencias() {
		Intent i = new Intent(this, Preferencias.class);
		startActivity(i);

	}

	public void lanzarJuego() {
		Intent i = new Intent(this, VistaJuego.class);
		startActivityForResult(i, 1234);
	}

	public void lanzarSalir() {
		finish();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1234 & resultCode == RESULT_OK & data != null) {
			int puntuacion = data.getExtras().getInt("puntuacion");
			String nombre = "Player";
			// Leerlo desde un Dialog o una nueva actividad AlertDialog.Builder
			almacen.guardarPuntuacion(puntuacion, nombre,
					System.currentTimeMillis());
			lanzarPuntuaciones();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.Salir:
			lanzarSalir_boton();
			break;

		case R.id.config:
			lanzarPreferencias();
			break;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (pref.getBoolean("musica", true) == true)
			mp.start();

		if (pref.getString("Almacenamiento", "0").equals("0"))
			almacen = new AlmacenPuntuacionesXML_SAX(this);
		if (pref.getString("Almacenamiento", "0").equals("1"))
			almacen = new AlmacenPuntuacionesSerWeb();
				
	}

	@Override
	protected void onPause() {
		super.onPause();
		mp.pause();

		if (pref.getString("Almacenamiento", "0").equals("0"))
			almacen = new AlmacenPuntuacionesXML_SAX(this);
		if (pref.getString("Almacenamiento", "0").equals("1"))
			almacen = new AlmacenPuntuacionesSerWeb();
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (pref.getBoolean("musica", true) == true)
			mp.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mp.pause();
	}

	@Override
	protected void onSaveInstanceState(Bundle guardarEstado) {
		super.onSaveInstanceState(guardarEstado);
		int pos = mp.getCurrentPosition();
		guardarEstado.putInt("posicion", pos);

	}

	@Override
	protected void onRestoreInstanceState(Bundle recEstado) {
		super.onRestoreInstanceState(recEstado);
		int pos = recEstado.getInt("posicion");
		if (pref.getBoolean("musica", true) == true)
			mp.seekTo(pos);
	}
}