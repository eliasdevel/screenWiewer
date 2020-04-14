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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

 import java.io.ByteArrayOutputStream;  

 import java.io.IOException;  
import java.util.logging.Level;
import java.util.logging.Logger;

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

    ScreenViewer(byte buffer[]) {
        this.buffer = buffer;
        setSize(320, 200);
        setTitle("Mostrador");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        final int BLOCK_X = 50;
        final int BLOCK_Y = 50;
        Graphics2D g2 = (Graphics2D) g;

        int scale = 1;
        
        int aux = 0;
        for (int i = 0; i < BLOCK_X; i++) {
            for (int j = 0; j < BLOCK_Y; j++) {
                // porque & 0xFF --> byte -128 a 127 .... 
                // int i = 255;  byte b = (byte) i; <- -1
                //r g b a
                Color cor = new Color(buffer[aux++] & 0xFF, buffer[aux++] & 0xFF, buffer[aux++] & 0xFF, buffer[aux++] & 0xFF);                
                g2.setColor(cor);
                g2.drawRect(xoffset + i * scale, yoffset + j * scale, scale, scale);
            }
        }
    }

    public static void main(String[] args) {
        

        try {
            
            Robot robot = new Robot();
            BufferedImage bi = robot.createScreenCapture(new Rectangle(1920, 1080));// pegar toda a minha tela a resolução é de cada um, pesquisar como saber.., tem 
            
            byte buffer[] = getBlock(bi,50,0,1);
            
            
            xoffset =10;
            yoffset =35;         
            ScreenViewer m = new ScreenViewer(buffer);
            xoffset+=50;
             
            
            m.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Terminou");
    }
    
    
public static byte[] getBlock( BufferedImage bi, int size,int offsetx, int offsety){
    byte buffer[] = new byte[size * size * 4]; // a r g b  (a=alpha)
    int aux = 0; 
    for (int i = offsetx*size; i < (size*(offsetx+1)); i++) {
        for (int j = offsety*size ; j < (size*(offsety+1)); j++) {

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
      System.out.println("Original: " + data.length);  
   System.out.println("Compressed: " + output.length);  
   return output;  
  }  

}
