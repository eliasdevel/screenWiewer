/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screenviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.zip.Deflater;
import static screenviewer.ScreenViewer.xlen;

public class Client {

    public static final int PORTA = 5000;
    public static final int TAM_BUFFER = 40960;

    public static void main(String[] args) {
        try {
            Robot robot = new Robot();
            byte[] bufferEntrada = new byte[TAM_BUFFER];
            byte[] bufferSaida = new byte[TAM_BUFFER];

            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IpServidor = InetAddress.getByName("127.0.0.1");

//            String sentence = "get";
//            bufferSaida = sentence.getBytes();
//            DatagramPacket sendPacket = new DatagramPacket(bufferSaida, bufferSaida.length, IpServidor, PORTA);
//            clientSocket.send(sendPacket);
            
            BufferedImage bi;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            byte[] buffer;
            DatagramPacket sendPacket;
            boolean sending = false;
            byte number =0;
            while (true) {
                
                bi = robot.createScreenCapture(new Rectangle(screenSize.width, screenSize.height));
                buffer = getBlock(bi,screenSize);
                byte block = 0;
//                System.out.println(buffer.length
                buffer = compress(buffer);
                System.out.println("b Length" + buffer.length);
                
                //Header
                if(!sending)
                {
                    System.out.println("start header");
                    bufferSaida = new byte[5];
                    bufferSaida[0] = (byte) 0xAA;
                    bufferSaida[1] = (byte) 0xFF;
                    bufferSaida[2] = (byte) 0xFF;
                    bufferSaida[3] = (byte) 0xFF;
                    bufferSaida[4] = (byte) 0xAA;

                    sendPacket = new DatagramPacket(bufferSaida, bufferSaida.length, IpServidor, PORTA);
                    clientSocket.send(sendPacket);
                    
                }
                
                
                DatagramPacket receivePacket = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                clientSocket.receive(receivePacket);

                String respostaServidor = new String(receivePacket.getData(),0,receivePacket.getLength());
            
                if(buffer.length > 40960 && respostaServidor.equals("receipt"))
                {
                    System.out.println("Start generate package");
                    bufferSaida = new byte[40960];
                    
                    int i =0;
                    int aux =0;
                    
                    while(i< buffer.length)
                    {
                        
                        sending = true;
                        
                        bufferSaida[aux++] = buffer[i]; 
                        
                        if(aux == 40959)
                        {
//                            System.out.println("Envindo");;
                            byte[] snd = build(bufferSaida, block++);
                            
                            sendPacket = new DatagramPacket(snd, snd.length, IpServidor, PORTA);
                            clientSocket.send(sendPacket); 
                           
                            receivePacket = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                            clientSocket.receive(receivePacket);
                            respostaServidor = new String(receivePacket.getData(),0,receivePacket.getLength());

                           if((buffer.length-i) >40960){
                               bufferSaida = new byte[40960];
                           }else
                           {
                               System.out.println("resto");
                               bufferSaida = new byte[buffer.length-(i+1)];
                           }
                            
                           aux =0; 
                        }
                        i++;     
                    }
     
                    byte[] snd = build(bufferSaida, block++);
                    
                    sendPacket = new DatagramPacket(snd, snd.length, IpServidor, PORTA);
                    clientSocket.send(sendPacket);
                    
                    receivePacket = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                    clientSocket.receive(receivePacket);
                    respostaServidor = new String(receivePacket.getData(),0,receivePacket.getLength());
                              
                    sending =  false;                    
                }
                Thread.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] build(byte[] data,byte index)
    {
        byte[] r  = new byte[data.length+1];
        r[0] = index;
        for (int i = 1; i < data.length+1; i++) {
            r[i] = (byte)(data[i-1] & 0xFF);
        }
        return r;
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
//        System.out.println("Original: " + data.length / 1024 + " Kb");;;
//        System.out.println("Compressed: " + output.length / 1024 + " Kb");  
        return output;  
    }
    
    public static byte[] getBlock( BufferedImage bi, Dimension d){
    byte buffer[] = new byte[d.width * d.height * 4];// a r g b  (a=alpha)
    int aux = 0; 
    
    for (int i = 0; i < d.width; i++) {
        for (int j = 0 ; j < d.height; j++) {
            Color cor = new Color(bi.getRGB(i, j)); 
            buffer[aux++] = (byte) cor.getRed();
            buffer[aux++] = (byte) cor.getGreen();
            buffer[aux++] = (byte) cor.getBlue();
            buffer[aux++] = (byte) cor.getAlpha();
        }
    }
    return buffer;
} 
}