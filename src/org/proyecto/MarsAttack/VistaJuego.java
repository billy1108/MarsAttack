package org.proyecto.MarsAttack;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.ZoomCamera;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.bitmap.BitmapTexture;
import org.anddev.andengine.opengl.texture.bitmap.BitmapTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.HorizontalAlign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

public class VistaJuego extends BaseGameActivity {
	// ===========================================================
	// Constantes
	// ===========================================================

	// Camara
	static final int CAMERA_WIDTH = 800;
	static final int CAMERA_HEIGHT = 480;

	// Escenario
	Scene scene;

	// Player_1
	static final int VelocityPlayer_1 = 300;
	private Player Player_1;
	// Pantalla

	// ===========================================================
	// Campos
	// ===========================================================

	// Camara del escenario
	private ZoomCamera mCamera;

	// Player1
	private BitmapTexture BitmapTexture_Player_1;
	private TiledTextureRegion TextureRegion_Player1;

	// Boton disparar
	private BitmapTexture BitmapTexture_boton_Disparo;
	private TextureRegion TextureRegion_boton_Disparo;

	// EnemyUFO
	private BitmapTexture BitmapTexture_EnemyUFO;
	private TiledTextureRegion TextureRegion_EnemyUFO;
	public int num_Enemy = 5;
	private ArrayList<Enemy> listaEnemigos = new ArrayList<Enemy>();

	// Disparo
	private BitmapTexture BitmapTexture_Disparo;
	public TiledTextureRegion TextureRegion_Disparo;
	private ArrayList<Disparo> listaDisparos = new ArrayList<Disparo>();
	public boolean flag_disparo = false;

	// Pantalla
	private BitmapTexture BackgroundTexture;
	private TextureRegion BackgroundLayerBack;

	// Margenes Pantalla
	public Shape ground;
	public Shape roof;
	public Shape left;
	public Shape right;

	// Joystick
	private DigitalOnScreenControl mDigitalOnScreenControl;
	private BitmapTexture mOnScreenControlTexture;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;
	public float DigitalControlX = 0;
	public float DigitalControlY = 0;

	// Puntuacion
	public int puntuacion = 0;

	// nivel de Vida
	private int nivelVida = 100;

	// variable preferencias
	private SharedPreferences pref;

	// Efectos FX juego
	public boolean Efectos;

	// Sonidos efectos
	private Sound ExplosionSound;
	private Sound DisparoSound;

	// Level Live
	public Rectangle level_life;
	private BitmapTexture BitmapTexture_level_life_Background;
	private TextureRegion TextureRegion_level_life_Background;

	// Fuente de texto
	private BitmapTexture mFontTexture;
	private Font mFont;

	// ===========================================================
	// Constructores
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Engine onLoadEngine() {
		this.mCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		this.mCamera.setBounds(0, CAMERA_WIDTH, 0, CAMERA_HEIGHT);
		this.mCamera.setBoundsEnabled(true);

		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);

		engineOptions.setNeedsSound(true);

