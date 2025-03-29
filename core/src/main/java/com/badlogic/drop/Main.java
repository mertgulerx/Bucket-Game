package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.w3c.dom.Text;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    private Texture backgroundTexture;
    private Texture bucketTexture;
    private Texture dropTexture;
    private Sound dropSound;
    private Music music;
    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private Sprite bucketSprite;
    private Vector2 touchPos;
    private Array<Sprite> dropSprites;
    private float dropTimer;
    private Rectangle bucketRectangle;
    private Rectangle dropRectangle;
    private Integer score;
    private float dropSpeed;
    private float moveSpeed;
    private float dropRate;
    private float increment;

    @Override
    public void create() {
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1);

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        touchPos = new Vector2();
        dropSprites = new Array<>();
        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
        score = 0;
        increment = 0;
        moveSpeed = 3f;
        dropSpeed = 1f;
        dropRate = 2f;
        // Prepare your application here.
    }

    @Override
    public void resize(int width, int height) {
        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
        // Draw your application here.
    }

    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        // Draw stuff at once

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        bucketSprite.draw(spriteBatch); //  Sprites have their own draw method
        for (Sprite dropSprite: dropSprites){
            dropSprite.draw(spriteBatch);
        }
        spriteBatch.end(); // The SpriteBatch is how libGDX combines these draw calls together.

    }

    private void input(){
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            bucketSprite.translateX(moveSpeed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            bucketSprite.translateX(-moveSpeed * delta);
        }

        if (Gdx.input.isTouched()){
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            bucketSprite.setCenterX(touchPos.x);
        }
    }

    private void logic(){
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float bucketWitdh = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0,worldWidth - bucketWitdh));

        float delta = Gdx.graphics.getDeltaTime();

        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWitdh, bucketHeight);
        for (int i = dropSprites.size - 1; i >= 0; i--){
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-dropSpeed * delta);

            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            if (dropSprite.getY() < -dropHeight){
                dropSprites.removeIndex(i);
            } else if (bucketRectangle.overlaps(dropRectangle)){
                dropSprites.removeIndex(i);
                dropSound.play();
                score += 10;
                increment += 10;
            }
        }

        if (increment % 100 == 0 && increment >= 100){
            dropSpeed += 0.05f;
            dropRate -= 0.05f;
            increment -= 100;
        }


        dropTimer += delta;
        if (dropTimer > dropRate){
            dropTimer = 0;
            createDroplet();
        }
    }

    private void createDroplet(){
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }



    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
