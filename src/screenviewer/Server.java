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
    public static byte[] imageBuffer;
    public static byte[] imageBytes;

    public static void main(String[] args) {
        try {

            DatagramSocket serverSocket = new DatagramSocket(PORTA);
            byte[] bufferEntrada = new byte[TAM_BUFFER];
            byte[] bufferSaida = new byte[TAM_BUFFER];
            
            ScreenViewer m = new ScreenViewer(imageBuffer);
            m.setVisible(true);
            
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                serverSocket.receive(receivePacket);
                
//                String recebido = new String(receivePacket.getData(), 0, receivePacket.getLength());
                byte recebido[] = receivePacket.getData();
                if(recebido.length == 5 && (recebido[0]== 0xAA && recebido[1]== 0xFF && recebido[2]== 0xFF && recebido[3]== 0xFF && recebido[4]== 0xAA))
                { 
                    if(size>0)
                    {
                        //Gera bytes da imagem
                        imageBytes = new byte[size];
                        for (int i = 0; i < size ; i++) {
                            imageBytes[i] = imageBuffer[i];
                        }
                        m.buffer = decompress(imageBytes);
                        m.repaint();
                    }
                    
                    //start new pack
                    n_pack =0;
                    imageBuffer = new byte[TAM_BUFFER*50000]; 
                    size = 0;
                    
                }
                if(n_pack > 0)
                {
                    int aux = size;
                    for (int i = 0; i < receivePacket.getLength(); i++) {
                        imageBuffer[aux] = recebido[aux++]; 
                    }
                    size += receivePacket.getLength();
                }
                
                n_pack ++;
                
                
                System.out.println("Recebido: " + recebido);;

                InetAddress ipCliente = receivePacket.getAddress(); // Quem me enviou os dados ???
                int portaCliente = receivePacket.getPort();                 // Que porta usou ?

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

               

             

                String mensagem = "Data e hora:" + dtf.format(now) + " Arquivos: " ;
                bufferSaida = mensagem.getBytes();
                
                //Verify if have a request
                if (recebido.equals("get")) {

                    DatagramPacket sendPacket = new DatagramPacket(bufferSaida, bufferSaida.length, ipCliente, portaCliente);
                    serverSocket.send(sendPacket);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    

