import java.io.*;
import java.net.*;

import java.security.MessageDigest; // HASH Code

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;

import javax.imageio.ImageIO;

import java.awt.Image;
import javax.swing.*;

// P2P ��ſ��� �۽� / ��ȣȭ / ���� update / Block ���� ��Ȱ�� ��.

public class Kcoinclient {                      
	
    private Frame mainFrame;
    private Label headerLabel;
    private Label statusLabel;
    private Label from_wallet;
    private Label to_wallet;
    private Label coin;
    
    private Panel controlPanel;
    
    private static Block[] block= new Block[100000000];
    private static transaction tran_info;
    
    private static String hostname;
    private static int port;
       
    public Kcoinclient() {                 // �����ڿ��� �ʱ�ȭ ��Ŵ
        
    	prepareGUI();
       
    	// ���α׷� ���Ǹ� ���� Block�� �� ���� �����Ѵ�. 
        // ������ ���Ͽ� Block������ ���� ������ �ý��� �޸𸮿��� ���� Block ������ �ʿ��ϴ�.
        // ��ü Block�� ��� �޸𸮷� ������ ���� �� ���ʿ� �ϴ�.          
    }
   
        
	public static void main(String[] args) {  
	
		   // Block �ʱ�ȭ �ʿ�/�ð� ���� �ʿ�/ ������ ��� Block �� ������ �ϳ�?? No!!! ���� Block ��
				
		     Kcoinclient awtControlDemo = new Kcoinclient();
             awtControlDemo.showButton();
             
		     //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		     // Make a connection to other Peer to broadcast 
		  	
		     if (args.length < 2) return;
	         hostname = args[0];	   
	         port = Integer.parseInt(args[1]);	      
	         
	         //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	         // SHA-256 ��ȣȭ��.  Base�� ��ȣȭ �� �� �ִ� �� ��
 	         
	         String Acount = "aaa";
	         try{
	 
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] hash = digest.digest(Acount.getBytes("UTF-8"));
	            StringBuffer hexString = new StringBuffer();
	 
	            for (int i = 0; i < hash.length; i++) {
	                String hex = Integer.toHexString(0xff & hash[i]);
	                if(hex.length() == 1) hexString.append('0');
	                hexString.append(hex);
	            }
	 
	            System.out.println(hexString.toString());
	 
	        } catch(Exception ex){
	            throw new RuntimeException(ex);
	        }       	     
	         
	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	        
	        //Time ��ũ�� �ʱ�ȭ �� 	         	         	         
	         
	        int current_state = 0 ; // 0�� garship state 1�� ���� state
	        String current_minute ="00" ;  //�ʱ�ȭ  
	        
	        //�ε����� ��ũ �ʱ�ȭ ��
	        int index = 0 ;    // ���α׷� ���� ��, ���° Block ���� Ȯ�� �Ѵ�.
	        // ������ Block�� �о� ���� �� �ε����� Ȯ���� ���Ѵ�. ������ Block�� ���Ͽ� �ִ�.
	        // �о� �� Block�� �ٸ� �ð��� ���� �ʴٸ� �ٸ� ������� �� �ؾ� �Ѵ�.
	        // ���⼭�� Block���� �о� �԰� �о� �� ����� 0 �̶�� �����Ѵ�.
	        	      
	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        // ��ܿ� ��� �ʱ�ȭ�� �Ϸ� �Ѵ�.
	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        // Block ���� �˰����� ����( �ŷ� ������ �����Ͽ� ���� Block�� ������Ʈ / 13�� ���� �ð��� �����Ͽ� Broadcast / 14�п� ������ Block�� ������)		        
	      
	        
	        while (true) {      //���� �Ҵ��� �ִ��� ���δ�.	        	
	        	
	        	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        	//�ð� Ȯ��
	        	
	        	Date date = new Date();   //while �� ������ ���;� �Ѵ�. �ȱ׷��� �ð� update�� �ȵȴ�.
	        	DateFormat df = new SimpleDateFormat("mm");  // �и� �̾� ����.	 	    
	        	// DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (z Z)");
	        	TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
	 	        df.setTimeZone(time);	

	 	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        	//1. �ŷ� ������ �ǽð����� �����Ͽ� ���� Block�� update 	
	 	        //2. 13�� ���� �ð��� ������� �ŷ������� update�Ͽ� Broadcast
	 	        //3. Transaction ����� ���� Reset ��.(�� ������)
	 	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	        		 	        
	 	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 	 	         	     	 	        
	 	        
	 	        if ( df.format(date).equals("03") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // �ŷ� ���� ���� �ð�
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               		       
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //��ü�� �ݵ�� ���� �ش� �ȱ׷��� Null Ponit �߻�
		        		             		        		           
		        		         System.out.println("���° ����:" + index);
		        		                 		        		        		     
		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  //1���� Block�� �Ҵ��ϰ� �ϳ��� Reset ��.
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list0[i];    // - ���� ��  + ���� ��                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // ����ϱ�   	      
		         	                 //block�� Time stamps �߰�
		        	                 
		        	                 // Kcoinp2pserver.tran_list0[i].reset();  // java �� �ٸ��� ���� �Ұ�
		        		   	     }
		        		   	     
		        		   	     
		        		         block_out.writeObject(block[index]);    
		        		         block_out.flush();           // flush
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	        
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("��� ��忡 Block ����");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }
	 	   	 		 	        
