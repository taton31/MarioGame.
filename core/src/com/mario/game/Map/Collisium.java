package com.mario.game.Map;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mario.game.Screens.play_game;

import java.util.HashSet;

public abstract class Collisium {

    play_game PlayGame;
    public TiledMap tiledMap;
    protected TiledMapRenderer tiledMapRenderer;
    TiledMapTileLayer layer;
    MapObjects ENDLVL;


    protected HashSet<Vector2> set;
    private Vector2 RESULT, result;
    private int i, j;
    private Vector2 proj_first_point1, proj_first_point2, proj_second_point1, proj_second_point2;
    private Vector2 temporary_point, proj_firsec_point1, proj_firsec_point2;
    private Vector2 [] vec;
    protected float RATIO;

    Collisium (float rat){
        RATIO = rat;
        set = new HashSet<Vector2>();

        RESULT = new Vector2(111,111);
        result = new Vector2(111,111);

        proj_first_point1 = new Vector2();
        proj_first_point2 = new Vector2();
        proj_second_point1 = new Vector2();
        proj_second_point2 = new Vector2();
        temporary_point = new Vector2();
        proj_firsec_point1 = new Vector2();
        proj_firsec_point2 = new Vector2();

        vec = new Vector2[4];
        vec[0] = new Vector2();
        vec[1] = new Vector2();
        vec[2] = new Vector2();
        vec[3] = new Vector2();
    }

    public Vector2 collisium(float[] body_first, float[] body_second){
        RESULT.set(-111,111);
        result.set(0,0);
        set.clear();
        j=0;
        for (i = 0; i < (body_first.length / 2); ++i){
            temporary_point.x = body_first[(2 * (i + 1)) % body_first.length] - body_first[2 * i];
            temporary_point.y = body_first[(2 * (i + 1) + 1) % body_first.length] - body_first[2 * i + 1];

            temporary_point.rotate90(1).nor();
            if (!(contanes(temporary_point, set))) {
                if (j < 4){
                    vec[j].set(temporary_point);
                    set.add(vec[j]);
                    ++j;
                } else set.add(temporary_point.cpy());

            }

        }

        for (i = 0; i < (body_second.length / 2); ++i){
            temporary_point.x = body_second[(2 * (i + 1)) % body_second.length] - body_second[2 * i];
            temporary_point.y = body_second[(2 * (i + 1) + 1) % body_second.length] - body_second[2 * i + 1];
            temporary_point.rotate90(1).nor();
            //set.add( (Math.abs(temporary_point.angle()) % 180));
            if (!(contanes(temporary_point, set))) {
                if (j < 4){
                    vec[j].set(temporary_point);
                    set.add(vec[j]);
                    ++j;
                } else set.add(temporary_point.cpy());
            }
        }

        for (Vector2 vector : set){
            search_projection(body_first, vector, proj_first_point1, proj_first_point2);
            search_projection(body_second, vector, proj_second_point1, proj_second_point2);
            if (cross_projections(proj_first_point1, proj_first_point2, proj_second_point1, proj_second_point2, result)){
                return result;
            }
            if (result.len2() > 0.000000001f && RESULT.len() > result.len()) {
                RESULT.set(result);
            }
        }
        if (RESULT.epsilonEquals(111,111)){
            RESULT.set(0,0);
        }
        return RESULT;
    }


