package com.mario.game.creatures;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mario.game.Screens.play_game;

import java.util.HashSet;

public class mushroom {

    private final play_game playGame;

    public Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;
    Mario mario;
    Vector2 coll_mar;

    public int acceleration_G;
    public int velocity_jump;
    public int max_velocity;
    private boolean running_right;
    private boolean DIE;
    public float die_time;
    public final float RATIO;
    public final int width;
    public int height;
    private HashSet<Vector2> bias, bias_ground, bias_bricks, bias_coins, bias_pipes;
    private float[] shape;
    private float timer;

    private Animation<TextureRegion> mushRun;
    private TextureRegion mushDie;
    public TextureRegion region;

    private Texture texture;

    public mushroom (float x, float y, final play_game a, Mario mar){
        playGame = a;
        mario = mar;
        RATIO = playGame.game.ratioY;
        DIE = false;
        running_right = false;
        velocity_jump = (int) ( 201 * RATIO);
        max_velocity = (int) (30 * RATIO);
        acceleration_G = (int) (1000 * RATIO);

        coll_mar = new Vector2();
        bias = new HashSet<Vector2>();
        shape = new float[8];
        doAnimation();

        width = (int) (16 * RATIO);
        height = (int) (16 * RATIO);
        timer = 0;
        die_time = 0;
        position = new Vector2(x, y);
        velocity = new Vector2( -max_velocity, 0);
        acceleration = new Vector2(0, -acceleration_G);
    }


    public void  update (float delta){
        update_velocity(delta);
        collisium();
        collisium_with_mar(delta);
    }

    private void update_velocity (float delta){
        if (DIE){
            region = mushDie;
        } else {
            timer += delta;
            if (timer > 10000) timer = 0;
            velocity.x = running_right ? max_velocity : -max_velocity;
            velocity.y = acceleration.y * delta;

            region = mushRun.getKeyFrame(timer, true);

            if (velocity.y < -velocity_jump) velocity.y = -velocity_jump;

            position.x += velocity.x * delta;
            position.y += velocity.y * delta;
        }

    }



    private void doAnimation(){
        Array<TextureRegion> frames = new Array<TextureRegion>();

        texture = new Texture("enemy/goomba.png");

        frames.add(new TextureRegion(texture, 0, 0, 16, 16));
        frames.add(new TextureRegion(texture, 16, 0, 16, 16));
        mushRun = new Animation<TextureRegion>(0.1f, frames);

        frames.clear();

        mushDie = new TextureRegion(texture, 32, 0, 16, 16);
    }


    private void collisium(){
        get_shape();
        bias.clear();
        bias_ground = playGame.map.grounds.collisium(shape);
        bias_bricks = playGame.map.bricks.collisium(shape);
        bias_pipes = playGame.map.pipes.collisium(shape);
        bias_coins = playGame.map.coins.collisium(shape);
        check_sets();
        check_revers();

        for (Vector2 vec : bias) {
            position.x += vec.x;
            position.y += vec.y;
            vec.rotate90(1).nor();
            if (vec.hasOppositeDirection(velocity)) vec.scl(-1);
            velocity.set(vec.scl(vec.dot(velocity)));
            if (velocity.x == 0) acceleration.x = 0;
        }

    }

    private void collisium_with_mar(float delta){
        if (DIE){
            die_time += delta;
            return;
        }
        coll_mar.set(playGame.map.collisium(shape, mario.shape));
        if (!coll_mar.epsilonEquals(0,0)) {
            if (coll_mar.y > 0) {
                DIE = true;
                mario.velocity.y = velocity_jump / 1.6f;
            } else {
                mario.mario_dead();
            }
        }
    }

    private void get_shape(){

        shape[0] = position.x ;
        shape[1] = position.y;

        shape[2] = position.x + width ;
        shape[3] = position.y;

        shape[4] = position.x + width ;
        shape[5] = position.y + height;

        shape[6] = position.x;
        shape[7] = position.y + height;

        /*shape[0] = position.x ;
        shape[1] = position.y;

        shape[2] = position.x + width ;
        shape[3] = position.y;

        shape[4] = position.x + width ;
        shape[5] = position.y + height / 2f;

        shape[6] = position.x + width / 2f ;
        shape[7] = position.y + height;

        shape[8] = position.x ;
        shape[9] = position.y + height / 2f;*/
    }


    public void check_sets(){
        bias.addAll(bias_ground);
        for (Vector2 vec : bias_bricks){
            for (Vector2 b_vec : bias){
                if (b_vec.isCollinear(vec)) continue;
            }
            bias.add(vec);
        }
        for (Vector2 vec : bias_pipes){
            for (Vector2 b_vec : bias){
                if (b_vec.isCollinear(vec)) continue;
            }
            bias.add(vec);
        }
        for (Vector2 vec : bias_coins){
            for (Vector2 b_vec : bias){
                if (b_vec.isCollinear(vec)) continue;
            }
            bias.add(vec);
        }
    }

    private void check_revers(){
        for (Vector2 vec : bias){
            if (vec.x > 0) {
                running_right = true;
                return;
            } else if (vec.x < 0) {
                running_right = false;
                return;
            }
        }
    }
}
