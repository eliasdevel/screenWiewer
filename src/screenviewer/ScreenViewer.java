/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screenviewer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import java.io.ByteArrayOutputStream;  
import java.io.IOException;  

import java.util.zip.DataFormatException;  
import java.util.zip.Deflater;  
import java.util.zip.Inflater;  
/**
 *
 * @author wolfi
 */
public class ScreenViewer extends JFrame {

    byte buffer[] = null;
    
    static int xoffset = 0;
    static int yoffset = 0;
    static int xlen = 1920;
    static int ylen = 1080;

    ScreenViewer(byte buffer[]) {
        this.buffer = buffer;
        setSize(xlen, ylen);
        setTitle("Screen viewer");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
//        this.repaint();;
        
        
    }

    @Override
    public void paint(Graphics g) {
        if(buffer != null)
        {
            
            Graphics2D g2 = (Graphics2D) g;
            int scale = 1;       
            int aux = 0;
            for (int i = 0; i < xlen; i++) {
                for (int j = 0; j < ylen; j++) {
                    // porque & 0xFF --> byte -128 a 127 .... 
                    // int i = 255;  byte b = (byte) i; <- -1
                    //r g b a
                    Color cor = new Color(buffer[aux++] & 0xFF, buffer[aux++] & 0xFF, buffer[aux++] & 0xFF, buffer[aux++] & 0xFF);                
                    g2.setColor(cor);
                    g2.drawRect(xoffset + i * scale, yoffset + j * scale, scale, scale);
                }
            }
        }
    }

    public static void main(String[] args) {
        

        try {
//            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//            
//            Robot robot = new Robot();
//            BufferedImage bi = robot.createScreenCapture(new Rectangle(screenSize.width, screenSize.height));// pegar toda a minha tela a resolução é de cada um, pesquisar como saber.., tem 
//            
//            int h = screenSize.height;
//            ylen = h;
//            int w = screenSize.width;
//            xlen = w;
//            int size = (h/10)*(w/10);
//            
//            byte buffer[] = getBlock(bi);
//            
//            byte buffer2[] = compress(buffer);
//                
//            xoffset =10;
//            yoffset =35;         
//            ScreenViewer m = new ScreenViewer(decompress(buffer2));
//            
//           
//            m.setVisible(true);
            
//            while (true) {;
//                bi = robot.createScreenCapture(new Rectangle(screenSize.width, screenSize.height));
//                bi = resize(bi, ylen, xlen);
//                buffer = getBlock(bi);
//            
//                buffer2 = compress(buffer);
//                m.buffer = decompress(buffer2);
//                
//                m.repaint();
//            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Terminou");
    }
    
    
public static byte[] getBlock( BufferedImage bi){
    byte buffer[] = new byte[xlen * ylen * 4];// a r g b  (a=alpha)
    int aux = 0; 
    
    for (int i = 0; i < xlen; i++) {
        for (int j = 0 ; j < ylen; j++) {
            Color cor = new Color(bi.getRGB(i, j)); 
            buffer[aux++] = (byte) cor.getRed();
            buffer[aux++] = (byte) cor.getGreen();
            buffer[aux++] = (byte) cor.getBlue();
            buffer[aux++] = (byte) cor.getAlpha();
        }
    }
    return buffer;
}    


public static byte[] compress(byte[] data) throws IOException {  
   Deflater deflater = new Deflater();  
   deflater.setInput(data);  
   ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);   
   deflater.finish();  
   byte[] buffer = new byte[1024];   
   while (!deflater.finished()) {  
        int count = deflater.deflate(buffer); // returns the generated code... index  
        outputStream.write(buffer, 0, count);   
   }  
   outputStream.close();  
   byte[] output = outputStream.toByteArray();  
   System.out.println("Original: " + data.length / 1024 + " Kb");  
   System.out.println("Compressed: " + output.length / 1024 + " Kb");  
   return output;  
}  

private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_FAST);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
}

public static byte[] decompress(byte[] data) throws IOException, DataFormatException {  
    Inflater inflater = new Inflater();   
    inflater.setInput(data);  
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
    byte[] buffer = new byte[1024];  
    while (!inflater.finished()) {  
        int count = inflater.inflate(buffer);  
        outputStream.write(buffer, 0, count);  
    }  
    outputStream.close();  
    byte[] output = outputStream.toByteArray();  
//    System.out.println("Original: " + data.length);  
//    System.out.println("Compressed: " + output.length);  
    return output;  
  }  
}
