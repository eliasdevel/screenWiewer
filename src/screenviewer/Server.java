/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screenviewer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import static screenviewer.ScreenViewer.decompress;

public class Server {

    public static final int PORTA = 5000;
    public static final int TAM_BUFFER = 40960;
    public static int n_pack = 0;
    public static int size = 0;
    public static byte[][] imageBuffer;
    public static byte[] imageBytes;
    public static int timeout = 10000;
    public static void main(String[] args) {
        try {

            DatagramSocket serverSocket = new DatagramSocket(PORTA);
            byte[] bufferEntrada = new byte[TAM_BUFFER];
            byte[] bufferSaida = new byte[TAM_BUFFER];
            
            ScreenViewer m = new ScreenViewer(null);
            m.setVisible(true);
            DatagramPacket receivePacket = new DatagramPacket(bufferEntrada, bufferEntrada.length);
            
            while (true) {
                
                serverSocket.receive(receivePacket);
                
//                String recebido = new String(receivePacket.getData(), 0, receivePacket.getLength());
                byte recebido[] = receivePacket.getData();
             
                
                if(receivePacket.getLength() == 5 && 
                        ((recebido[0] & 0xFF) == 0xAA &&
                        (recebido[1] & 0xFF) == 0xFF && (recebido[2] & 0xFF)== 0xFF && 
                        (recebido[3] & 0xFF)== 0xFF && (recebido[4] & 0xFF)== 0xAA)){ 
                    
                    System.out.println("New sequence");
                    if(size>0){
//                        System.out.println(size);
                        //Gera bytes da imagem
                        imageBytes = new byte[((n_pack-1)*40960)+imageBuffer[n_pack-1].length];
                        int x =0;

                        for (int i = 0; i < n_pack; i++) {
   
                            for (int j = 0; j < imageBuffer[i].length; j++) {
                                imageBytes[x++] = imageBuffer[i][j];
                            }
                        }
                        
//                        System.out.println(size);
//                        System.out.println(imageBytes.length);
//                        m.buffer = decompress(imageBytes);
                        m.buffer = imageBytes;
                        
                        if(m.buffer != null)
                        {
                            m.repaint();
                        }
                        
                    }
                    
                    //start new pack
                    n_pack =0;
                    imageBuffer = new byte[254][40960]; 
                    size = 0;
                    
                }
                
                if(n_pack > 0)
                {
//                    System.out.println(receivePacket.getLength());
                    int aux = 0;
//                    System.out.println(recebido[0]);;
                    imageBuffer[recebido[0]& 0xFF] = new byte[receivePacket.getLength()];
                    for (int i = 1; i < receivePacket.getLength(); i++) {
                        imageBuffer[recebido[0]& 0xFF][aux++] = (byte) (recebido[i] & 0xFF);
                    }
                    size += receivePacket.getLength()-1;          
                    
                }        
                n_pack ++;
                
                
                InetAddress ipCliente = receivePacket.getAddress(); // Quem me enviou os dados ???
                int portaCliente = receivePacket.getPort();                 // Que porta usou ?
                
                bufferSaida = "receipt".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(bufferSaida, bufferSaida.length, ipCliente, portaCliente);
                serverSocket.send(sendPacket);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        try {
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
        catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
        
  } 
}
    

