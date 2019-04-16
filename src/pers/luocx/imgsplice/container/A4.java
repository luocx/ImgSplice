package pers.luocx.imgsplice.container;

import pers.luocx.imgsplice.Region;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Create by LuoChenXu on 2019/3/29
 */
public class A4 extends Container {
    private static double HEIGTH = 2105d;

    private static double WIDTH = 1487d;
    //背景色
    private static Color backgroundColor = Color.WHITE;

    private static int DPI = 120;

    //y偏移量
    Double minOffY = 0d;

    public A4(){
        this(WIDTH, HEIGTH, backgroundColor);
    }

    /**
     * A4容器构造方法
     * @param width 容器宽度
     * @param height 容器高度
     * @param backgroundColor 容器背景色
     */
    public A4(double width, double height, Color backgroundColor){
        this.WIDTH = width;
        this.HEIGTH = height;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public double getHeight() {
        return HEIGTH;
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public Color getBackground() {
        return backgroundColor;
    }



    public List<Region> getAllOccRegion(){
        return this.occupyRegions;
    }

    public byte[] createImageByte(){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", bos);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