		return new Engine(engineOptions);
	}

	public void onLoadResources() {
		// Cargamos las preferencias
		pref = this.getSharedPreferences("org.proyecto.MarsAttack_preferences",
				Context.MODE_PRIVATE);
		String Aux_numEnemigos = pref.getString("NumeroDeEnemigos", "5");
		num_Enemy = Integer.parseInt(Aux_numEnemigos);

		Efectos = pref.getBoolean("efectos", true);

		// Cargamos efectos de sonido
		SoundFactory.setAssetBasePath("mfx/");
		try {
			ExplosionSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "explosion.mp3");

			DisparoSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), this, "disparo.mp3");
		} catch (final IOException e) {
			Debug.e(e);
		}

		// Cargamos la carpeta de recursos gfx
		BitmapTextureRegionFactory.setAssetBasePath("gfx/");

		// Player_1
		this.BitmapTexture_Player_1 = new BitmapTexture(128, 128,
				TextureOptions.DEFAULT);
		this.TextureRegion_Player1 = BitmapTextureRegionFactory
				.createTiledFromAsset(this.BitmapTexture_Player_1, this,
						"Player_1.png", 0, 0, 1, 1);
		this.mEngine.getTextureManager().loadTexture(
				this.BitmapTexture_Player_1);

		// EnemyUFO
		this.BitmapTexture_EnemyUFO = new BitmapTexture(512, 512,
				TextureOptions.DEFAULT);
		this.TextureRegion_EnemyUFO = BitmapTextureRegionFactory
				.createTiledFromAsset(this.BitmapTexture_EnemyUFO, this,
						"EnemyUFO.png", 0, 0, 8, 5);
		this.mEngine.getTextureManager().loadTexture(
				this.BitmapTexture_EnemyUFO);

		// Disparo
		this.BitmapTexture_Disparo = new BitmapTexture(64, 64,
				TextureOptions.DEFAULT);
		this.TextureRegion_Disparo = BitmapTextureRegionFactory
				.createTiledFromAsset(this.BitmapTexture_Disparo, this,
						"disparo.png", 0, 0, 1, 2);
		this.mEngine.getTextureManager()
				.loadTexture(this.BitmapTexture_Disparo);

		// Cargamos las imagenes del joystick
		this.mOnScreenControlTexture = new BitmapTexture(256, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 128, 0);

		this.mEngine.getTextureManager().loadTextures(
				this.BitmapTexture_Player_1, this.mOnScreenControlTexture);

		// Cargamos las imagenes de la barra de vida
		BitmapTexture_level_life_Background = new BitmapTexture(128, 32,
				TextureOptions.DEFAULT);
		TextureRegion_level_life_Background = BitmapTextureRegionFactory
				.createFromAsset(this.BitmapTexture_level_life_Background,
						this, "level_life_background.png", 0, 0);
		mEngine.getTextureManager().loadTexture(
				BitmapTexture_level_life_Background);

		// Cargamos fuente de texto
		this.mFontTexture = new BitmapTexture(256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(
				Typeface.DEFAULT, Typeface.BOLD), 14, true, Color.GREEN);

		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);

		// Cargamos las imagenes de la Pantalla
		this.BackgroundTexture = new BitmapTexture(1024, 1024,
				TextureOptions.DEFAULT);
		this.BackgroundLayerBack = BitmapTextureRegionFactory.createFromAsset(
				this.BackgroundTexture, this, "background.png", 0, 0);

		this.mEngine.getTextureManager().loadTextures(
				this.BitmapTexture_Player_1, this.BackgroundTexture);

		// Cargamos la imagen Boton disparo
		BitmapTexture_boton_Disparo = new BitmapTexture(128, 128,
				TextureOptions.DEFAULT);
		TextureRegion_boton_Disparo = BitmapTextureRegionFactory
				.createFromAsset(BitmapTexture_boton_Disparo, this,
						"botonDisparar.png", 0, 0);
		mEngine.getTextureManager().loadTexture(
				this.BitmapTexture_boton_Disparo);

	}

	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		scene = new Scene();
		// Pantalla
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(
				0, 0, 0, 5);

		scene.setBackground(autoParallaxBackground);

		final Sprite SpriteBackground = new Sprite(0, CAMERA_HEIGHT
				- this.BackgroundLayerBack.getHeight(),
				this.BackgroundLayerBack);

		scene.attachChild(SpriteBackground);

		// Margenes Pantalla

		ground = new Rectangle(0, CAMERA_HEIGHT - 25, CAMERA_WIDTH, 25);
		roof = new Rectangle(0, 0, CAMERA_WIDTH, 25);
		left = new Rectangle(0, 0, 50, CAMERA_HEIGHT);
		right = new Rectangle(CAMERA_WIDTH - 50, 0, 50, CAMERA_HEIGHT);

		ground.setVisible(false);
		roof.setVisible(false);
		left.setVisible(false);
		right.setVisible(false);

		scene.attachChild(ground);
		scene.attachChild(roof);
		scene.attachChild(left);
		scene.attachChild(right);

		// inicializamos Player1
		final int centerX = (CAMERA_WIDTH - this.TextureRegion_Player1
				.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.TextureRegion_Player1
				.getHeight()) / 2;

		Player_1 = new Player(centerX, centerY, this.TextureRegion_Player1);

		scene.attachChild(Player_1);

		// Iniciamos levelLife

		final Sprite Sprite_LevelLifeBackground = new Sprite(2, 20,
				this.TextureRegion_level_life_Background);
		level_life = new Rectangle(6, 24, 102, 10);
		level_life.setColor(0, 255, 0);

		final Text textLive = new Text(2, 2, this.mFont, "LIVE",
				HorizontalAlign.CENTER);

		scene.attachChild(textLive);
		scene.attachChild(Sprite_LevelLifeBackground);
		scene.attachChild(level_life);

		// inicializamos EnemyUFO

		for (int i = 0; i < num_Enemy; i++) {
			añadirEnemy(i);
		}

		// Joystick
		this.mDigitalOnScreenControl = new DigitalOnScreenControl(0,
				CAMERA_HEIGHT
						- this.mOnScreenControlBaseTextureRegion.getHeight(),
				this.mCamera, this.mOnScreenControlBaseTextureRegion,
				this.mOnScreenControlKnobTextureRegion, 0.1f,
				new IOnScreenControlListener() {
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {

						// Movimiento Player_1

						if (left.collidesWith(Player_1) == true && pValueX < 0
								|| right.collidesWith(Player_1) == true
								&& pValueX > 0) {
							Player_1.mPhysicsHandler.setVelocityX(0);
						} else {
							Player_1.mPhysicsHandler.setVelocityX(pValueX
									* VelocityPlayer_1);
						}

						if (roof.collidesWith(Player_1) == true && pValueY < 0
								|| ground.collidesWith(Player_1) == true
								&& pValueY > 0) {
							Player_1.mPhysicsHandler.setVelocityY(0);
						} else {
							Player_1.mPhysicsHandler.setVelocityY(pValueY
									* VelocityPlayer_1);
						}

					}

				});

		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(
				GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
		this.mDigitalOnScreenControl.getControlBase().setScale(1.25f);
		this.mDigitalOnScreenControl.getControlKnob().setScale(1.25f);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();
		this.mDigitalOnScreenControl.setAllowDiagonal(true);

		scene.setChildScene(this.mDigitalOnScreenControl);

		// Control disparo
		final Sprite sprite = new Sprite(CAMERA_WIDTH - 100,
				CAMERA_HEIGHT - 100, this.TextureRegion_boton_Disparo) {

			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					disparar();
				}
				return true;
			}
		};

		scene.attachChild(sprite);
		scene.registerTouchArea(sprite);

		scene.registerUpdateHandler(new IUpdateHandler() {

			public void reset() {
			}

			public void onUpdate(final float pSecondsElapsed) {

				// Disparar si el flag de disparo está habilitado
				if (flag_disparo == true)
					disparar();

				// Colision de la nave con los UFO's
				for (int i = 0; i < listaEnemigos.size(); i++) {
					if (Player_1.collidesWith(listaEnemigos.get(i))) {
						// Explota UFO

						scene.detachChild(listaEnemigos.get(i));
						listaEnemigos.remove(i);

						// quitamos vida
						nivelVida = nivelVida - 10;
						LevelLife(nivelVida);

						// Play explosion FX
						if (Efectos == true)
							ExplosionSound.play();
						break;
					}
				}
				for (int j = 0; j < listaDisparos.size(); j++) {
					// Colision y destruccion de los disparos con la
					// parte
					// derecha
					// de la pantalla

					if (listaDisparos.get(j).collidesWith(right)) {
						scene.detachChild(listaDisparos.get(j));
						listaDisparos.remove(j);
						break;
					}
				}
				// Colison y destruccion de los Ufos con la parte
				// izquierda
				for (int i = 0; i < listaEnemigos.size(); i++) {
					if (listaEnemigos.get(i).collidesWith(left)) {
						scene.detachChild(listaEnemigos.get(i));
						listaEnemigos.remove(i);
						break;
					}
				}
				// Colision del disparo con los ufos
				for (int i = 0; i < listaEnemigos.size(); i++) {
					for (int j = 0; j < listaDisparos.size(); j++) {
						if (listaEnemigos.get(i).collidesWith(
								listaDisparos.get(j))) {
							scene.detachChild(listaDisparos.get(j));
							scene.detachChild(listaEnemigos.get(i));
							listaDisparos.remove(j);
							listaEnemigos.remove(i);

							// Añadimos la puntuacion
							puntuacion = puntuacion + 10;

							// Play explosion FX
							if (Efectos == true)
								ExplosionSound.play();

							break;

						}
					}
				}

				// Eliminamos explosion cuando ha finalizado la animacion

				// Añadimos enemigos cuando el num_enemy es menor que los que
				// hay en la escena, es decir que listaEnemigos.size()

				if (num_Enemy > listaEnemigos.size()) {
					añadirEnemy(listaEnemigos.size());
				}

				if (nivelVida < 0) {
					// Sale del juego
					salir();
				}

			}
		});

		return scene;

	}

	public void onLoadComplete() {

	}

	/*
	 * @Override public boolean onTouchEvent(MotionEvent event) {
	 * super.onTouchEvent(event); if (event.getAction() ==
	 * MotionEvent.ACTION_DOWN) flag_disparo = true; return true; }
	 */
	// ===========================================================
	// Métodos
	// ===========================================================

	public void LevelLife(int i) {
		level_life.setWidth(i);
	}

	public void disparar() {
		final Disparo disparo = new Disparo(
				(Player_1.getX() + TextureRegion_Disparo.getWidth()),
				(Player_1.getY() + TextureRegion_Disparo.getHeight() / 2),
				TextureRegion_Disparo);

		disparo.mPhysicsHandler.setVelocity(100, 0);
		listaDisparos.add(disparo);

		scene.attachChild(listaDisparos.get(listaDisparos.size() - 1));
		if (Efectos == true)
			DisparoSound.play();
		flag_disparo = false;
	}

	public void añadirEnemy(int i) {
		final Enemy EnemyUFO = new Enemy(
				(int) (Math.random() * (CAMERA_WIDTH - TextureRegion_EnemyUFO.getWidth()))
						+ CAMERA_WIDTH,
				(int) (Math.random() * (CAMERA_HEIGHT - TextureRegion_EnemyUFO
						.getHeight())), this.TextureRegion_EnemyUFO);
		EnemyUFO.animate(8);

		EnemyUFO.mPhysicsHandler.setVelocity(-(int) (200 * Math.random()), 0);
		listaEnemigos.add(EnemyUFO);
		scene.attachChild(listaEnemigos.get(i));
	}

	private void salir() {
		Bundle bundle = new Bundle();
		bundle.putInt("puntuacion", puntuacion);
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	// ===========================================================
	// Clases
	// ===========================================================
	private static class Player extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;

		public Player(final float pX, final float pY,
				final TiledTextureRegion pTextureRegion) {
			super(pX, pY, pTextureRegion);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);

		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			super.onManagedUpdate(pSecondsElapsed);

		}
	}

	private static class Enemy extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;

		public Enemy(final float pX, final float pY,
				final TiledTextureRegion pTextureRegion) {
			super(pX, pY, pTextureRegion);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);

		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			super.onManagedUpdate(pSecondsElapsed);
			animate(8);

		}
	}

	private static class Disparo extends AnimatedSprite {
		private final PhysicsHandler mPhysicsHandler;

		public Disparo(final float pX, final float pY,
				final TiledTextureRegion pTextureRegion) {
			super(pX, pY, pTextureRegion);
			this.mPhysicsHandler = new PhysicsHandler(this);
			this.registerUpdateHandler(this.mPhysicsHandler);

		}

		@Override
		protected void onManagedUpdate(final float pSecondsElapsed) {
			super.onManagedUpdate(pSecondsElapsed);
			animate(4);

		}
	}

	
}
