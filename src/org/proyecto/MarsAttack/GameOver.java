package org.proyecto.MarsAttack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class GameOver extends Activity {
	 /** Called when the activity is first created. */
	public int Puntuacion;
	public EditText Text_nombre_In;
	public Button bAceptar;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameover);
        
        Text_nombre_In= (EditText) findViewById(R.id.editText1);
        
        bAceptar = (Button) findViewById(R.id.button_GameOver);
        bAceptar.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				salir();
			}
		}); 
    }
    
    public void salir() {
		Bundle bundle = new Bundle();
		bundle.putString("nombre", Text_nombre_In
				.getText().toString());
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}