	 	       if ( df.format(date).equals("13") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // �ŷ� ���� ���� �ð�
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               		       
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //��ü�� �ݵ�� ���� �ش� �ȱ׷��� Null Ponit �߻�
		        		             		        		           
		        		         System.out.println("���° ����:" + index);
		        		         
		        		        	    
		        		   	     for (int i = 0; i < 1000; i++) {  //1���� Block�� �Ҵ��ϰ� �ϳ��� Reset ��.		        		   	    	 
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list1[i];    // - ���� ��  + ���� ��                            	      
		        	                 //Kcoinp2pserver.tran_list1[i].reset(); // java�� �ٸ��� ���� �Ұ�   
		        	                 		             
		         		             
		        		   	     } 		        		   	  		        		           		        		         
		        		        		 
		        		         block_out.writeObject(block[index]);
		        		       
		        		         block_out.flush();           // flush	         
		        		                 	         
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("��� ��忡 Block ����");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }
	 	        
	 	        
	 	       if ( df.format(date).equals("23") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // �ŷ� ���� ���� �ð�
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //��ü�� �ݵ�� ���� �ش� �ȱ׷��� Null Ponit �߻�
		        		             		        		           
		        		         System.out.println("���° ����:" + index);
		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  //1���� Block�� �Ҵ��ϰ� �ϳ��� Reset ��.
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list2[i];    // - ���� ��  + ���� ��                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // ����ϱ�   	      
		         		             
		        	                 //Kcoinp2pserver.tran_list2[i].reset();
		        	                  
		        		   	     } 		        		   	  		        		           		        		         
		        		        		 
		        		         block_out.writeObject(block[index]);
		        		       		        		         
