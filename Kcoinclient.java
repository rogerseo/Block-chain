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


//What is Client

//1. Send transaction of my Node
//2. Create  Block of this Node
//3. Send Block of this Node
//4. Compare the Blocks and make decision  


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
       

    public Kcoinclient() {               
        
    	prepareGUI(); 
    	         
    }
   
	public static void main(String[] args) {  
	
		     				
		     Kcoinclient awtControlDemo = new Kcoinclient();
             awtControlDemo.showButton();
             
		     //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		     // Make a connection to other Peer to broadcast 
		  	
		     if (args.length < 2) return;
	         hostname = args[0];	   
	         port = Integer.parseInt(args[1]);	      
	         
	         //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	         // SHA-256 encryption
 	         
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
	        //Time sync 	         	         	         
	         
	        int current_state = 0 ; // 0=normal   1= make block state
	        
	        String current_minute ="00" ; 
	        
	 
	        int index = 0 ;
	        
	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        // recieve transaction and save at the block / make decision at 13minute and Broadcast / Save block at 14 minute)		        
	      	        
	        
	        while (true) {    
	        	
	        	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        
	        	
	        	
	        	Date date = new Date();
	        	DateFormat df = new SimpleDateFormat("mm");  	 	    
	        	
	        	TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
	 	        df.setTimeZone(time);	

	 	        //block time stamps
   	 	        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (z Z)");
	        	TimeZone time1 = TimeZone.getTimeZone("Asia/Seoul");
	 	        df1.setTimeZone(time1);	

	 	        // System.out.println(df1.format(date));

	 	        
                //String a =df1.format(date) ;              
                //String b = a.substring(14,16);              
                //System.out.println(b);
                
	 	        
	 	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        	//1. recieve transaction and save at the Block 	
	 	        //2. save transaction and broadcast
	 	        //3. Transaction reset
	 	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	        		 	        
	 	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 	 	         	     	 	        
	 	        
	 	        
	 	        if ( df.format(date).equals("03") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // broadcast time
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               		       
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();          
		        		          		         
		        		         System.out.println("몇번째 블락:" + index);
		        		         
		        		         block[index].settimestamps(df1.format(date));
		        		         
		        		         for (int i = 0; i < 1000; i++) {  //Reset
		        		   	    	 
		        	                 
		        		        	 block[index].tran_list[i] =  Kcoinp2pserver.tran_list0[i];
		        	                 
		        	                 
		        		         }
		        		         
		        		         Kcoinp2pserver.tran_reset0 = true;
		        		       
		        		         block_out.writeObject(block[index]);    
		        		         block_out.flush();           
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	        
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }

	 	        
	 	       if ( df.format(date).equals("13") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1; 
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               		       
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();       
		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		         block[index].settimestamps(df1.format(date));
		        		        	    
		        		   	     for (int i = 0; i < 1000; i++) {  	        		   	    	 
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list1[i];                                	      
		        	                 //Kcoinp2pserver.tran_list1[i].reset();    
		        	                 		             		         		             
		        		   	     } 		        		   	  		        		           		        		         
		        		        		 
		        		   	     Kcoinp2pserver.tran_reset1 = true;
		        		   	     
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
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }
	 	        	 	        
	 	       if ( df.format(date).equals("23") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            
		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		         
		        		         block[index].settimestamps(df1.format(date));
		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list2[i];      	      
		         		             
		        	                		        	                  
		        		   	     } 		        		   	  		        		           		        		         
		        		        		 
		        		   	     Kcoinp2pserver.tran_reset2 = true;
		        		   	   
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
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }
	 	         	 	       
	 	       if ( df.format(date).equals("33") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    
		        		
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		        
		        		         block[index].settimestamps(df1.format(date));
		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list3[i];     	      
		         		             
		        	                        
		        		   	     } 		        		   	  		        		           		        		         
		        		        	
		        		   	     Kcoinp2pserver.tran_reset3 = true;
		        		   	   
		        		   	   
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
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        } 
	 	        
	 	       
	 	       if ( df.format(date).equals("43") )
		        {	
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		       
		        		         
		        		         block[index].settimestamps(df1.format(date));
		        		         
		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list4[i];                                                            	      
		        	                 		         		             		        	                 		        		   	   
		        		   	     }
		        	                 
		        		   	     Kcoinp2pserver.tran_reset4 = true;
		        		   	   
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
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		           }          
		        } 	        
	 	        
	 	
	 	       if ( df.format(date).equals("53") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;   
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		         
		        		         block[index].settimestamps(df1.format(date));
		        		         
		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list5[i];     	      
		        	                 //Kcoinp2pserver.tran_list5[i].reset();
		        		   	     } 		        		   	  		        		           		        		         
		        		        	
		        		   	     Kcoinp2pserver.tran_reset5 = true;
		        		   	   
		        		   	     
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
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }
	 	        	 	       
	 	    	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        	//3. save at 14 minute
	 	       
	 	        current_minute = df.format(date);      
	 	        
	 	        //break;       	        
	            
	        }   //    
	}           // Main
	
	
	   private void prepareGUI() {
	        
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

	        
	        headerLabel = new Label();
	        headerLabel.setAlignment(Label.CENTER);
	        headerLabel.setText("Coin Transaction");
	       
	        
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
		   
	        Button btnOk = new Button("계좌확인");
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
	        
	        
	        from_wallet = new Label();
	        //from_wallet.setAlignment(Label.CENTER);
	        from_wallet.setText("나의 지갑");
	        
	        to_wallet = new Label();
	        //to_wallet.setAlignment(Label.CENTER);
	        to_wallet.setText("상대 지갑");
	        
	        coin = new Label();
	        //coin.setAlignment(Label.CENTER);
	        coin.setText("Coin");
	        
	        btnOk.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                statusLabel.setText("계좌가 확인 되었습니다");
	            }
	        });
	             	        	        
	        btnSubmit.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                   
	                tran_info = new transaction();
	                
	                tran_info.setwalletfrom(Integer.parseInt(t1.getText()));  
	                tran_info.setwalletto(Integer.parseInt(t2.getText()));
	                tran_info.setamount(Integer.parseInt(t3.getText())); 
	                
        	        try (Socket socket = new Socket(hostname, port)) {
	   	 	        		         	               
        	        	ObjectOutputStream tran_out = new ObjectOutputStream(socket.getOutputStream());	         		
       		       	 	        
        	        	Date date1 = new Date();    	    
        	        	DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (z Z)");
        	        	TimeZone time1 = TimeZone.getTimeZone("Asia/Seoul");
        	        	df1.setTimeZone(time1);	
 	        		
        	        	tran_info.settimestamps(df1.format(date1));
       		           		        
        	        	System.out.println(tran_info.valueOftimestamps());
        	        	
        	        	System.out.println("transaction 객체 전송");
        	        	       	        	
        	        	tran_out.writeObject(tran_info);
       		       
        	        	tran_out.flush();           // flush	         
       		         	        	         
       		         } catch (UnknownHostException ex) {
       		            System.out.println("Server not found: " + ex.getMessage());
       		         } catch (IOException ex) {
       		            System.out.println("I/O error: " + ex.getMessage());
       		         }
       	       	            	        
       	             System.out.println("");
	        		 System.out.println("거래 내용 전송");
	        	     System.out.println("");
	        	     
	                 try {
	                	 statusLabel.setText("전송 중....");  
	                     Thread.sleep(3000);
	                      
	                  } catch (InterruptedException ie) {
	                      ie.printStackTrace();
	                  }
	        	     
	        	     statusLabel.setText("전송이 완료 되었습니다");  
	        	     
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







