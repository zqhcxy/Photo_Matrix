package com.example.photo_matrix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.photo_matrix.util.PicMatrixUtil;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int BLUR = 0;// 高斯模糊
    private static final int OLDREMEBER = 1;// 怀旧
    private static final int SHARPEN = 2;// 锐化
    private static final int SUNSHINE = 3;// 光照
    private static final int NEGATIVE = 4;// 底片
    private static final int SKETCH = 5;// 素描
    private static final int EMBOSS = 6;// 浮雕
    private static final int REVERSE = 7;// 翻转
    private static final int BLACKWHITE = 8;// 黑白
    private static final int SOFTGLOWFILTER = 9;// 柔化美白
    private static final int BLUR_NEW = 10;//模糊

    private ImageView pic_sc;
    private ImageView pic_matrix;// 变化后的图片
    private RecyclerView matrix_menu;// 底部编辑图片菜单
    private ArrayList<String> menulist;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

    }

    private void initView() {
        pic_sc = (ImageView) findViewById(R.id.pic_sc);
        pic_matrix = (ImageView) findViewById(R.id.pic_matrix);
        matrix_menu = (RecyclerView) findViewById(R.id.matrix_menu);
    }

    private void initData() {
        menulist = new ArrayList<String>();
        menulist.add("高斯模糊");
        menulist.add("怀旧效果");
        menulist.add("锐化效果");
        menulist.add("光照效果");
        menulist.add("底片效果");
        menulist.add("素描效果");
        menulist.add("浮雕效果");
        menulist.add("翻转效果");
        menulist.add("黑白效果");
        menulist.add("柔化美白");
        menulist.add("模糊效果");

        bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.b1);

        pic_sc.setImageBitmap(bitmap);

        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        matrix_menu.setLayoutManager(layoutManager);
        RecyclerAdapter adapter = new RecyclerAdapter(menulist);
        matrix_menu.setAdapter(adapter);
        adapter.setOnItemOnClickLitener(new OnItemClickLitener() {

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onItemClick(View view, int position) {
                setPicMatrix(position);
            }
        });
    }

    @SuppressLint("NewApi")
    private void setPicMatrix(int whitch) {
        switch (whitch) {
            case BLUR:// 高斯模糊（柔化）
                Bitmap blur_bm = PicMatrixUtil.blurImageAmeliorate(bitmap);
                pic_matrix.setImageBitmap(blur_bm);
                break;
            case OLDREMEBER:// 怀旧
                Bitmap oldRemeber_bm = PicMatrixUtil.oldRemeber(bitmap);
                pic_matrix.setImageBitmap(oldRemeber_bm);
                break;
            case SHARPEN:// 锐化
                Bitmap sharpen_bm = PicMatrixUtil.sharpenImageAmeliorate(bitmap);
                pic_matrix.setImageBitmap(sharpen_bm);
                break;
            case SUNSHINE:// 光照
                Bitmap sunsine_bm = PicMatrixUtil.sunshine(bitmap,
                        bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                pic_matrix.setImageBitmap(sunsine_bm);
                break;
            case NEGATIVE:// 底片
                Bitmap natigative_bm = PicMatrixUtil
                        .negativeImageAmeliorate(bitmap);
                pic_matrix.setImageBitmap(natigative_bm);
                break;
            case SKETCH:// 素描
                Bitmap sketch_bm = PicMatrixUtil.sketch(bitmap);
                pic_matrix.setImageBitmap(sketch_bm);
                break;
            case EMBOSS:// 浮雕
                Bitmap emboss_bm = PicMatrixUtil.emboss(bitmap);
                pic_matrix.setImageBitmap(emboss_bm);
                break;
            case REVERSE:// 翻转
                Bitmap reverse_bm = PicMatrixUtil.reverseBitmap(bitmap, 0);
                pic_matrix.setImageBitmap(reverse_bm);
                break;
            case BLACKWHITE:// 黑白
                Bitmap blackwhite_bm = PicMatrixUtil.blackWhite(bitmap);
                pic_matrix.setImageBitmap(blackwhite_bm);
                break;
            case SOFTGLOWFILTER:// 柔化美白
                Bitmap softglowfilter_bm = PicMatrixUtil.SoftGlowFilter(bitmap, 10,
                        0.1f, 0.1f);
                pic_matrix.setImageBitmap(softglowfilter_bm);
                break;
            case BLUR_NEW://模糊
                Bitmap BLUR_NEW_bm = PicMatrixUtil.blur(MainActivity.this, bitmap);
                if(BLUR_NEW_bm!=null) {
                    pic_matrix.setImageBitmap(BLUR_NEW_bm);
                }
                break;
            default:
                break;
        }

    }

    // 对外界开放的回调接口因为Recy控件没有提供点击事件
    public interface OnItemClickLitener {
        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }
}
