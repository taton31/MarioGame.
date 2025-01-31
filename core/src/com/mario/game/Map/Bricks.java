package com.mario.game.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mario.game.creatures.Mario.Mario;
import com.mario.game.creatures.enemy.Goomba;
import com.mario.game.creatures.enemy.Turtle;

import java.util.HashSet;
import java.util.Random;

public class Bricks extends MapObject_{
// элементы брикс могут быть только кубиками 1 на 1!!!!!!!!!!!!!!!!!!!!!!

    private final float tile_size;


    Bricks(Map ma, OrthographicCamera cam, Mario mar){
        super(ma, cam, mar);


        tile_size = 16 * map.PlayGame.game.ratioY;
        objects = map.tiledMap.getLayers().get("bricks").getObjects();
        length = objects.getCount();
        rectangle_object = new float[8];
        temporary = new Vector2(0,0);
        set = new HashSet<Vector2>();
        temporary_arr = new Vector2[2];
        temporary_arr[0] = new Vector2();
        temporary_arr[1] = new Vector2(0,0);

        mapObjects = new Array<MapObjects_rectangles>();
        get_mapObjects_rectangle();
    }

    public HashSet<Vector2> collisium (float [] rectangle, boolean check_all_world){
        set.clear();
        temporary_arr[0].set(0,0);
        temporary_arr[1].set(0,0);
        //do_crash(mapObjects);
        for (int i = 0 ;i < mapObjects.size; i++){
            if (!check_all_world && !check_camera(mapObjects.get(i).rectangle)) continue;
            temporary.set(map.collisium(mapObjects.get(i).rectangle, rectangle));

            if (temporary.epsilonEquals(0,0)) continue;

            if( check_direction(temporary, mapObjects.get(i).rectangle) ) continue;

            if (set.isEmpty()) {
                set.add(temporary_arr[0].set(temporary));
                check_crash(temporary, i);
                continue;
            }

            if (has_collinear(temporary)) continue;

            if (temporary_arr[1].epsilonEquals(0,0)) {
                set.add(temporary_arr[1].set(temporary));
                check_crash(temporary, i);
            }
            else {
                set.add(temporary.cpy());
                check_crash(temporary, i);
                break;
            }
        }

        return set;
    }




    public boolean check_camera (float [] a){
        if ( a[0] < camera.position.x && a[2] > camera.position.x ) return true;
        for (int k = 0; k < (a.length / 2); ++k){
            if ( a[2 * k] > camera.position.x - Gdx.app.getGraphics().getWidth() / 2 && a[2 * k] < camera.position.x + Gdx.app.getGraphics().getWidth() / 2) return true;
        }
        return false;
    }

    private boolean check_direction(Vector2 vec, float [] rectangle){
        float proj_vec_x = Math.signum(vec.x);
        float proj_vec_y = Math.signum(vec.y);
        float x, y;

        if (proj_vec_x != 0) {
            x = rectangle[0] + tile_size / 2 + tile_size * proj_vec_x;
            y = rectangle[1] + tile_size / 2;
            if (check_input_tile(x, y)) return true;
        }

        if (proj_vec_y != 0) {
            x = rectangle[0] + tile_size / 2;
            y = rectangle[1] + tile_size / 2 + tile_size * proj_vec_y;
            return check_input_tile(x, y);
        }

        return false;
    }

    private boolean check_input_tile(float x, float y){
        for (MapObjects_rectangles obj_rec : mapObjects){
            if (!check_camera(obj_rec.rectangle)) continue;
            if (x > obj_rec.rectangle[0] && x < obj_rec.rectangle[2] && y > obj_rec.rectangle[1] && y < obj_rec.rectangle[7]) {
                return true;
            }
        }
        for (MapObjects_rectangles obj_rec : map.grounds.mapObjects){
            if (!check_camera(obj_rec.rectangle)) continue;
            if (x > obj_rec.rectangle[0] && x < obj_rec.rectangle[2] && y > obj_rec.rectangle[1] && y < obj_rec.rectangle[7]) {
                return true;
            }
        }
        for (MapObjects_rectangles obj_rec : map.coins.mapObjects){
            if (!check_camera(obj_rec.rectangle)) continue;
            if (x > obj_rec.rectangle[0] && x < obj_rec.rectangle[2] && y > obj_rec.rectangle[1] && y < obj_rec.rectangle[7]) {
                return true;
            }
        }
        return false;
    }
    void check_crash (Vector2 temp, int k){
        if (temp.x == 0 && temp.y < 0){

            for (Goomba goomba : map.getGoomdas()){
                if (mapObjects.get(k).rectangle[0] < goomba.position.x + goomba.width / 2f && mapObjects.get(k).rectangle[2] > goomba.position.x + goomba.width / 2f &&
                        mapObjects.get(k).rectangle[7] < goomba.position.y + goomba.height / 2f && mapObjects.get(k).rectangle[7] + tile_size > goomba.position.y + goomba.height / 2f){
                    goomba.mushDie.flip(false, true);
                    goomba.DIE = true;
                    goomba.get_shape_zero();
                    goomba.velocity.set(Math.signum(goomba.random.nextInt(1000) - 500) * (goomba.random.nextInt(150) + 150), 650);
                }
            }

            for (Turtle turtle : map.turtles_array){
                if (mapObjects.get(k).rectangle[0] < turtle.position.x + turtle.width / 2f && mapObjects.get(k).rectangle[2] > turtle.position.x + turtle.width / 2f &&
                        mapObjects.get(k).rectangle[7] < turtle.position.y + turtle.height / 2f && mapObjects.get(k).rectangle[7] + tile_size > turtle.position.y + turtle.height / 2f){
                    turtle.turtleDie.flip(false, true);
                    turtle.DIE = true;
                    turtle.get_shape_zero();
                    turtle.velocity.set(Math.signum(turtle.random.nextInt(1000) - 500) * (turtle.random.nextInt(150) + 150), 650);
                }
            }

            if (mario.isMarioBig()){
                mario.getBreakblock().play(mario.getPlayGame().game.MUS_ON / 100f);
                map.delete_tile((int) ((mapObjects.get(k).rectangle[0] + 2) / tile_size), (int) (mapObjects.get(k).rectangle[1] / tile_size));
                mapObjects.removeIndex(k);

            } else {
                mario.getBump().play(mario.getPlayGame().game.MUS_ON / 100f);
                //map.bump_tile((int) (mapObjects.get(k).rectangle[0] / tile_size), (int) (mapObjects.get(k).rectangle[1] / tile_size));
            }

            //objects.remove(k-1);

            //layer.getCell(3,7).setRotation(33);//((int)((RectangleMapObject) objects.get(k)).getRectangle().getX(), (int)((RectangleMapObject) objects.get(k)).getRectangle().getY());
            //map.tiledMap.getLayers().get("grounds").getC

        }
    }



}