    private void search_projection(float[] body, Vector2 vector, Vector2 start, Vector2 end){
        start.set(111, 111);
        end.set(111, 111);
        int length = body.length;
        vector.nor();
        for (j = 0; j < (length / 2); ++j) {
            if (vector.x != 0) {
                temporary_point.y = vector.y * (body[2 * j] * vector.x + body[2 * j + 1] * vector.y);
                temporary_point.x = (temporary_point.y - body[2 * j + 1]) * (-vector.y / vector.x) + body[2 * j];
            } else {
                temporary_point.x = 0;
                temporary_point.y = body[2 * j + 1];
            }

            if (start.epsilonEquals(111, 111)) {
                start.set(temporary_point);
                continue;
            } else if (end.epsilonEquals(111, 111)) {
                end.set(temporary_point);
                continue;
            } else if (start.x != end.x) {
                if (temporary_point.x < start.x && temporary_point.x < end.x) {
                    if (start.x < end.x) {
                        start.set(temporary_point);
                    } else {
                        end.set(temporary_point);
                    }
                } else if (temporary_point.x > start.x && temporary_point.x > end.x) {
                    if (start.x > end.x) {
                        start.set(temporary_point);
                    } else {
                        end.set(temporary_point);
                    }
                }
            } else {
                if (temporary_point.y < start.y && temporary_point.y < end.y) {
                    if (start.y < end.y) {
                        start.set(temporary_point);
                    } else {
                        end.set(temporary_point);
                    }
                } else if (temporary_point.y > start.y && temporary_point.y > end.y) {
                    if (start.y > end.y) {
                        start.set(temporary_point);
                    } else {
                        end.set(temporary_point);
                    }
                }
            }
        }
    }

    private boolean cross_projections(Vector2 start_first, Vector2 end_first, Vector2 start_second, Vector2 end_second, Vector2 result){

        if (start_first.x != end_first.x) {
            if (start_first.x <= start_second.x && start_first.x <= end_second.x && end_first.x <= start_second.x && end_first.x <= end_second.x) {
                result.set(0, 0);
                return true;
            }
            else if (start_first.x >= start_second.x && start_first.x >= end_second.x && end_first.x >= start_second.x && end_first.x >= end_second.x) {
                result.set(0, 0);
                return true;
            }
        } else  {
            if (start_first.y <= start_second.y && start_first.y <= end_second.y && end_first.y <= start_second.y && end_first.y <= end_second.y) {
                result.set(0, 0);
                return true;
            }
            else if (start_first.y >= start_second.y && start_first.y >= end_second.y && end_first.y >= start_second.y && end_first.y >= end_second.y) {
                result.set(0, 0);
                return true;
            }
        }

        if (start_first.x != end_first.x){
            if (start_first.x < end_first.x){
                if (start_second.x < end_first.x && start_second.x > start_first.x){
                    proj_firsec_point1.set(start_second);
                } else {
                    proj_firsec_point1.set(end_second);
                }
            } else {
                if (start_second.x < start_first.x && start_second.x > end_first.x){
                    proj_firsec_point1.set(start_second);
                } else {
                    proj_firsec_point1.set(end_second);
                }
            }

            if (start_second.x < end_second.x){
                if (start_first.x < end_second.x && start_first.x > start_second.x){
                    proj_firsec_point2.set(start_first);
                } else {
                    proj_firsec_point2.set(end_first);
                }
            } else {
                if (start_first.x < start_second.x && start_first.x > end_second.x){
                    proj_firsec_point2.set(start_first);
                } else {
                    proj_firsec_point2.set(end_first);
                }
            }
        } else {
            if (start_first.y < end_first.y){
                if (start_second.y < end_first.y && start_second.y > start_first.y){
                    proj_firsec_point1.set(start_second);
                } else {
                    proj_firsec_point1.set(end_second);
                }
            } else {
                if (start_second.y < start_first.y && start_second.y > end_first.y){
                    proj_firsec_point1.set(start_second);
                } else {
                    proj_firsec_point1.set(end_second);
                }
            }

            if (start_second.y < end_second.y){
                if (start_first.y < end_second.y && start_first.y > start_second.y){
                    proj_firsec_point2.set(start_first);
                } else {
                    proj_firsec_point2.set(end_first);
                }
            } else {
                if (start_first.y < start_second.y && start_first.y > end_second.y){
                    proj_firsec_point2.set(start_first);
                } else {
                    proj_firsec_point2.set(end_first);
                }
            }
        }
        result.set(proj_firsec_point2.x - proj_firsec_point1.x, proj_firsec_point2.y - proj_firsec_point1.y);
        return false;
    }

