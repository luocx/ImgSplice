
import pers.luocx.imgsplice.ImgSpliceDraw;
import pers.luocx.imgsplice.container.A4;
import pers.luocx.imgsplice.container.Container;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    Integer index = 0;
    Integer index2 = 0;
    public static final int[][] FECTS = new int[][]{
            {600, 300} ,{200, 200}, {300, 100}
    };

    public static List<int[]> siteList = new ArrayList<int[]>();

    public static void main(String[]args) throws IllegalAccessException, IOException, InstantiationException {
        File dir = new File("g:/ftp/img");
        if (dir.isDirectory()){
            for (File f : dir.listFiles()){
                f.delete();
            }
        }

        autoImg();
    }

    public static void autoImg() throws IllegalAccessException, IOException, InstantiationException {
        int count = 100;
        for (int i =0; i< count; i ++){
            int[] site = new int[2];
            double r1 = Math.random();
            double r2 = Math.random();
            double r3 = Math.random();
            double r4 = Math.random();
            Double x = 0d;
            Double y = 0d;
            if (r1 < 0.33){
                x = r2 * 100;
            }else if (r1 >=0.33 && r1 < 0.63){
                x = r2 * 500;
            }else{
                x = r2 * 1000 > 700 ? 700 : r2 * 1000;
            }
            if (r3 < 0.33){
                y = r4 * 100;
            }else if (r3 >=0.33 && r3 < 0.63){
                y = r4 * 500;
            }else{
                y = r4 * 1000 > 1000 ? 1000 : r4 * 1000;
            }
            site[0] = (x.intValue() <200? 200 : x.intValue()) * 2;
            site[1] = (y.intValue() < 100? 100 : y.intValue()) * 2;
            //System.out.println("siteList.add(new int[]{"+site[0]+", "+site[1]+"});");
            siteList.add(site);
        }
        //siteList.clear();
        List<BufferedImage> images = new ArrayList<BufferedImage>();
        A4 a4 = new A4();
        A4 newa4 = new A4();
        //拼接
        for (int i = 0; i < siteList.size(); i++){
            int[] s = siteList.get(i);
            //try {
            //    a4.spliceRegion(s[0], s[1]);
            ////生成图片
            //writeToLocal(a4.createImageByte(), "G:/ftp/img/auto.jpg");
            //} catch (Exception e) {
            //    e.printStackTrace();
            //}
            //try {
            //    newa4.spliceRegion2(s[0], s[1]);
            //    writeToLocal(newa4.createImageByte(), "G:/ftp/img/newauto.jpg");
            //} catch (Exception e) {
            //    e.printStackTrace();
            //}
            images.add(createImage(s[0], s[1]));
        }
        long st = System.currentTimeMillis();
        List<Container> containers = ImgSpliceDraw.drawImgs(a4, images);
        BufferedImage img = containers.get(0).createBufferImage();
        long ct = System.currentTimeMillis() - st;

        st = System.currentTimeMillis();
        int i = 0;
        for (Container c : containers){
            i++;
            writeToLocal(((A4) c).createImageByte(), "G:/ftp/img/new_"+ i +".jpg");
        }
        System.out.println("影像数量: "+count+" 计算耗时:" + (ct) + " ms" + " IO耗时:" + (System.currentTimeMillis() - st)+ " ms");
    }

    public void runDemo() throws IllegalAccessException, IOException, InstantiationException {
        //需要拼接的图片数组
        List<BufferedImage> images = new ArrayList<BufferedImage>();

        //声明容器实例，参数： 容器宽度，容器高度，容器背景色
        A4 a4 = new A4(1487d, 2105d, Color.WHITE);
        //拼接绘制，返回容器数组
        List<Container> containers = ImgSpliceDraw.drawImgs(a4, images);
        int i = 0;
        for (Container c : containers){
            BufferedImage image = ((A4)c).createBufferImage();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", bos);
            //输出到本地
            writeToLocal(bos.toByteArray(), "d:/auto"+ (++i) +".jpg");
        }
    }

    public static void writeToLocal(byte[] bytes, String name){
        try {
            File file = new File(name);
            if(file.exists() && file.isFile()){
                file.delete();
            }
            //输出到本地
            OutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage createImage(int width, int height){
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g2d.setComposite(alphaChannel);
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, width, height);
        //设置默认背景色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0 + 2, 0 + 2,width - (2 << 1), height - (2 << 1));
        g2d.setColor(Color.RED);
        g2d.setFont(new Font(null, Font.BOLD, 15));
        g2d.drawString("w :"+width + ", h: "+ height, width /2, height/2);
        g2d.dispose();
        return img;
    }


}
