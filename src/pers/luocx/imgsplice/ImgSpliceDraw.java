package pers.luocx.imgsplice;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pers.luocx.imgsplice.container.Container;


/**
 * Create by LuoChenXu on 2019/3/29
 */
public class ImgSpliceDraw {
    //分页数
    public static final int LIMIT = 100;

    /**
     * 向容器内绘制图片，返回多个图片二进制字节数组
     * @param container
     * @param images
     * @return
     */
    public static List<Container> drawImgs(Container container, List<BufferedImage> images) throws IllegalAccessException, IOException, InstantiationException {
        return drawImgs(container, images, LIMIT);
    }

    public static List<Container> drawImgs(Container container, List<BufferedImage> images, int limit) throws InstantiationException, IllegalAccessException, IOException {
        List<Container> result = null;
        if (images.size() > limit){
            result =  doLimitDraw(container, images, limit);
        }else {
            result =  doDraw(container, images);
        }
        return result;
    }

    private static List<Container> doLimitDraw(Container container, List<BufferedImage> images, int limit) throws InstantiationException, IllegalAccessException {
        List<Container> result = new ArrayList<Container>();
        for (int i = 0; i < images.size() / limit; i ++){
            int start = i * limit;
            int end = (i + 1) * limit - 1;
            end = end > images.size() - 1 ? images.size() -1 : end;
            result.addAll(doDraw(container, new ArrayList<BufferedImage>(images.subList(start,  end))));
        }
        return result;
    }

    /**
     * 将图片绘制到容器
     * @param container
     * @param images
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static List<Container> doDraw(Container container, List<BufferedImage> images) throws IllegalAccessException, InstantiationException {
        List<Container> resultC = new ArrayList<Container>();
        List<BufferedImage> missImgs = null;
        Container temp = container;
        retry:
        for (int i = 0; i < images.size(); i++){
            boolean isLastOne = i + 1 == images.size();
            BufferedImage srcImg = images.get(i);
            //计算空闲区域
            //Region idleR = getIdleRegion(temp.getOccupyRegions(), container, srcImg.getWidth(), srcImg.getHeight(), 0d, 0d);
            Region idleR = getIdleRegionC2(temp.getOccupyRegions(), container, srcImg.getWidth(), srcImg.getHeight(), 0d, 0d, 0d);
            if (idleR != null){
                //System.out.println("idleR: " + idleR.getX() + ", " + idleR.getY());
                BufferedImage boardImg = temp.createBufferImage();
                Graphics2D g2d = boardImg.createGraphics();
                //绘制图片
                g2d.drawImage(srcImg, null, idleR.getX(), idleR.getY());
                //增加容器使用区域
                temp.addOccupyRegion(idleR);
            }else {
                //System.out.println("区域越界: w " + srcImg.getWidth() +  ", h "+ srcImg.getHeight());
                if (missImgs == null){
                    missImgs = new ArrayList<BufferedImage>();
                }
                missImgs.add(srcImg);
            }
            if (isLastOne){
                resultC.add(temp);
                if (missImgs != null){
                    //生成新的临时容器
                    temp = container.getClass().newInstance();
                    images = new ArrayList<BufferedImage>(missImgs);
                    //清除缓存
                    missImgs = null;
                    i = -1;
                    continue retry;
                }
            }
        }
        return resultC;
    }

    /**
     * 获取指定宽高的空闲区域
     * @param width
     * @param height
     * @return
     */
    private static Region getIdleRegion(List<Region> occupyRegions, final Container container, int width, int height, final double offx, final double offy){
        final int contianW = ((int) container.getWidth());
        final int contianH = ((int) container.getHeight());
        double x = offx;
        double y = offy;
        if (occupyRegions != null && occupyRegions.size() > 0){
            for (Region r : occupyRegions){
                double rx = r.getX();
                double ry = r.getY();
                double rw = r.getX() + r.getWidth();
                double rh = r.getY() + r.getHeight();
                double xw = x + width;
                double yh = y + height;
                //检查区域是否重叠
                if ((((x <= rx && xw > rx) || (x >= rx && x < rw) || x <rw && xw > rx ) && ((y <= ry && yh > ry) || (y >= ry && y < rh) || (y < rh && yh > rh)))
                        || (((y <= ry && yh > rh) || (y >= ry && y < rh) || y < rh && yh > ry) && ((x <= rx && xw > rx) || (x >= rx && x < rw) || (x < rw && xw > rx)))) {
                    //x轴重叠
                    if (x >= r.getX() &&  x < rw){
                        x = x + r.getWidth();
                        //x是否越界
                        if (x + width > contianW){
                            x = 0d;
                            y = r.getHeight() + r.getY();
                            if (y + height > contianH){
                                return null;
                            }
                        }
                        return getIdleRegion(occupyRegions, container, width, height, x, y);
                    }else if (y >= r.getY() && y < rh){
                        //Y轴重叠
                        y = r.getHeight() + r.getY();
                        if (y + height > contianH){
                            return null;
                        }
                        return getIdleRegion(occupyRegions, container, width, height, x, y);
                    }
                }
            }
        }
        Region region = new Region();
        region.setX(((int) x));
        region.setY(((int) y));
        region.setWidth(width);
        region.setHeight(height);
        return region;
    }

    private static Region getIdleRegionC2(List<Region> occupyRegions, final Container container, int width, int height, final double offx, final double offy, double minOffY){
        final int contianW = ((int) container.getWidth());
        final int contianH = ((int) container.getHeight());
        double x = offx;
        double y = offy;
        if (occupyRegions != null && occupyRegions.size() > 0){
            for (Region r : occupyRegions){
                double rx = r.getX();
                double ry = r.getY();
                double rw = r.getX() + r.getWidth();
                double rh = r.getY() + r.getHeight();
                double xw = x + width;
                double yh = y + height;
                minOffY = minOffY == 0d ? r.getHeight() : minOffY;
                //检查区域是否重叠
                if ((((x <= rx && xw > rx) || (x >= rx && x < rw) || x <rw && xw > rx ) && ((y <= ry && yh > ry) || (y >= ry && y < rh) || (y < rh && yh > rh)))
                        || (((y <= ry && yh > rh) || (y >= ry && y < rh) || y < rh && yh > ry) && ((x <= rx && xw > rx) || (x >= rx && x < rw) || (x < rw && xw > rx)))) {
                    if (minOffY > r.getHeight()){
                        minOffY = r.getHeight();
                    }
                    //x轴重叠
                    if (x >= r.getX() &&  x < rw){
                        x = x + r.getWidth();
                        //x是否越界
                        if (x + width > contianW){
                            x = 0d;
                            if (rh <= y + height){
                                y = rh;
                            }else{
                                y = minOffY + y;
                            }
                            minOffY = 0d;
                            if (y + height > contianH){
                                return null;
                            }
                        }
                        return getIdleRegionC2(occupyRegions, container, width, height, x, y, minOffY);
                    }else if (y >= r.getY() && y < rh){
                        //Y轴重叠
                        y = r.getHeight() + r.getY();
                        if (y + height > contianH){
                            return null;
                        }
                        return getIdleRegionC2(occupyRegions, container, width, height, x, y, minOffY);
                    }
                }
            }
        }
        Region region = new Region();
        region.setX(((int) x));
        region.setY(((int) y));
        region.setWidth(width);
        region.setHeight(height);
        return region;
    }

}
