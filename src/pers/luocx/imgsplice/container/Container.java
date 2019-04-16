package pers.luocx.imgsplice.container;

import pers.luocx.imgsplice.Region;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 容器
 */
public abstract class Container {
    protected List<Region> occupyRegions = new ArrayList<Region>();

    protected BufferedImage bufferedImage = null;

    public abstract double getHeight();

    public abstract double getWidth();

    public abstract Color getBackground();

    /**
     * 创建容器的图像对象
     * @return
     */
    public BufferedImage createBufferImage(){
        if (bufferedImage == null){
            synchronized (this){
                if (bufferedImage == null){
                    bufferedImage = new BufferedImage(((int) getWidth()), (int)getHeight(), BufferedImage.TYPE_INT_RGB);
                    if (getBackground() != null){
                        Graphics2D g2d = bufferedImage.createGraphics();
                        g2d.setBackground(getBackground());
                        g2d.setColor(getBackground());
                        g2d.fillRect(0, 0, ((int) getWidth()), ((int) getHeight()));
                        g2d.dispose();
                    }
                }
            }
        }
        return bufferedImage;
    }

    public void addOccupyRegion(Region r){
        occupyRegions.add(r);
    }

    public List<Region> getOccupyRegions(){
        return occupyRegions;
    }

}