    private boolean contanes (Vector2 a, HashSet<Vector2> set){
        for (Vector2 vec : set){
            if (vec.angle() % 180 == a.angle() % 180) return true;
        }
        return  false;
    }


    void get_rectangle(MapObject cell, float[] ground_rect){
        float x;
        ground_rect[0] = x = ((RectangleMapObject) cell).getRectangle().getX() * RATIO;
        float y;
        ground_rect[1] = y = ((RectangleMapObject) cell).getRectangle().getY() * RATIO;
        float width = ((RectangleMapObject) cell).getRectangle().getWidth() * RATIO;
        float height = ((RectangleMapObject) cell).getRectangle().getHeight() * RATIO;
        ground_rect[2] = x + width;
        ground_rect[3] = y;
        ground_rect[4] = x + width;
        ground_rect[5] = y + height;
        ground_rect[6] = x;
        ground_rect[7] = y + height;
    }

    void delete_tile (int a, int b){
        layer.getCell(a, b).setTile(null);
        ((OrthoCachedTiledMapRenderer) tiledMapRenderer).invalidateCache();
    }

    void bump_tile (int a, int b){
        layer.getCell(a, b).getTile().setOffsetY(55);
        layer.getCell(a, b).getTile().setOffsetY(0);
        //layer.getCell(a,b).setTile(layer.getCell(a, b).getTile().getTextureRegion())
        //tile.setOffsetY(77);
        //tile.setOffsetY(-2);
        //layer.getCell(a, b).setTile(tile);
        //layer.getCell(a, b).getTile().setOffsetY(j++);
        ((OrthoCachedTiledMapRenderer) tiledMapRenderer).invalidateCache();
    }


    public Vector2 collisium_goomb(float[] body_first, float[] body_second){
        RESULT.set(-111,111);
        result.set(0,0);
        set.clear();
        j=0;
        for (i = 0; i < (body_first.length / 2); ++i){
            temporary_point.x = body_first[(2 * (i + 1)) % body_first.length] - body_first[2 * i];
            temporary_point.y = body_first[(2 * (i + 1) + 1) % body_first.length] - body_first[2 * i + 1];

            temporary_point.rotate90(1).nor();
            if (!(contanes(temporary_point, set))) {
                if (j < 4){
                    vec[j].set(temporary_point);
                    set.add(vec[j]);
                    ++j;
                } else set.add(temporary_point.cpy());

            }

        }

        for (i = 0; i < (body_second.length / 2); ++i){
            temporary_point.x = body_second[(2 * (i + 1)) % body_second.length] - body_second[2 * i];
            temporary_point.y = body_second[(2 * (i + 1) + 1) % body_second.length] - body_second[2 * i + 1];
            temporary_point.rotate90(1).nor();
            //set.add( (Math.abs(temporary_point.angle()) % 180));
            if (!(contanes(temporary_point, set))) {
                if (j < 4){
                    vec[j].set(temporary_point);
                    set.add(vec[j]);
                    ++j;
                } else set.add(temporary_point.cpy());
            }
        }

        for (Vector2 vector : set){
            search_projection(body_first, vector, proj_first_point1, proj_first_point2);
            search_projection(body_second, vector, proj_second_point1, proj_second_point2);
            if (cross_projections(proj_first_point1, proj_first_point2, proj_second_point1, proj_second_point2, result)){
                return result;
            }
            if (result.len2() > 0.000000001f && RESULT.len() > result.len() && Math.abs(result.x) > 3 * RATIO) {
                RESULT.set(result);
            }
        }
        if (RESULT.epsilonEquals(111,111)){
            RESULT.set(0,0);
        }
        return RESULT;
    }
}