		        		         block_out.flush();           // flush	         
		        		                 	         
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("��� ��忡 Block ����");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }
	 	         
	 	       
	 	       if ( df.format(date).equals("33") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // �ŷ� ���� ���� �ð�
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //��ü�� �ݵ�� ���� �ش� �ȱ׷��� Null Ponit �߻�
		        		             		        		           
		        		         System.out.println("���° ����:" + index);
		        		        
		        		        		        		     
		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  //1���� Block�� �Ҵ��ϰ� �ϳ��� Reset ��.
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list3[i];    // - ���� ��  + ���� ��                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // ����ϱ�   	      
		         		             
		        	                 //Kcoinp2pserver.tran_list3[i].reset();       
		        		   	     } 		        		   	  		        		           		        		         
		        		        		 
		        		         block_out.writeObject(block[index]);
		        		       		         
		        		         block_out.flush();           // flush	         
		        		                 	         
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("��� ��忡 Block ����");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        } 
	 	        
	 	       
	 	       if ( df.format(date).equals("43") )
		        {	
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // �ŷ� ���� ���� �ð�
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //��ü�� �ݵ�� ���� �ش� �ȱ׷��� Null Ponit �߻�
		        		             		        		           
		        		         System.out.println("���° ����:" + index);
		        		       
		        		   	     for (int i = 0; i < 1000; i++) {  //1���� Block�� �Ҵ��ϰ� �ϳ��� Reset ��.
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list4[i];    // - ���� ��  + ���� ��                                                           	      
		        	                 //Kcoinp2pserver.tran_list4[i].reset();		         		             		        	                 		        		   	   
		        		   	     }
		        	                                            		        	        
		        		         block_out.writeObject(block[index]);		        		         
		        		         block_out.flush();           // flush	         
		        		                 	         
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        		         
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("��� ��忡 Block ����");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		           }          
		        } 	        
	 	        
	 	       if ( df.format(date).equals("53") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // �ŷ� ���� ���� �ð�
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //��ü�� �ݵ�� ���� �ش� �ȱ׷��� Null Ponit �߻�
		        		             		        		           
		        		         System.out.println("���° ����:" + index);
		        		         		        		        		        		     		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  //1���� Block�� �Ҵ��ϰ� �ϳ��� Reset ��.
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list5[i];    // - ���� ��  + ���� ��                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // ����ϱ�   	      
		        	                 //Kcoinp2pserver.tran_list5[i].reset();
		        		   	     } 		        		   	  		        		           		        		         
		        		        		 
		        		         block_out.writeObject(block[index]);
		        		       
		        		         block_out.flush();           // flush	         
		        		                 	         
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	         
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("��� ��忡 Block ����");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }
	 	        	 	       
	 	    	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        	//3. 14�� ���� �ð��� ����ҿ� ���� �ʿ�
	 	       
	 	        current_minute = df.format(date);      // �ð� ������Ʈ
	 	       
	 	        //break;       	        
	            
	        }   //while �� ����    
	}           // Main method ����
	
	
	   private void prepareGUI() {
	        // Frame �� ���� ����
	        mainFrame = new Frame("Achive");
	        mainFrame.setSize(400, 400);
	        mainFrame.setLayout(new GridLayout(3, 1));
	        mainFrame.addWindowListener(new WindowAdapter() {
	            public void windowClosing(WindowEvent windowEvent) {
	                System.exit(0);
	            }
	        });
	        
	        Toolkit toolkit = mainFrame.getToolkit();
	        Image image = toolkit.createImage("C:\\Users\\yu\\eclipse-workspace\\1.png");
	        mainFrame.setIconImage(image);

	        // ��ܿ� �ִ� ��
	        headerLabel = new Label();
	        headerLabel.setAlignment(Label.CENTER);
	        headerLabel.setText("Coin Transaction");
	       
	        // �ϴ� ���°� ��
	        statusLabel = new Label();
	        statusLabel.setText("Status Lable");
	        statusLabel.setAlignment(Label.CENTER);
	        statusLabel.setSize(350, 100);
	 
	        controlPanel = new Panel();
	        controlPanel.setLayout(new FlowLayout());
	 
	        mainFrame.add(headerLabel);
	        mainFrame.add(controlPanel);
	        mainFrame.add(statusLabel);
	        
	        mainFrame.setVisible(true);
	    }
	   
	   private void showButton() {
		   
	        Button btnOk = new Button("����Ȯ��");
	        Button btnSubmit = new Button("Submit");
	        Button btnCancel = new Button("Cancel");
	         
	        TextField t1,t2, t3;  
	        t1=new TextField("");  
	        t1.setBounds (0,0, 200,30); 
	        t1.setColumns(19);
	        t2=new TextField("");  
	        t2.setBounds (0,0, 200,30); 
	        t2.setColumns(19);
	        t3=new TextField("");  
	        t3.setBounds (50,100, 200,30);  
	        t3.setColumns(16);	        
	        
	        /*
	        x - the new x-coordinate of this component
	        y - the new y-coordinate of this component
	        width - the new width of this component
	        height - the new height of this component
	        */
	        
	        from_wallet = new Label();
	        //from_wallet.setAlignment(Label.CENTER);
	        from_wallet.setText("���� ����");
	        
	        to_wallet = new Label();
	        //to_wallet.setAlignment(Label.CENTER);
	        to_wallet.setText("��� ����");
	        
	        coin = new Label();
	        //coin.setAlignment(Label.CENTER);
	        coin.setText("Coin");
	        
	        btnOk.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                statusLabel.setText("���°� Ȯ�� �Ǿ����ϴ�");
	            }
	        });
	             	        	        
	        btnSubmit.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                   
	                tran_info = new transaction();
	                
	                tran_info.setwalletfrom(Integer.parseInt(t1.getText()));  //t1�� Text ���� �ް� �ٽ� int�� ��ȯ
	                tran_info.setwalletto(Integer.parseInt(t2.getText()));
	                tran_info.setamount(Integer.parseInt(t3.getText())); 
	                
        	        try (Socket socket = new Socket(hostname, port)) {
	   	 	        		         	               
        	        	ObjectOutputStream tran_out = new ObjectOutputStream(socket.getOutputStream());	         		
       		       	 	        
        	        	Date date1 = new Date();   //while �� ������ ���;� �Ѵ�. �ȱ׷��� �ð� update�� �ȵȴ�. 	    
        	        	DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (z Z)");
        	        	TimeZone time1 = TimeZone.getTimeZone("Asia/Seoul");
        	        	df1.setTimeZone(time1);	
 	        		
        	        	tran_info.settimestamps(df1.format(date1));
       		           		        
        	        	System.out.println(tran_info.valueOftimestamps());
        	        	
        	        	System.out.println("transaction ��ü ����");
        	        	       	        	
        	        	tran_out.writeObject(tran_info);
       		       
        	        	tran_out.flush();           // flush	         
       		         	        	         
       		         } catch (UnknownHostException ex) {
       		            System.out.println("Server not found: " + ex.getMessage());
       		         } catch (IOException ex) {
       		            System.out.println("I/O error: " + ex.getMessage());
       		         }
       	       	            	        
       	             System.out.println("");
	        		 System.out.println("�ŷ� ���� ����");
	        	     System.out.println("");
	        	     
	                 try {
	                	 statusLabel.setText("���� ��....");  
	                     Thread.sleep(3000);
	                      
	                  } catch (InterruptedException ie) {
	                      ie.printStackTrace();
	                  }
	        	     
	        	     statusLabel.setText("������ �Ϸ� �Ǿ����ϴ�");  
	        	     
	            }
	        });
	          
	        controlPanel.add(t1);
	        controlPanel.add(from_wallet);
	        controlPanel.add(t2);
	        controlPanel.add(to_wallet);
	        controlPanel.add(t3);
	        controlPanel.add(coin);
	        controlPanel.add(btnSubmit);	        
	        mainFrame.setVisible(true);        
	   }	   
}