class wallet implements Serializable {
		
      private int ID; 
      private int amount;
      

      public wallet() {		
    	     ID = 0;
		     amount = 0;
	  }
	  
      public int set_amount (int a){        // Set money at the wallet 
		     amount = a  ;
		     return amount;
      }
	  
	  public int add_amount (int a){        // Add money at the wallet 
		     amount = amount + a  ;
		     return amount;
      }
	  
      public int minus_amount (int a){      // Minus money at the wallet
     	     amount = amount - a  ;
	         return amount;
      }	     	  
}


class Block implements Serializable{         // This is a recording DATA that everyone save.
	
	  private int index ;                    // Index of Block.
	  
	  private String pre_hash;              	  
	  private String this_hash;                      
	  
	  private String timestamps;             
	  	   
	  public wallet[] wallet_list = new wallet[3000];
	  
	  public transaction[] tran_list = new transaction[1000];    
	  
	  public Block() {
		  
      index = 0;
      pre_hash ="";
      this_hash="";
      timestamps="";

  	  //for (int i = 0; i < wallet.length; i++) {
  	  //	           wallet[i] = -i;                       	      
  	  //	       } 
	  
  	 
  	  for (int i = 0; i < 3000 ; i++) {
          wallet_list[i] = new wallet();                       	      
      } 
  	  
  	  
  	  for (int i = 0; i < 1000 ; i++) {
	               tran_list[i] = new transaction();            	      
	           } 
      }
	  
	  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	  //
	  
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
	   
   	  //public int valueOfwallet(int w){
   	  //	     return wallet[w];
      // }
   	     	  
      //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   	  //   	  
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
	  
	  //public void setwallet(int w){
		//     wallet[w] = 0 ;
      //}
	  
	  //public void tran_a_to_b(int a, int b , int c){     
	  	  //     wallet[a] = wallet[a] - c ;
		 //    wallet[b] = wallet[b] + c ;
      //}
	  
	  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	  //값 전송 가능
}


class transaction implements Serializable{   // 
		
	  private int wallet_from;               //
	  
	  private int wallet_to;
	  
	  private int amount; 
	 
	  private String timestamps;
	  
	  private String pre_hash;               //
	  
	  private String this_hash;              //         
	  	  
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