class Block implements Serializable{         // ����ȭ ������ ���ؼ� �ʿ���.
	
	  private int index ;                    // �� ��° Block ���� Ȯ��
	  
	  private String pre_hash;               // pre block�� ���� Hash ��
	  
	  private String this_hash;              // �� ������ ���� �ؽ�         
	  
	  private String timestamps;             // ���� �ð�
	  
	  private int wallet[] = new int [3000];      // ���� 30000�� ����
	  
	  public transaction[] tran_list = new transaction[1000];    // 10���� �ŷ� ������ ����. ������ ������ ������ Null point �߻�
	  
	  public Block() {
		  
      index = 0;
      pre_hash ="";
      this_hash="";
      timestamps="";

  	  for (int i = 0; i < wallet.length; i++) {
  		           wallet[i] = -i;                       // - ���� ��  + ���� ��                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // ����ϱ�   	      
  		       } 
	  
  	  for (int i = 0; i < 1000 ; i++) {
	               tran_list[i] = new transaction();                       // - ���� ��  + ���� ��                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // ����ϱ�   	      
	           } 
      }
	  
	  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	  //���� Ȯ�� ����
	  
	  public int valueOfindex(){
		     return index;
      }
	  	  
	  public String valueOfpre_prehash(){
		    return pre_hash;
      }
	  
	  public String valueOfthis_thishash(){
		     return this_hash;
      }
	  
	  public String valueOftimestamps(){
		     return timestamps;
      }
	   
   	  public int valueOfwallet(int w){
		     return wallet[w];
      }
   	  
      //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   	  //���� setting ����
   	  
   	  public void setindex(int a){
		     index= a; 
		     }
	  
	  public void setprehash(){
		     pre_hash= "";
             }
	  
	  public void setthishash(){
		     this_hash= "";
      }
	  
	  public void settimestamps(String a){
		     timestamps= a;
      }
	  
	  public void setwallet(int w){
		     wallet[w] = 0 ;
      }
	  
	  public void tran_a_to_b(int a, int b , int c){     //���� ��ü
		     wallet[a] = wallet[a] - c ;
		     wallet[b] = wallet[b] + c ;
      }
	  
	  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	  //�� ���� ����
}


class transaction implements Serializable{         // ����ȭ ������ ���ؼ� �ʿ���.
		
	  private int wallet_from;               //����!!static ������ ���� ��ü�� �Ⱥ��� ����.
	  
	  private int wallet_to;
	  
	  private int amount; 
	 
	  private String timestamps;
	  
	  private String pre_hash;               // pre block�� ���� Hash ��
	  
	  private String this_hash;              // �� ������ ���� �ؽ�         
	  	  
	  public transaction() {
		  
		     wallet_from = 0;
		     wallet_to = 0; 
		     amount = 0;
		     timestamps="";  
             pre_hash ="";
             this_hash="";
      }
	 
	  public void reset(){		  
		     wallet_from = 0;
		     wallet_to = 0; 
		     amount = 0;
		     timestamps="";  
             pre_hash ="";
             this_hash="";
      }

	  
	  public void setwalletfrom(int a){
		     wallet_from = a;
      }
	  
	  public void setwalletto(int a){
		     wallet_to = a;
      }
	  
	  public void setamount(int a){
		     amount = a;
      }
	  
	  public void settimestamps(String a){
		     timestamps = a ;
      }
	  
	  public void setprehash(String a){
		     pre_hash= a ;
      }
	  
	  public void setthishash(String a){
		     this_hash = a ;
      }
	  
	  public int valueOfwalletfrom(){
		     return wallet_from ;
      }
	  
	  public int valueOfwalletto(){
		     return wallet_to ;
      }
	  
	  public int valueOfamount(){
		     return amount ;
      }
	  
	  public String valueOftimestamps(){
		     return timestamps ;
      } 
	  
	  public String valueOfprehash(){
		     return pre_hash ;
      }
	  
	  public String valueOfthishash(){
		     return this_hash ;
      }
}